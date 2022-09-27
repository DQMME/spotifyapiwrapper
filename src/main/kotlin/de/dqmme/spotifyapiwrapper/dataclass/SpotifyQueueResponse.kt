package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpotifyQueueResponse(
    @SerialName("currently_playing") val currentlyPlaying: SpotifyTrack,
    val queue: List<SpotifyTrack>
)