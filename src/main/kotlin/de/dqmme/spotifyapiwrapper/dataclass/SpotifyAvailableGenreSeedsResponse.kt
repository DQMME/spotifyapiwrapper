package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class SpotifyAvailableGenreSeedsResponse(
    val genres: List<String>
)
