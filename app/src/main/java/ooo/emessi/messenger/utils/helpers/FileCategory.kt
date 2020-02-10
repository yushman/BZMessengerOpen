package ooo.emessi.messenger.utils.helpers

import ooo.emessi.messenger.data.model.bz_model.message.BZMessage

enum class FileCategory {
    image,
    audio,
    video,
    document,
    pdf,
    table,
    presentation,
    archive,
    file;


    companion object {

        fun getFileCategoryByPath(path: String): FileCategory {
            val ext = path.substring(path.lastIndexOf(".")).substring(1)
            return when (ext) {
                "jpg", "png", "bmp", "gif", "webp", "heic", "heif" -> image
                "mp3", "ogg", "mid", "xmf", "mxmf", "flac", "wav" -> audio
                "webm", "avi", "mkv", "mp4", "3gp" -> video
                "xml", "txt", "json", "doc", "docx", "odt", "odf" -> document
                "pdf" -> pdf
                "xls", "xlsx", "ods" -> table
                "odp", "ppt" -> presentation
                "rar", "zip", "7z", "tar", "gz", "s7z", "jar", "sfx", "tgz", "Z", "bz2", "zipx" -> archive
                else -> file
            }
        }

        fun getFileCategoryByMime(mimeType: String?): FileCategory {
            if (mimeType == null) return file

            return if (mimeType.contains("image/")) {
                image

            } else if (mimeType.contains("audio/")) {
                audio

            } else if (mimeType.contains("video/")) {
                video

            } else if (mimeType.contains("text/") || mimeType == "application/json"
                || mimeType == "application/xml"
                || mimeType == "application/vnd.oasis.opendocument.text"
                || mimeType == "application/vnd.oasis.opendocument.graphics"
                || mimeType == "application/msword"
            ) {
                document

            } else if (mimeType == "application/pdf") {
                pdf

            } else if (mimeType == "application/vnd.oasis.opendocument.spreadsheet" || mimeType == "application/vnd.ms-excel") {
                table

            } else if (mimeType == "application/vnd.ms-powerpoint" || mimeType == "application/vnd.oasis.opendocument.presentation") {
                presentation

            } else if (mimeType == "application/zip" || mimeType == "application/gzip"
                || mimeType == "application/x-rar-compressed"
                || mimeType == "application/x-tar"
                || mimeType == "application/x-7z-compressed"
            ) {
                archive

            } else {
                FileCategory.file
            }
        }

        fun getCategoryName(category: FileCategory, withHtml: Boolean): String {
            when (category) {
                image -> return if (withHtml) "<font color='#1565c0'>Image:</font> " else "Image: "
                audio -> return if (withHtml) "<font color='#1565c0'>Audio:</font> " else "Audio: "
                video -> return if (withHtml) "<font color='#1565c0'>Video:</font> " else "Video: "
                document -> return if (withHtml) "<font color='#1565c0'>Document:</font> " else "Document: "
                pdf -> return if (withHtml) "<font color='#1565c0'>PDF:</font> " else "PDF: "
                table -> return if (withHtml) "<font color='#1565c0'>Table:</font> " else "Table: "
                presentation -> return if (withHtml) "<font color='#1565c0'>Presentation:</font> " else "Presentation: "
                archive -> return if (withHtml) "<font color='#1565c0'>Archive:</font> " else "Archive: "
                else -> return if (withHtml) "<font color='#1565c0'>File:</font> " else "File: "
            }
        }

        fun getCategoryByName(name: String): FileCategory{
            return when (name) {
                image.name -> image
                audio.name -> audio
                video.name -> video
                document.name -> document
                pdf.name -> pdf
                table.name -> table
                presentation.name -> presentation
                archive.name -> archive
                file.name -> file
                else -> file
            }
        }

//        fun toPayloadType(fileCategory: FileCategory): BZMessage.PayloadType{
//            return when (fileCategory) {
//                image -> BZMessage.calcPayloadType()
//                audio -> TODO()
//                video -> TODO()
//                document -> TODO()
//                pdf -> TODO()
//                table -> TODO()
//                presentation -> TODO()
//                archive -> TODO()
//                file -> TODO()
//                else ->
//            }
//        }
    }
}