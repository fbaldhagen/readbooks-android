package com.fbaldhagen.readbooks.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class CollectionWithBooks(
    @Embedded val collection: CollectionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = BookCollectionCrossRefEntity::class,
            parentColumn = "collectionId",
            entityColumn = "bookId"
        )
    )
    val books: List<BookEntity>
)