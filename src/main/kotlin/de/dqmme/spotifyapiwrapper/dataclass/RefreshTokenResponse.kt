package de.dqmme.spotifyapiwrapper.dataclass

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data class to serialize responses when an access token is being requested
 * @param[accessToken] The new bearer access token
 * @param[tokenType] The token type, is always bearer
 * @param[scope] The scopes, which were used to generate this access token
 * @param[expiresIn] The time until the token is no longer valid
 */

@Serializable
data class RefreshTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    val scope: String,
    @SerialName("expires_in") val expiresIn: Int,
)
