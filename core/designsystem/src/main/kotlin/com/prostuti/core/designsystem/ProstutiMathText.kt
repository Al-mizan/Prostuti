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

import androidx.compose.ui.unit.em

/**
 * Robust Math & Bangla Typography Formatter for Prostuti.
 * Converts LaTeX formulas ($...$), superscripts, subscripts, radicals, fractions,
 * and Greek symbols into rich styled AnnotatedString, while ensuring Bengali conjuncts (যুক্তবর্ণ)
 * render crisply without clipping or font boundary corruption.
 */
object MathTextFormatter {

    private fun replaceNestedFractions(input: String): String {
        var s = input
        while (s.contains("\\frac{")) {
            val start = s.indexOf("\\frac{")
            var depth = 1
            var numEnd = -1
            for (idx in (start + 6) until s.length) {
                if (s[idx] == '{') depth++
                else if (s[idx] == '}') {
                    depth--
                    if (depth == 0) {
                        numEnd = idx
                        break
                    }
                }
            }
            if (numEnd == -1 || numEnd + 1 >= s.length || s[numEnd + 1] != '{') break
            val denStart = numEnd + 1
            depth = 1
            var denEnd = -1
            for (idx in (denStart + 1) until s.length) {
                if (s[idx] == '{') depth++
                else if (s[idx] == '}') {
                    depth--
                    if (depth == 0) {
                        denEnd = idx
                        break
                    }
                }
            }
            if (denEnd == -1) break
            val num = s.substring(start + 6, numEnd).trim()
            val den = s.substring(denStart + 1, denEnd).trim()
            s = s.substring(0, start) + "(${num}/${den})" + s.substring(denEnd + 1)
        }
        return s
    }

    private fun replaceNestedSqrt(input: String): String {
        var s = input
        while (s.contains("\\sqrt{")) {
            val start = s.indexOf("\\sqrt{")
            var depth = 1
            var end = -1
            for (idx in (start + 6) until s.length) {
                if (s[idx] == '{') depth++
                else if (s[idx] == '}') {
                    depth--
                    if (depth == 0) {
                        end = idx
                        break
                    }
                }
            }
            if (end == -1) break
            val content = s.substring(start + 6, end).trim()
            s = s.substring(0, start) + "√(${content})" + s.substring(end + 1)
        }
        return s
    }

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
            .replace("\\to", "→")
            .replace("\\leftarrow", "←")
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
            .replace("\\cdot", "·")
            .replace("\\degree", "°")
            .replace("\\angle", "∠")
            .replace("\\triangle", "△")
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
            .replace("\\mathbb{Q}", "ℚ")
            .replace("\\IN", "ℕ")
            .replace("\\left(", "(")
            .replace("\\right)", ")")
            .replace("\\left[", "[")
            .replace("\\right]", "]")
            .replace("\\left.", "")
            .replace("\\right.", "")
            .replace("\\{", "{")
            .replace("\\}", "}")

        // Remove \text{...} and \mathrm{...} wrappers
        text = Regex("""\\(?:text|mathrm)\{([^{}]+)\}""").replace(text) { it.groupValues[1] }
        text = text.replace("\\text", "").replace("\\mathrm", "")

        // 3. Convert \sqrt{x} -> √(x) and single-letter sqrt
        text = replaceNestedSqrt(text)
        val sqrtSingle = Regex("""\\sqrt\s*([0-9a-zA-Z])""")
        text = sqrtSingle.replace(text) { match ->
            "√${match.groupValues[1]}"
        }

        // 4. Convert \frac{a}{b} -> (a/b)
        text = replaceNestedFractions(text)

        // 5. Convert \dot{x} -> ẋ
        val dotRegex = Regex("""\\dot\{([^{}]+)\}""")
        text = dotRegex.replace(text) { match ->
            "${match.groupValues[1]}̇"
        }

        // 6. Strip $ delimiters used in LaTeX
        text = text.replace("$", "")

        // 7. Build rich annotated string with clean single-shift superscript and subscript spans
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
                                        baselineShift = BaselineShift(0.35f),
                                        fontSize = 0.75.em,
                                    )
                                )
                                append(content)
                                pop()
                                i = end + 1
                            } else {
                                append('^')
                            }
                        } else if (i < text.length) {
                            val c = text[i]
                            pushStyle(
                                SpanStyle(
                                    baselineShift = BaselineShift(0.35f),
                                    fontSize = 0.75.em,
                                )
                            )
                            append(c)
                            pop()
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
                                        baselineShift = BaselineShift(-0.25f),
                                        fontSize = 0.75.em,
                                    )
                                )
                                append(content)
                                pop()
                                i = end + 1
                            } else {
                                append('_')
                            }
                        } else if (i < text.length) {
                            val c = text[i]
                            pushStyle(
                                SpanStyle(
                                    baselineShift = BaselineShift(-0.25f),
                                    fontSize = 0.75.em,
                                )
                            )
                            append(c)
                            pop()
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
