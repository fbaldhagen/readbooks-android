package com.fbaldhagen.readbooks.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fbaldhagen.readbooks.domain.model.ReadingStatus

@Entity(
    tableName = "books",
    indices = [
        Index(value = ["filePath"], unique = true),
        Index(value = ["remoteId"], unique = true)
    ]
)
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val remoteId: String? = null,
    val title: String,
    val author: String?,
    val description: String?,
    val filePath: String,
    val coverImagePath: String?,
    val dateAdded: Long,
    val lastReadLocator: String? = null,
    val lastOpenedTimestamp: Long? = null,
    val rating: Int = 0,
    val readingStatus: ReadingStatus = ReadingStatus.NOT_STARTED,
    val isArchived: Boolean = false
)