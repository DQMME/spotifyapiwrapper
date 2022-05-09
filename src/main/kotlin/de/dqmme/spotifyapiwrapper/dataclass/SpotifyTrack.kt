package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data class to serialize responses when a track info is being fetched
 * @param[artists] List of artists that worked on this track
 * @param[album] The album where the track is coming from. May be null
 * @param[availableMarkets] The markets where the song can be streamed. May be null
 * @param[durationMilliseconds] How long the song is
 * @param[explicit] Whether the song contains explicit lyrics
 * @param[externalUrls] Currently only the link which can be opened in a spotify player
 * @param[apiHref] The url to fetch info about this track
 * @param[id] The id of the track
 * @param[isLocal] Whether the song is local
 * @param[name] The name of the track
 * @param[previewUrl] The url of the preview
 * @param[type] The object type
 * @param[uri] The uri of the track
 */

@Serializable
data class SpotifyTrack(
    val artists: List<SpotifyArtist>,
    val album: SpotifyAlbum? = null,
    @SerialName("available_markets") val availableMarkets: List<String>? = null,
    @SerialName("duration_ms") val durationMilliseconds: Long,
    val explicit: Boolean,
    @SerialName("external_urls") val externalUrls: SpotifyExternalUrls,
    @SerialName("href") val apiHref: String,
    val id: String,
    @SerialName("is_local") val isLocal: Boolean,
    val name: String,
    @SerialName("preview_url") val previewUrl: String,
    val type: String,
    val uri: String
)