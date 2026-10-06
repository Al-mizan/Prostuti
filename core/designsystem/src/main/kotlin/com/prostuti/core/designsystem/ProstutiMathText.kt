package com.prostuti.core.designsystem

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/**
 * Robust Math & Bangla Typography Formatter for Prostuti.
 * Converts LaTeX formulas ($...$), superscripts, subscripts, radicals, fractions,
 * and Greek symbols into rich styled AnnotatedString, while ensuring Bengali conjuncts (যুক্তবর্ণ)
 * render crisply without clipping or font boundary corruption.
 */
object MathTextFormatter {

    private val superscriptMap = mapOf(
        '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
        '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹',
        '+' to '⁺', '-' to '⁻', '=' to '⁼', '(' to '⁽', ')' to '⁾',
        'n' to 'ⁿ', 'i' to 'ⁱ', 'x' to 'ˣ'
    )

    private val subscriptMap = mapOf(
        '0' to '₀', '1' to '₁', '2' to '₂', '3' to '₃', '4' to '₄',
        '5' to '₅', '6' to '₆', '7' to '₇', '8' to '₈', '9' to '₉',
        '+' to '₊', '-' to '₋', '=' to '₌', '(' to '₍', ')' to '₎',
        'a' to 'ₐ', 'e' to 'ₑ', 'o' to 'ₒ', 'x' to 'ₓ', 'n' to 'ₙ'
    )

