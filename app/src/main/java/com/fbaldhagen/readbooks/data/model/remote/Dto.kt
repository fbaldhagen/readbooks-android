package com.fbaldhagen.readbooks.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookDetailDto(
    val id: Int,
    val title: String,
    val authors: List<PersonDto> = emptyList(),
    val subjects: List<String> = emptyList(),
    val summaries: List<String> = emptyList(),
    val languages: List<String> = emptyList(),
    val formats: FormatsDto = FormatsDto(imageUrl = null, textHtmlUrl = null, epubUrl = null),
    @SerialName("download_count")
    val downloadCount: Int
)

@Serializable
data class PersonDto(
    val name: String,
    @SerialName("birth_year")
    val birthYear: Int?,
    @SerialName("death_year")
    val deathYear: Int?
)

@Serializable
data class FormatsDto(
    @SerialName("image/jpeg")
    val imageUrl: String? = null,

    @SerialName("application/epub+zip")
    val epubUrl: String? = null,

    @SerialName("text/html")
    val textHtmlUrl: String? = null
)

@Serializable
data class BookDto(
    val id: Int,
    val title: String,
    val authors: List<PersonDto>,
    @SerialName("download_count")
    val downloadCount: Int,
    val formats: FormatsDto = FormatsDto(imageUrl = null, textHtmlUrl = null, epubUrl = null)
)