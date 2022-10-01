package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpotifyUser(
    @SerialName("display_name") val displayName: String,
    @SerialName("external_urls") val externalUrls: SpotifyExternalUrls,
    val followers: SpotifyFollowers? = null,
    val href: String,
    val id: String,
    val images: List<SpotifyImage>? = null,
    val type: String,
    val uri: String
)