package one.launay.deckswipe.ui.import

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context

sealed class ClipboardReadOutcome {
    data class Ok(val text: String?) : ClipboardReadOutcome()
    object EmptyClipboard : ClipboardReadOutcome()
    object UnsupportedMime : ClipboardReadOutcome()
}

fun interface ClipboardAccessor {
    fun read(): ClipboardReadOutcome
}

fun clipboardAccessorFromContext(context: Context): ClipboardAccessor {
    return ClipboardAccessor {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        if (clip == null || !clipboard.hasPrimaryClip()) {
            return@ClipboardAccessor ClipboardReadOutcome.EmptyClipboard
        }
        val description = clip.description
        if (!description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) &&
            !description.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)
        ) {
            return@ClipboardAccessor ClipboardReadOutcome.UnsupportedMime
        }
        ClipboardReadOutcome.Ok(clip.getItemAt(0).text?.toString())
    }
}
