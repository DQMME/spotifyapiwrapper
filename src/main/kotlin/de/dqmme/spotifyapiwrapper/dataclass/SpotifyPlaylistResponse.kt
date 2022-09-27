package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class SpotifyPlaylistResponse(
    val href: String,
    val items: List<SpotifyPlaylist>
)
