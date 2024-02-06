package de.dqmme.spotifyapiwrapper

import de.dqmme.spotifyapiwrapper.dataclass.AccessTokenResponse
import de.dqmme.spotifyapiwrapper.dataclass.RefreshTokenResponse
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyAlbum
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyAlbumResponse
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyArtist
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyArtistAlbums
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyArtistsResponse
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyIncludeGroup
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyPlayback
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyPlaylistResponse
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyQueueResponse
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyRelatedArtist
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyRelatedArtistResponse
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyTopTracksResponse
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyTrack
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyTracks
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyUser
import de.dqmme.spotifyapiwrapper.util.findParameterValue
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.net.URL

/**
 * Where all the magic happens
 */

class SpotifyClient(
    var clientId: String? = null,
    var clientSecret: String? = null,
    var redirectUri: String? = null,
    var bearerToken: String? = null,
    var refreshToken: String? = null
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

    fun generateAuthUrl(vararg scopes: String): String {
        checkNotNull(clientId)
        checkNotNull(redirectUri)

        return "https://accounts.spotify.com/authorize" +
                "?response_type=code" +
                "&client_id=$clientId" +
                "&scope=${scopes.joinToString(" ")}" +
                "&redirect_uri=$redirectUri"
    }

    /**
     * Extractes the code from the given callback url
     * @param[callbackUrl] The callback url
     * @return[String] The extracted code or null
     */

    fun extractAuthCode(callbackUrl: String): String? {
        return URL(callbackUrl).findParameterValue("code")
    }

    /**
     * Requests a new access and refresh bearer token
     * @param[code] The auth code which was returned from the callback
     * @return[AccessTokenResponse] The response containing the tokens
     */

    suspend fun requestAccessToken(code: String): AccessTokenResponse {
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
        }.body<AccessTokenResponse>()

        this@SpotifyClient.bearerToken = response.accessToken
        this@SpotifyClient.refreshToken = response.refreshToken

        return response
    }

    /**
     * Refreshes an access token
     * @param[refreshToken] The refresh token that should be used. If null the cached refresh token is being used
     * @return[RefreshTokenResponse] The response containing the refreshed token
     */

    suspend fun refreshToken(refreshToken: String? = null): RefreshTokenResponse {
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
        }.body<RefreshTokenResponse>()

        this@SpotifyClient.bearerToken = response.accessToken

        return response
    }

    /**
     * Fetches one or multiple albums
     * @param[albumIds] The album ids that should be fetched
     * @return[List] The list of spotify albums. Empty if failed
     */

    suspend fun getAlbums(albumIds: Set<String>): List<SpotifyAlbum> {
        checkNotNull(bearerToken)

        if (albumIds.isEmpty()) return listOf()

        return httpClient.get("$endpoint/albums") {
            parameter("ids", albumIds.joinToString(","))

            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.body<SpotifyAlbumResponse>().albums
    }

    /**
     * Fetches the tracks of an album
     * @param[albumId] The album id that should be used
     * @param[limit] How many songs should be fetched. Can't be greater than 50. Default 50
     * @param[offset] Where the song list should start.
     * @return[SpotifyTracks] The fetched tracks.
     */

    suspend fun getAlbumTracks(albumId: String, limit: Int = 50, offset: Int = 0): SpotifyTracks {
        checkNotNull(bearerToken)

        return httpClient.get("$endpoint/albums/$albumId/tracks") {
            parameter("limit", "$limit")
            parameter("offset", "$offset")

            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.body()
    }

    /**
     * Fetches albums of an artist
     * @param[artistId] The artist id that should be used
     * @param[includeGroups] What groups should be included
     * @param[limit] How many albums should be fetched. Can't be greater than 50. Default 50
     * @param[offset] Where the album list should start.
     * @see de.dqmme.spotifyapiwrapper.dataclass.SpotifyIncludeGroup
     * @return[SpotifyArtistAlbums] The fetched albums.
     */

    suspend fun getArtistAlbums(
        artistId: String,
        includeGroups: Set<String> = setOf(SpotifyIncludeGroup.ALBUM),
        limit: Int = 50,
        offset: Int = 0
    ): SpotifyArtistAlbums {
        checkNotNull(bearerToken)

        return httpClient.get("$endpoint/artists/$artistId/albums") {
            parameter("include_groups", includeGroups.joinToString(","))

            parameter("limit", "$limit")
            parameter("offset", "$offset")

            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.body()
    }

    /**
     * Fetches related artists
     * @param[artistId] The artist id that should be used
     * @return[List] The fetched artists.
     */

    suspend fun getRelatedArtists(artistId: String): List<SpotifyRelatedArtist> {
        checkNotNull(bearerToken)

        return httpClient.get("$endpoint/artists/$artistId/related-artists") {
            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.body<SpotifyRelatedArtistResponse>().artists
    }

    /**
     * Fetches artist's top tracks
     * @param[artistId] The artist id that should be used
     * @param[market] The market that should be used. Default US
     * @return[List] The fetched tracks.
     */

    suspend fun getArtistTopTracks(artistId: String, market: String = "US"): List<SpotifyTrack> {
        checkNotNull(bearerToken)

        return httpClient.get("$endpoint/artists/$artistId/top-tracks") {
            parameter("market", market)

            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.body<SpotifyTopTracksResponse>().tracks
    }

    /**
     * Fetches artist's top tracks
     * @param[artistIds] A set of artist ids.
     * @return[List] The fetched artists.
     */

    suspend fun getArtists(artistIds: Set<String>): List<SpotifyArtist> {
        checkNotNull(bearerToken)

        return httpClient.get("$endpoint/artists/${artistIds.joinToString(",")}") {
            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.body<SpotifyArtistsResponse>().artists
    }

    /**
     * Unfollows artists or users
     * @param[userIds] A set of artist/user id's.
     * @param[type] Whether it's an artist or a user. Use [de.dqmme.spotifyapiwrapper.dataclass.UnfollowUserType]
     * @return[Boolean] Whether the request succeeded or not
     * */

    suspend fun unfollowUsers(userIds: Set<String>, type: String): Boolean {
        checkNotNull(bearerToken)

        return httpClient.delete("$endpoint/me/following") {
            parameter("ids", userIds.joinToString(","))
            parameter("type", type)

            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.status.isSuccess()
    }

    /**
     * Unfollows a playlist
     * @param[playlistId] The id of the playlist
     * @return[Boolean] Whether the request succeeded or not
     * */

    suspend fun unfollowPlaylist(playlistId: String): Boolean {
        checkNotNull(bearerToken)

        return httpClient.delete("$endpoint/$playlistId/followers") {
            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.status.isSuccess()
    }

    /**
     * Gets the current user's queue
     * @return[SpotifyQueueResponse] The queue
     * */

    suspend fun getQueue(): SpotifyQueueResponse {
        checkNotNull(bearerToken)

        return httpClient.get("$endpoint/me/player/queue") {
            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.body()
    }

    /**
     * Gets the user's current playback
     * @param[market] The market. Default: US
     * @return[SpotifyPlayback] The playback
     * */

    suspend fun getPlayback(market: String = "US"): SpotifyPlayback {
        checkNotNull(bearerToken)

        return httpClient.get("$endpoint/me/player") {
            parameter("market", market)

            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.body<SpotifyPlayback>()
    }

    /**
     * Gets the own user
     * @return[SpotifyUser] The user
     * */

    suspend fun getSelfUser(): SpotifyUser {
        checkNotNull(bearerToken)

        return httpClient.get("$endpoint/me") {
            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.body()
    }

    /**
     * Gets own playlists
     * @param[limit] The limit of numbers
     * @param[offset] Where to start in the list
     * @return[de.dqmme.spotifyapiwrapper.dataclass.SpotifyPlaylistResponse] The playlists response
     * */

    suspend fun getOwnPlaylists(limit: Int, offset: Int): SpotifyPlaylistResponse {
        checkNotNull(bearerToken)

        return httpClient.get("$endpoint/me/playlists") {
            parameter("limit", limit)
            parameter("offset", offset)

            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.body()
    }

    /**
     * Adds an item to a playlist
     * @param[playlistId] The id of the playlist
     * @param[position] At which position
     * @param[uris] A set of song uris to be added
     * @return[Boolean] Whether the request succeeded or not
     */

    suspend fun addItemsToPlaylist(playlistId: String, position: Int, uris: Set<String>): Boolean {
        checkNotNull(bearerToken)

        return httpClient.post("$endpoint/playlists/$playlistId") {
            parameter("position", position)
            parameter("uris", uris.joinToString(","))

            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }.status.isSuccess()
    }

    /**
     * Closes the http client
     */

    fun close() {
        httpClient.close()
    }
}

/**
 * Inline client builder
 * @param[builder] The Spotify Client Builder as Unit
 * */
inline fun SpotifyClient(
    builder: SpotifyClient.() -> Unit
): SpotifyClient {
    return SpotifyClient().apply(builder)
}