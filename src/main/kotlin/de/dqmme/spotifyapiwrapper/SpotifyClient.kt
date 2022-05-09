package de.dqmme.spotifyapiwrapper

import de.dqmme.spotifyapiwrapper.dataclass.AccessTokenResponse
import de.dqmme.spotifyapiwrapper.dataclass.RefreshTokenResponse
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyAlbum
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyAlbumResponse
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyArtistAlbums
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyIncludeGroup
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyRelatedArtist
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyRelatedArtistResponse
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyTopTracksResponse
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyTrack
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyTracks
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Where all the magic happens
 */

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

    private val endpoint = "https://api.spotify.com/v1"

    /**
     * Generates an url to get an auth code
     * @param[scopes] The list of scopes that should be used
     * @see de.dqmme.spotifyapiwrapper.dataclass.SpotifyScope
     * @return[String] The url to authorize
     */

    fun generateAuthUrl(scopes: Set<String>): String {
        val scope = scopeString(scopes)

        return "https://accounts.spotify.com/authorize" +
                "?response_type=code" +
                "&client_id=$clientId" +
                "&scope=$scope" +
                "&redirect_uri=$redirectUri"
    }

    /**
     * Generates a string with all given scopes to use it as parameter
     * @param[scopes] The list of scopes that should be used
     * @see de.dqmme.spotifyapiwrapper.dataclass.SpotifyScope
     * @return[String] The string which contains all the scopes
     */

    private fun scopeString(scopes: Set<String>): String {
        return scopes.joinToString(" ")
    }

    /**
     * Requests a new access and refresh bearer token
     * @param[code] The auth code which was returned from the callback
     * @return[AccessTokenResponse] The response containing the tokens
     */

    suspend fun requestAccessToken(code: String): AccessTokenResponse? {
        checkNotNull(clientId)
        checkNotNull(clientSecret)
        checkNotNull(redirectUri)

        val response = httpClient.post("https://accounts.spotify.com/api/token") {
            setBody(FormDataContent(Parameters.build {
                append("code", code)
                append("client_id", clientId!!)
                append("client_secret", clientSecret!!)
                append("redirect_uri", redirectUri!!)
                append("grant_type", "authorization_code")
            }))
        }.parseOrNull<AccessTokenResponse>()

        if (response != null) {
            with(response) {
                this@SpotifyClient.bearerToken = this.accessToken
                this@SpotifyClient.refreshToken = this.refreshToken
            }
        }

        return response
    }

    /**
     * Refreshes an access token
     * @param[refreshToken] The refresh token that should be used. If null the cached refresh token is being used
     * @return[RefreshTokenResponse] The response containing the refreshed token
     */

    suspend fun refreshToken(refreshToken: String? = null): RefreshTokenResponse? {
        val newRefreshToken = refreshToken ?: this.refreshToken

        checkNotNull(newRefreshToken)
        checkNotNull(clientId)
        checkNotNull(clientSecret)

        val response = httpClient.post("https://accounts.spotify.com/api/token") {
            setBody(FormDataContent(Parameters.build {
                append("grant_type", "refresh_token")
                append("refresh_token", newRefreshToken)
                append("client_id", clientId!!)
                append("client_secret", clientSecret!!)
            }))
        }.parseOrNull<RefreshTokenResponse>()

        if (response != null) {
            with(response) {
                this@SpotifyClient.bearerToken = this.accessToken
            }
        }

        return response
    }

    /**
     * Fetches one or multiple albums
     * @param[albumIds] The album ids that should be fetched
     * @return[List] The list of spotify albums. Empty if failed
     */

    suspend fun getAlbums(albumIds: Set<String>): List<SpotifyAlbum> {
        checkNotNull(bearerToken)

        if(albumIds.isEmpty()) return listOf()

        val response = httpClient.get("$endpoint/albums") {
            parameter("ids", albumIds.joinToString(","))

            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.parseOrNull<SpotifyAlbumResponse>()

        return response?.albums ?: listOf()
    }

    /**
     * Fetches the tracks of an album
     * @param[albumId] The album id that should be used
     * @param[limit] How many songs should be fetched. Can't be greater than 50. Default 50
     * @param[offset] Where the song list should start.
     * @return[SpotifyTracks] The fetched tracks. Null if failed
     */

    suspend fun getAlbumTracks(albumId: String, limit: Int = 50, offset: Int = 0): SpotifyTracks? {
        checkNotNull(bearerToken)

        val response = httpClient.get("$endpoint/albums/$albumId/tracks") {
            parameter("limit", "$limit")
            parameter("offset", "$offset")

            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.parseOrNull<SpotifyTracks>()

        return response
    }

    /**
     * Fetches albums of an artist
     * @param[artistId] The artist id that should be used
     * @param[includeGroups] What groups should be included
     * @param[limit] How many albums should be fetched. Can't be greater than 50. Default 50
     * @param[offset] Where the album list should start.
     * @see de.dqmme.spotifyapiwrapper.dataclass.SpotifyIncludeGroup
     * @return[SpotifyArtistAlbums] The fetched albums. Null if failed
     */

    suspend fun getArtistAlbums(
        artistId: String,
        includeGroups: Set<String> = setOf(SpotifyIncludeGroup.ALBUM),
        limit: Int = 50,
        offset: Int = 0
    ): SpotifyArtistAlbums? {
        checkNotNull(bearerToken)

        val response = httpClient.get("$endpoint/artists/$artistId/albums") {
            parameter("include_groups", includeGroups.joinToString(","))

            parameter("limit", "$limit")
            parameter("offset", "$offset")

            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.parseOrNull<SpotifyArtistAlbums>()

        return response
    }

    /**
     * Fetches related artists
     * @param[artistId] The artist id that should be used
     * @return[List] The fetched artists. Null if failed
     */

    suspend fun getRelatedArtists(artistId: String): List<SpotifyRelatedArtist> {
        checkNotNull(bearerToken)

        val response = httpClient.get("$endpoint/artists/$artistId/related-artists") {
            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.parseOrNull<SpotifyRelatedArtistResponse>()

        return response?.artists ?: listOf()
    }

    /**
     * Fetches artist's top tracks
     * @param[artistId] The artist id that should be used
     * @param[market] The market that should be used. Default US
     * @return[List] The fetched tracks. Null if failed
     */

    suspend fun getArtistTopTracks(artistId: String, market: String = "US"): List<SpotifyTrack>? {
        checkNotNull(bearerToken)

        val response = httpClient.get("$endpoint/artists/$artistId/top-tracks") {
            parameter("market", market)

            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }

        println(response.body<SpotifyTopTracksResponse>())

        return response.parseOrNull<SpotifyTopTracksResponse>()?.tracks
    }

    private suspend inline fun <reified Any> HttpResponse.parseOrNull(): Any? {
        return try {
            body<Any>()
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Closes the http client
     */

    fun close() {
        httpClient.close()
    }

    /**
     * Creates a new spotify client object
     */

    class Builder {
        private var clientId: String? = null
        private var clientSecret: String? = null
        private var redirectUri: String? = null
        private var bearerToken: String? = null
        private var refreshToken: String? = null

        /**
         * Sets the client id which should be used
         * @param[clientId] The client id
         * @return[Builder] The instance
         */

        fun withClientId(clientId: String): Builder {
            this.clientId = clientId
            return this
        }

        /**
         * Sets the client secret which should be used
         * @param[clientSecret] The client secret
         * @return[Builder] The instance
         */

        fun withClientSecret(clientSecret: String): Builder {
            this.clientSecret = clientSecret
            return this
        }

        /**
         * Sets the redirect uri which should be used
         * @param[redirectUri] The redirect uri
         * @return[Builder] The instance
         */

        fun withRedirectUri(redirectUri: String): Builder {
            this.redirectUri = redirectUri
            return this
        }

        /**
         * Sets the bearer token which should be used
         * @param[bearerToken] The bearer token
         * @return[Builder] The instance
         */

        fun withBearerToken(bearerToken: String): Builder {
            this.bearerToken = bearerToken
            return this
        }

        /**
         * Sets the refresh token which should be used
         * @param[refreshToken] The refresh token
         * @return[Builder] The instance
         */

        fun withRefreshToken(refreshToken: String): Builder {
            this.refreshToken = refreshToken
            return this
        }

        /**
         * Builds the client
         * @return[SpotifyClient] The spotify client instance
         */

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