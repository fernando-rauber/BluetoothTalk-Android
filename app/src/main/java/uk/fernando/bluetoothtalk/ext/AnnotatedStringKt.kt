package uk.fernando.bluetoothtalk.ext

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans

fun AnnotatedString.onTagFound(tag: String, index: Int, onFound: () -> Unit) {
    val annotations = getStringAnnotations(tag, start = index, end = index)
    annotations.firstOrNull()?.run {
        onFound()
    }
}

private fun Spanned.toAnnotatedString(): AnnotatedString {
    val spans: Array<*> = this.getSpans<Any>()
    return buildAnnotatedString {
        append(this@toAnnotatedString.toString())
        spans.forEach {
            val spanStart = getSpanStart(it)
            val spanEnd = getSpanEnd(it)
            when (it) {
                is URLSpan -> {
                    // TODO:not implemented url clicking
                }
                is StyleSpan -> {
                    val style = when (it.style) {
                        Typeface.BOLD -> FontWeight.Bold
                        Typeface.NORMAL -> FontWeight.Normal
                        else -> FontWeight.Normal
                    }
                    addStyle(SpanStyle(fontWeight = style), spanStart, spanEnd)
                }
                is ForegroundColorSpan -> {
                    val colour = it.foregroundColor
                    addStyle(SpanStyle(color = Color(colour)), spanStart, spanEnd)
                }
                else -> {
                    Log.w("CONVERT", "span not supported : $it")
                }
            }
        }
    }
}

fun String.asHtmlAnnotatedString(): AnnotatedString =
    HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT).toAnnotatedString()

@Composable
fun htmlAnnotatedStringResource(@StringRes id: Int) =
    stringResource(id = id).asHtmlAnnotatedString()
