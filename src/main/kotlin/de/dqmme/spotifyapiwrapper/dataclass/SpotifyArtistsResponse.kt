package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class SpotifyArtistsResponse(
    val artists: List<SpotifyArtist>
)
