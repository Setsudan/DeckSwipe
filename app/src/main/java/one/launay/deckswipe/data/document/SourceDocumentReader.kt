package one.launay.deckswipe.data.document

import android.content.ContentResolver
import android.net.Uri
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

enum class SourceDocumentFailure {
    UNSUPPORTED_TYPE,
    READ_FAILED,
    RAW_FILE_TOO_LARGE,
    EMPTY_TEXT,
    PDF_PASSWORD,
    PDF_PARSE_ERROR
}

sealed class SourceDocumentResult {
    data class Ok(val text: String, val truncated: Boolean) : SourceDocumentResult()
    data class Error(val failure: SourceDocumentFailure) : SourceDocumentResult()
}

object SourceDocumentReader {

    private const val MAX_PLAIN_BYTES: Int = 786_432
    private const val MAX_CLIP_SOURCE_CHARS: Int = 100_000
    private const val MAX_PDF_PAGES: Int = 80

    suspend fun extractText(resolver: ContentResolver, uri: Uri): SourceDocumentResult =
        withContext(Dispatchers.IO) {
            val mime = resolver.getType(uri).orEmpty().lowercase()
            val path = uri.lastPathSegment?.lowercase().orEmpty()
            val treatAsPdf = mime == "application/pdf" || path.endsWith(".pdf")
            val treatAsText =
                mime.startsWith("text/") ||
                    mime == "application/json" ||
                    path.endsWith(".txt") ||
                    path.endsWith(".md") ||
                    path.endsWith(".markdown")

            when {
                treatAsPdf -> readPdf(resolver, uri)
                treatAsText -> readPlain(resolver, uri)
                else -> SourceDocumentResult.Error(SourceDocumentFailure.UNSUPPORTED_TYPE)
            }
        }

    private fun readPlain(resolver: ContentResolver, uri: Uri): SourceDocumentResult {
        return try {
            resolver.openInputStream(uri)?.use { input ->
                val out = ByteArrayOutputStream()
                val buf = ByteArray(8192)
                var total = 0
                while (true) {
                    val read = input.read(buf)
                    if (read == -1) break
                    total += read
                    if (total > MAX_PLAIN_BYTES) {
                        return SourceDocumentResult.Error(SourceDocumentFailure.RAW_FILE_TOO_LARGE)
                    }
                    out.write(buf, 0, read)
                }
                val bytes = out.toByteArray()
                val text = decodeUtf8StripBom(bytes).trim()
                if (text.isEmpty()) {
                    SourceDocumentResult.Error(SourceDocumentFailure.EMPTY_TEXT)
                } else {
                    clipToClipboard(text)
                }
            } ?: SourceDocumentResult.Error(SourceDocumentFailure.READ_FAILED)
        } catch (_: Exception) {
            SourceDocumentResult.Error(SourceDocumentFailure.READ_FAILED)
        }
    }

    private fun readPdf(resolver: ContentResolver, uri: Uri): SourceDocumentResult {
        val inputStream = try {
            resolver.openInputStream(uri)
        } catch (_: Exception) {
            return SourceDocumentResult.Error(SourceDocumentFailure.READ_FAILED)
        } ?: return SourceDocumentResult.Error(SourceDocumentFailure.READ_FAILED)

        return try {
            inputStream.use { input ->
                PDDocument.load(input).use { doc ->
                    val stripper = PDFTextStripper()
                    val end = minOf(doc.numberOfPages, MAX_PDF_PAGES)
                    if (end < 1) {
                        SourceDocumentResult.Error(SourceDocumentFailure.EMPTY_TEXT)
                    } else {
                        stripper.setStartPage(1)
                        stripper.setEndPage(end)
                        val text = stripper.getText(doc).trim()
                        if (text.isEmpty()) {
                            SourceDocumentResult.Error(SourceDocumentFailure.EMPTY_TEXT)
                        } else {
                            clipToClipboard(text)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            val name = e.javaClass.name
            if (name.contains("InvalidPassword")) {
                SourceDocumentResult.Error(SourceDocumentFailure.PDF_PASSWORD)
            } else {
                SourceDocumentResult.Error(SourceDocumentFailure.PDF_PARSE_ERROR)
            }
        }
    }

    private fun clipToClipboard(text: String): SourceDocumentResult {
        if (text.length <= MAX_CLIP_SOURCE_CHARS) {
            return SourceDocumentResult.Ok(text = text, truncated = false)
        }
        return SourceDocumentResult.Ok(
            text = text.substring(0, MAX_CLIP_SOURCE_CHARS),
            truncated = true
        )
    }

    private fun decodeUtf8StripBom(bytes: ByteArray): String {
        if (bytes.size >= 3 &&
            bytes[0] == 0xEF.toByte() &&
            bytes[1] == 0xBB.toByte() &&
            bytes[2] == 0xBF.toByte()
        ) {
            return String(bytes, 3, bytes.size - 3, Charsets.UTF_8)
        }
        return String(bytes, Charsets.UTF_8)
    }
}