    fun format(rawText: String): AnnotatedString {
        if (rawText.isBlank()) return AnnotatedString("")

        // 1. Strip zero-width characters and KaTeX artifacts
        var text = rawText
            .replace("\u200B", "")
            .replace("\u200C", "")
            .replace("\uFEFF", "")
            .trim()

        // 2. Decode standard LaTeX symbol replacements
        text = text
            .replace("\\rightarrow", "→")
            .replace("\\le", "≤")
            .replace("\\leq", "≤")
            .replace("\\ge", "≥")
            .replace("\\geq", "≥")
            .replace("\\ne", "≠")
            .replace("\\neq", "≠")
            .replace("\\times", "×")
            .replace("\\div", "÷")
            .replace("\\pm", "±")
            .replace("\\mp", "∓")
            .replace("\\infty", "∞")
            .replace("\\pi", "π")
            .replace("\\in", "∈")
            .replace("\\notin", "∉")
            .replace("\\cap", "∩")
            .replace("\\cup", "∪")
            .replace("\\subset", "⊂")
            .replace("\\subseteq", "⊆")
            .replace("\\approx", "≈")
            .replace("\\alpha", "α")
            .replace("\\beta", "β")
            .replace("\\gamma", "γ")
            .replace("\\theta", "θ")
            .replace("\\lambda", "λ")
            .replace("\\mu", "μ")
            .replace("\\sigma", "σ")
            .replace("\\omega", "ω")
            .replace("\\log", "log")
            .replace("\\ln", "ln")
            .replace("\\sin", "sin")
            .replace("\\cos", "cos")
            .replace("\\tan", "tan")
            .replace("\\mathbb{N}", "ℕ")
            .replace("\\mathbb{R}", "ℝ")
            .replace("\\mathbb{Z}", "ℤ")
            .replace("\\IN", "ℕ")
            .replace("\\left(", "(")
            .replace("\\right)", ")")
            .replace("\\left[", "[")
            .replace("\\right]", "]")
            .replace("\\left.", "")
            .replace("\\right.", "")
            .replace("\\{", "{")
            .replace("\\}", "}")
            .replace("\\text", "")
            .replace("\\mathrm", "")

        // 3. Convert \sqrt{x} -> √(x)
        val sqrtRegex = Regex("""\\sqrt\{([^{}]+)\}""")
        text = sqrtRegex.replace(text) { match ->
            "√(${match.groupValues[1].trim()})"
        }
        val sqrtSingle = Regex("""\\sqrt\s*([0-9a-zA-Z])""")
        text = sqrtSingle.replace(text) { match ->
            "√${match.groupValues[1]}"
        }

        // 4. Convert \frac{a}{b} -> (a/b)
        val fracRegex = Regex("""\\frac\{([^{}]+)\}\{([^{}]+)\}""")
        text = fracRegex.replace(text) { match ->
            "${match.groupValues[1].trim()}/${match.groupValues[2].trim()}"
        }

        // 5. Convert \dot{x} -> ẋ
        val dotRegex = Regex("""\\dot\{([^{}]+)\}""")
        text = dotRegex.replace(text) { match ->
            "${match.groupValues[1]}̇"
        }

        // 6. Strip $ delimiters used in LaTeX
        text = text.replace("$", "")

        // 7. Build rich annotated string with superscript and subscript spans
        return buildAnnotatedString {
            var i = 0
            while (i < text.length) {
                when {
                    // Superscript syntax: ^{content} or ^char
                    text[i] == '^' -> {
                        i++
                        if (i < text.length && text[i] == '{') {
                            val end = text.indexOf('}', i)
                            if (end != -1) {
                                val content = text.substring(i + 1, end)
                                pushStyle(
                                    SpanStyle(
                                        baselineShift = BaselineShift.Superscript,
                                        fontSize = TextUnit.Unspecified,
                                    )
                                )
                                append(toUnicodeSuperscript(content))
                                pop()
                                i = end + 1
                            } else {
                                append('^')
                            }
                        } else if (i < text.length) {
                            val c = text[i]
                            val superChar = superscriptMap[c]
                            if (superChar != null) {
                                append(superChar)
                            } else {
                                pushStyle(SpanStyle(baselineShift = BaselineShift.Superscript))
                                append(c)
                                pop()
                            }
                            i++
                        }
                    }

                    // Subscript syntax: _{content} or _char
                    text[i] == '_' -> {
                        i++
                        if (i < text.length && text[i] == '{') {
                            val end = text.indexOf('}', i)
                            if (end != -1) {
                                val content = text.substring(i + 1, end)
                                pushStyle(
                                    SpanStyle(
                                        baselineShift = BaselineShift.Subscript,
                                        fontSize = TextUnit.Unspecified,
                                    )
                                )
                                append(toUnicodeSubscript(content))
                                pop()
                                i = end + 1
                            } else {
                                append('_')
                            }
                        } else if (i < text.length) {
                            val c = text[i]
                            val subChar = subscriptMap[c]
                            if (subChar != null) {
                                append(subChar)
                            } else {
                                pushStyle(SpanStyle(baselineShift = BaselineShift.Subscript))
                                append(c)
                                pop()
                            }
                            i++
                        }
                    }

                    else -> {
                        append(text[i])
                        i++
                    }
                }
            }
        }
    }

    private fun toUnicodeSuperscript(s: String): String {
        val sb = StringBuilder()
        for (c in s) {
            sb.append(superscriptMap[c] ?: c)
        }
        return sb.toString()
    }

    private fun toUnicodeSubscript(s: String): String {
        val sb = StringBuilder()
        for (c in s) {
            sb.append(subscriptMap[c] ?: c)
        }
        return sb.toString()
    }
}

/**
 * Dedicated Math & Bangla Text composable.
 * Guarantees proper conjunct font metrics (`includeFontPadding = false`)
 * and mathematical equation rendering without broken symbols.
 */
@Composable
fun ProstutiMathText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    lineHeight: TextUnit = TextUnit.Unspecified,
) {
    val formatted = remember(text) { MathTextFormatter.format(text) }

    val combinedStyle = style.copy(
        fontWeight = fontWeight ?: style.fontWeight,
        textAlign = textAlign ?: style.textAlign,
        lineHeight = if (lineHeight != TextUnit.Unspecified) lineHeight else style.lineHeight,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false,
        ),
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None,
        ),
    )

    Text(
        text = formatted,
        modifier = modifier,
        style = combinedStyle,
        color = color,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
    )
}
