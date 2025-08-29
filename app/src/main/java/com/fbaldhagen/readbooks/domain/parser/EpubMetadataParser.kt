package com.fbaldhagen.readbooks.domain.parser

import com.fbaldhagen.readbooks.domain.model.Book
import java.io.File

interface EpubMetadataParser {

    suspend fun parse(file: File): Result<Book>
}