package com.prostuti.server.csv

import com.prostuti.core.model.CsvRowError
import com.prostuti.core.model.Difficulty
import com.prostuti.core.model.ImportSummary
import com.prostuti.core.model.Option
import com.prostuti.core.model.QuestionType
import com.prostuti.core.model.Subject
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import java.io.Reader

/**
 * Parses + validates CSVs against docs/backend_design.md §7. Two templates:
 *
 *   - question-bank:    exam_session, subject, topic, question_text, option_a..d, correct_option, explanation, difficulty
 *   - practice:         subject, topic, question_text, option_a..d, correct_option, explanation, difficulty
 *
 * Validation is per-row and non-fatal — the import response carries both
 * counts so the admin can see exactly what landed vs. what was rejected.
 * A header-row mismatch (whole file wrong) IS fatal — returns an error
 * shape the route translates to HTTP 400.
 *
 * Apache Commons CSV is used instead of manual splitting — question_text
 * routinely contains commas and quoted fields, see SKILL.md §6.
 */
object CsvImporter {

    private const val MAX_QUESTION_LENGTH = 5000
    private const val MAX_OPTION_LENGTH = 1000

    /** Header order MUST match the template in backend_design.md §7. */
    private val HEADER_BANK = listOf(
        "exam_session", "subject", "topic", "question_text",
        "option_a", "option_b", "option_c", "option_d",
        "correct_option", "explanation", "difficulty",
    )
    private val HEADER_PRACTICE = listOf(
        "subject", "topic", "question_text",
        "option_a", "option_b", "option_c", "option_d",
        "correct_option", "explanation", "difficulty",
    )

    /**
     * Header-mismatch result. Routes convert this to HTTP 400 with the
     * message as `{"error": "..."}`.
     */
    sealed interface ParseOutcome {
        data class Failed(val message: String) : ParseOutcome
        data class Ok(
            val parsed: List<ParsedRow>,
            val rejected: List<CsvRowError>,
        ) : ParseOutcome
    }

    /** Per-row parse result. Carries the question type so the service can insert correctly. */
    sealed interface ParsedRow {
        val subject: Subject
        val topic: String?
        val questionText: String
        val optionA: String
        val optionB: String
        val optionC: String
        val optionD: String
        val correctOption: Option
        val explanation: String?
        val difficulty: Difficulty?

        data class Bank(
            val examSession: String,
            override val subject: Subject,
            override val topic: String?,
            override val questionText: String,
            override val optionA: String,
            override val optionB: String,
            override val optionC: String,
            override val optionD: String,
            override val correctOption: Option,
            override val explanation: String?,
            override val difficulty: Difficulty?,
        ) : ParsedRow

        data class Practice(
            override val subject: Subject,
            override val topic: String?,
            override val questionText: String,
            override val optionA: String,
            override val optionB: String,
            override val optionC: String,
            override val optionD: String,
            override val correctOption: Option,
            override val explanation: String?,
            override val difficulty: Difficulty?,
        ) : ParsedRow
    }

    fun parseQuestionBank(reader: Reader): ParseOutcome =
        parse(reader, HEADER_BANK, requireExamSession = true, type = QuestionType.BANK)

    fun parsePractice(reader: Reader): ParseOutcome =
        parse(reader, HEADER_PRACTICE, requireExamSession = false, type = QuestionType.PRACTICE)

    /** Convert the successful outcome to the public ImportSummary DTO. */
    fun toSummary(outcome: ParseOutcome.Ok): ImportSummary =
        ImportSummary(imported = outcome.parsed.size, rejected = outcome.rejected)

    private fun parse(
        reader: Reader,
        expectedHeader: List<String>,
        requireExamSession: Boolean,
        type: QuestionType,
    ): ParseOutcome {
        // EXCEL format = comma delimiter + header row, matches §7 examples.
        val format = CSVFormat.EXCEL.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreEmptyLines(true)
            .setTrim(true)
            .build()

        val parser = CSVParser(reader, format)
        val header = parser.headerNames.map { it.trim() }

        if (header != expectedHeader) {
            parser.close()
            return ParseOutcome.Failed(
                "header mismatch: expected [${expectedHeader.joinToString(",")}] " +
                    "got [${header.joinToString(",")}]"
            )
        }

        val parsed = mutableListOf<ParsedRow>()
        val rejected = mutableListOf<CsvRowError>()

        for (record in parser) {
            if (isBlankRow(record)) continue
            // CSVRecord.recordNumber is 1-based including the header row, so
            // the first data row is 2 — that's the number admins see in Excel.
            val rowNum = record.recordNumber

            validateRow(record, rowNum.toInt(), requireExamSession, parsed, rejected, type)
        }
        parser.close()

        return ParseOutcome.Ok(parsed = parsed, rejected = rejected)
    }

