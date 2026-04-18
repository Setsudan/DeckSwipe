package one.launay.deckswipe.ui.import

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context

sealed class ClipboardReadOutcome {
    data class Ok(val text: String?) : ClipboardReadOutcome()
    object EmptyClipboard : ClipboardReadOutcome()
    object UnsupportedMime : ClipboardReadOutcome()
}

interface ClipboardAccessor {
    fun read(): ClipboardReadOutcome
    fun writePlainText(label: String, text: String)
}

fun clipboardAccessorFromContext(context: Context): ClipboardAccessor {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    return object : ClipboardAccessor {
        override fun read(): ClipboardReadOutcome {
            val clip = clipboard.primaryClip
            if (clip == null || !clipboard.hasPrimaryClip()) {
                return ClipboardReadOutcome.EmptyClipboard
            }
            val description = clip.description
            if (!description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) &&
                !description.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)
            ) {
                return ClipboardReadOutcome.UnsupportedMime
            }
            return ClipboardReadOutcome.Ok(clip.getItemAt(0).text?.toString())
        }

        override fun writePlainText(label: String, text: String) {
            clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        }
    }
}
