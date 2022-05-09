package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.Serializable

/**
 * A data class to serialize responses when the copyright of a spotify object is being fetched
 * @param[text] Which license is used
 * @param[type] What type of license is used
 */

@Serializable
data class SpotifyCopyright(
    val text: String,
    val type: String
)