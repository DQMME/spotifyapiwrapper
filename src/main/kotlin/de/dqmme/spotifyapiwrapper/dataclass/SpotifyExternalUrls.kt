package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.Serializable

/**
 * A data class to serialize responses when a spotify object is being fetched
 * @param[spotify] The link which is used to open the object in a player
 */

@Serializable
data class SpotifyExternalUrls(
    val spotify: String
)