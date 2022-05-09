package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data class to serialize responses when a track list is being fetched
 * @param[apiHref] The url to fetch info about this track list
 * @param[items] The list of tracks
 * @param[limit] The limit of how many tracks were fetched
 * @param[offset] Where the list starts
 * @param[total] How many tracks were fetched
 */

@Serializable
data class SpotifyTracks(
    @SerialName("href") val apiHref: String,
    val items: List<SpotifyTrack>,
    val limit: Int,
    val offset: Int,
    val total: Int
)