    private fun validateRow(
        record: CSVRecord,
        rowNum: Int,
        requireExamSession: Boolean,
        parsed: MutableList<ParsedRow>,
        rejected: MutableList<CsvRowError>,
        type: QuestionType,
    ) {
        val errors = mutableListOf<String>()

        // exam_session only exists in HEADER_BANK; record.get() throws on a
        // missing header, so we guard with isMapped() before reading it.
        val examSession = if (record.isMapped("exam_session")) {
            val raw = record.get("exam_session")
            when {
                raw.isNullOrBlank() -> if (requireExamSession) { errors += "exam_session required"; null } else null
                else -> raw
            }
        } else if (requireExamSession) {
            errors += "exam_session required"
            null
        } else {
            null
        }

        val subjectRaw = record.get("subject").orEmptyNull()
        val subject = parseEnum(subjectRaw, Subject.entries, "subject", errors)

        val topic = record.get("topic").orEmptyNull()?.takeIf { it.isNotBlank() }

        val questionText = record.get("question_text").orEmptyNull()
        if (questionText.isNullOrBlank()) errors += "question_text required"
        else if (questionText.length > MAX_QUESTION_LENGTH) errors += "question_text > $MAX_QUESTION_LENGTH chars"

        val a = record.get("option_a").orEmptyNull()
        val b = record.get("option_b").orEmptyNull()
        val c = record.get("option_c").orEmptyNull()
        val d = record.get("option_d").orEmptyNull()
        listOf("option_a" to a, "option_b" to b, "option_c" to c, "option_d" to d).forEach { (n, v) ->
            if (v.isNullOrBlank()) errors += "$n required"
            else if (v.length > MAX_OPTION_LENGTH) errors += "$n > $MAX_OPTION_LENGTH chars"
        }

        val correctRaw = record.get("correct_option").orEmptyNull()
        val correct = parseEnum(correctRaw, Option.entries, "correct_option", errors)

        val explanation = record.get("explanation").orEmptyNull()?.takeIf { it.isNotBlank() }

        val difficultyRaw = record.get("difficulty").orEmptyNull()
        val difficulty = if (difficultyRaw.isNullOrBlank()) null
            else parseEnum(difficultyRaw, Difficulty.entries, "difficulty", errors)

        if (errors.isNotEmpty()) {
            rejected += CsvRowError(row = rowNum, message = errors.joinToString("; "))
            return
        }

        // All validations passed — assemble the typed row.
        val row = when (type) {
            QuestionType.BANK -> ParsedRow.Bank(
                examSession = examSession!!,
                subject = subject!!,
                topic = topic,
                questionText = questionText!!,
                optionA = a!!, optionB = b!!, optionC = c!!, optionD = d!!,
                correctOption = correct!!,
                explanation = explanation,
                difficulty = difficulty,
            )
            QuestionType.PRACTICE -> ParsedRow.Practice(
                subject = subject!!,
                topic = topic,
                questionText = questionText!!,
                optionA = a!!, optionB = b!!, optionC = c!!, optionD = d!!,
                correctOption = correct!!,
                explanation = explanation,
                difficulty = difficulty,
            )
        }
        parsed += row
    }

    private inline fun <reified E : Enum<E>> parseEnum(
        raw: String?,
        entries: List<E>,
        field: String,
        errors: MutableList<String>,
    ): E? {
        if (raw.isNullOrBlank()) {
            errors += "$field required"
            return null
        }
        // Subject + Difficulty + Option are case-sensitive per §7
        // ("must exactly match one of the 9 enum values"). We try raw first,
        // then a single-case fallback only for Option (A/B/C/D — admins
        // sometimes type lowercase).
        return entries.firstOrNull { it.name == raw }
            ?: if (E::class == Option::class) {
                entries.firstOrNull { it.name.equals(raw, ignoreCase = true) }
            } else null
            ?: run {
                errors += "$field '$raw' is not a valid value"
                null
            }
    }

    /** Apache Commons CSV returns "" for missing fields; normalize that to null. */
    private fun String?.orEmptyNull(): String? =
        if (this == null) null else if (isBlank()) null else this

    private fun isBlankRow(record: CSVRecord): Boolean =
        record.toList().all { it.isNullOrBlank() }
}
