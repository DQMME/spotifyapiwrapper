package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data class to serialize responses when related artists are being fetched
 * @param[externalUrls] Currently only the link which can be opened in a spotify player
 * @param[followers] How many followers the artist has
 * @param[genres] The genres of the artists. May be null
 * @param[apiHref] The url to fetch info about this related artist
 * @param[id] The id of the artist
 * @param[images] The images the artist is using
 * @param[name] The name of the artist
 * @param[popularity] The popularity of the artist. The value will be between 0 and 100, with 100 being the most popular. The artist's popularity is calculated from the popularity of all the artist's tracks
 * @param[type] The object type
 * @param[uri] The uri of the artist
 */

@Serializable
data class SpotifyRelatedArtist(
    @SerialName("external_urls") val externalUrls: SpotifyExternalUrls,
    val followers: SpotifyFollowers,
    val genres: List<String>? = null,
    @SerialName("href") val apiHref: String,
    val id: String,
    val images: List<SpotifyImage>,
    val name: String,
    val popularity: Int,
    val type: String,
    val uri: String
)

@Serializable
data class SpotifyRelatedArtistResponse(
    val artists: List<SpotifyRelatedArtist>
)