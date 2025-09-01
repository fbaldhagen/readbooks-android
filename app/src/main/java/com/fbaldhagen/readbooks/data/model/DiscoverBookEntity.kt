package com.fbaldhagen.readbooks.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "discover_books")
data class DiscoverBookEntity(
    @PrimaryKey
    val remoteId: Int,
    val title: String,
    val author: String?,
    val coverUrl: String?,
    val query: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)