package com.fbaldhagen.readbooks.data.parser

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocatorParser @Inject constructor() {

    private val json = Json { ignoreUnknownKeys = true }

    fun parseTotalProgression(locatorJson: String?): Float? {
        if (locatorJson.isNullOrBlank()) return null

        return try {
            val locator = json.decodeFromString<Locator>(locatorJson)
            locator.locations.totalProgression?.toFloat()
        } catch (e: Exception) {
            null
        }
    }
}

@Serializable
data class Locator(
    @SerialName("locations")
    val locations: Locations
)

@Serializable
data class Locations(
    @SerialName("totalProgression")
    val totalProgression: Double? = null
)