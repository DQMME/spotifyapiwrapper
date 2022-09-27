package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpotifyContext(
    @SerialName("external_urls") val externalUrls: SpotifyExternalUrls,
    val href: String,
    val type: String,
    val uri: String
)
