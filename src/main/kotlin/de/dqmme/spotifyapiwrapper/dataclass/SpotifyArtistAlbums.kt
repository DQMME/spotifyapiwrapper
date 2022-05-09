package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data class to serialize responses when the albums of an artist are being fetched
 * @param[apiHref] The url to fetch info about this album list
 * @param[items] A list of albums
 * @param[limit] The limit of how many albums were fetched
 * @param[offset] Where the list starts
 * @param[total] How many albums were fetched
 */

@Serializable
data class SpotifyArtistAlbums(
    @SerialName("href") val apiHref: String,
    val items: List<SpotifyAlbum>,
    val limit: Int,
    val offset: Int,
    val total: Int
)