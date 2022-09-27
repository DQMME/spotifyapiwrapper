package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpotifyPlayback(
    val device: SpotifyDevice,
    @SerialName("shuffle_state") val shuffleState: Boolean,
    @SerialName("repeat_state") val repeatState: String,
    val timestamp: Long,
    val context: SpotifyContext,
    @SerialName("progress_ms") val progressMs: Int,
    val item: SpotifyTrack,
    @SerialName("currently_playing_type") val currentlyPlayingType: String,
    @SerialName("is_playing") val isPlaying: Boolean
)