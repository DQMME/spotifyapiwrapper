package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.Serializable

/**
 * A data class to serialize responses when an object with images is beeing fetched
 * @param[height] The height of the image
 * @param[width] The width of the image
 * @param[url] The url of the image
 */

@Serializable
data class SpotifyImage(
    val height: Int? = null,
    val width: Int? = null,
    val url: String
)
