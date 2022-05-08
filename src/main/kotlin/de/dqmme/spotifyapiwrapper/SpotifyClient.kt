package de.dqmme.spotifyapiwrapper

import de.dqmme.spotifyapiwrapper.dataclass.AccessTokenResponse
import de.dqmme.spotifyapiwrapper.dataclass.RefreshTokenResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class SpotifyClient private constructor(
    private var clientId: String?,
    private var clientSecret: String?,
    private var redirectUri: String?,
    private var bearerToken: String?,
    private var refreshToken: String?
) {
    private val httpClient: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    fun generateCodeUrl(scopes: Set<String>): String {
        val scope = scopeString(scopes)

        return "https://accounts.spotify.com/authorize" +
                "?response_type=code" +
                "&client_id=$clientId" +
                "&scope=$scope" +
                "&redirect_uri=$redirectUri"
    }

    private fun scopeString(scopes: Set<String>): String {
        var string = ""

        scopes.forEach {
            string += "$it "
        }

        string = string.dropLast(1)

        return string
    }

    suspend fun requestAccessToken(code: String): AccessTokenResponse? {
        checkNotNull(clientId)
        checkNotNull(clientSecret)
        checkNotNull(redirectUri)

        val response = httpClient.post("https://accounts.spotify.com/api/token") {
            formData {
                append("code", code)
                append("client_id", clientId!!)
                append("client_secret", clientSecret!!)
                append("redirect_uri", redirectUri!!)
                append("grant_type", "authorization_code")
            }
        }.parseOrNull<AccessTokenResponse>()

        if (response != null) {
            with(response) {
                this@SpotifyClient.bearerToken = this.accessToken
                this@SpotifyClient.refreshToken = this.refreshToken
            }
        }

        return response
    }

    suspend fun refreshToken(refreshToken: String? = null): RefreshTokenResponse? {
        val newRefreshToken = refreshToken ?: this.refreshToken

        checkNotNull(newRefreshToken)
        checkNotNull(clientId)
        checkNotNull(clientSecret)

        val response = httpClient.post("https://accounts.spotify.com/api/token") {
            formData {
                append("grant_type", "refresh_token")
                append("refresh_token", newRefreshToken)
                append("client_id", clientId!!)
                append("client_secret", clientSecret!!)
            }
        }.parseOrNull<RefreshTokenResponse>()

        if (response != null) {
            with(response) {
                this@SpotifyClient.bearerToken = this.accessToken
            }
        }

        return response
    }

    private suspend inline fun <reified Any> HttpResponse.parseOrNull(): Any? {
        return try {
            body<Any>()
        } catch (_: Exception) {
            null
        }
    }

    class Builder {
        private var clientId: String? = null
        private var clientSecret: String? = null
        private var redirectUri: String? = null
        private var bearerToken: String? = null
        private var refreshToken: String? = null

        fun withClientId(clientId: String): Builder {
            this.clientId = clientId
            return this
        }

        fun withClientSecret(clientSecret: String): Builder {
            this.clientSecret = clientSecret
            return this
        }

        fun withRedirectUri(redirectUri: String): Builder {
            this.redirectUri = redirectUri
            return this
        }

        fun withBearerToken(bearerToken: String): Builder {
            this.bearerToken = bearerToken
            return this
        }

        fun withRefreshToken(refreshToken: String): Builder {
            this.refreshToken = refreshToken
            return this
        }

        fun build(): SpotifyClient {
            return SpotifyClient(
                clientId = clientId,
                clientSecret = clientSecret,
                redirectUri = redirectUri,
                bearerToken = bearerToken,
                refreshToken = refreshToken
            )
        }
    }
}