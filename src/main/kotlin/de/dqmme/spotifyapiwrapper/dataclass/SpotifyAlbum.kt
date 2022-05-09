package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data class to serialize responses when the info for a spotify album is fetched
 * @param[albumType] The type of the album. Allowed values: "album", "single", "compilation"
 * @param[artists] The artists the album came from
 * @param[availableMarkets] The markets where the album can be streamed, might be null
 * @param[copyrights] Which copyright licenses are used for this album, might be null
 * @param[externalUrls] Currently only the link which can be opened in a spotify player
 * @param[apiHref] The url to fetch info about this album
 * @param[id] The id of the album
 * @param[images] The images from this album such as the icon
 * @param[name] The name of the album
 * @param[popularity] The popularity of the album. The value will be between 0 and 100, with 100 being the most popular. The album's popularity is calculated from the popularity of all the album's tracks
 * @param[releaseDate] The date when the album was released
 * @param[releaseDatePrecision] How precise the release date is
 * @param[tracks] A list of every track of the album
 * @param[type] The object type.
 * @param[uri] The uri of the album.
 */

@Serializable
data class SpotifyAlbum(
    @SerialName("album_type") val albumType: String,
    val artists: List<SpotifyArtist>,
    @SerialName("available_markets") val availableMarkets: List<String>? = null,
    val copyrights: List<SpotifyCopyright>? = null,
    @SerialName("external_urls") val externalUrls: SpotifyExternalUrls,
    @SerialName("href") val apiHref: String,
    val id: String,
    val images: List<SpotifyImage>,
    val name: String,
    val popularity: Int? = null,
    @SerialName("release_date") val releaseDate: String,
    @SerialName("release_date_precision") val releaseDatePrecision: String,
    @SerialName("total_tracks") val totalTracks: Int,
    val tracks: SpotifyTracks? = null,
    val type: String,
    val uri: String
)

@Serializable
data class SpotifyAlbumResponse(
    val albums: List<SpotifyAlbum>
)
