package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpotifyDevice(
    val id: String,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("is_private_session") val isPrivateSession: Boolean,
    @SerialName("is_restricted") val isRestricted: Boolean,
    val name: String,
    val type: String,
    @SerialName("volume_percent") val volumePercent: Int
)
