package one.launay.deckswipe.data.covers

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun persistCoverFromUri(
    context: Context,
    sourceUri: Uri,
    deckId: Long
): Uri {
    val dir = File(context.filesDir, "covers").also { it.mkdirs() }
    val dest = File(dir, "deck_${deckId}_${System.currentTimeMillis()}.jpg")
    context.contentResolver.openInputStream(sourceUri)?.use { input ->
        dest.outputStream().use { output -> input.copyTo(output) }
    } ?: error("Could not read source image")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        dest
    )
}

fun deleteOwnedCoverFile(context: Context, coverUriString: String?) {
    val s = coverUriString ?: return
    val uri = Uri.parse(s)
    if (uri.authority != "${context.packageName}.fileprovider") return
    val name = uri.lastPathSegment ?: return
    val file = File(File(context.filesDir, "covers"), name)
    if (file.exists()) {
        file.delete()
    }
}
