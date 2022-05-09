package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data class to serialize responses when the info for a spotify album is fetched
 * @param[externalUrls] Currently only the link which can be opened in a spotify player
 * @param[apiHref] The url to fetch info about this artist
 * @param[id] The id of the artist
 * @param[name] The name of the artist
 * @param[type] The object type.
 * @param[uri] The uri of the artist.
 */

@Serializable
data class SpotifyArtist(
    @SerialName("external_urls") val externalUrls: SpotifyExternalUrls,
    @SerialName("href") val apiHref: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)
