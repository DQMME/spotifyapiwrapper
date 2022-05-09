package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.Serializable

/**
 * A data class to serialize responses when the top tracks of an artist are fetched
 * @param[tracks] The list of tracks
 */

@Serializable
data class SpotifyTopTracksResponse(
    val tracks: List<SpotifyTrack>
)
