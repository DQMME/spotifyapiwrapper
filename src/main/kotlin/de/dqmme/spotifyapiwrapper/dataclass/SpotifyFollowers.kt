package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data class to serialize responses when an amount of followers is being fetched
 * @param[apiHref] The url to fetch info about this follower list
 * @param[total] How many followers they got
 */

@Serializable
data class SpotifyFollowers(
    @SerialName("href") val apiHref: String? = null,
    val total: Int
)