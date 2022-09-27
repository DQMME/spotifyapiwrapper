package de.dqmme.spotifyapiwrapper.dataclass

/**
 * Every Scope that can be used to authorize
 */

object SpotifyScope {
    //Images
    const val UGC_IMAGE_UPLOAD = "ugc-image-upload"

    //Spotify Connect
    const val USER_MODIFY_PLAYBACK_STATE = "user-modify-playback-state"
    const val USER_READ_PLAYBACK_STATE = "user-read-playback-state"
    const val USER_READ_CURRENTLY_PLAYING = "user-read-currently-playing"

    //Follow
    const val USER_FOLLOW_MODIFY = "user-follow-modify"
    const val USER_FOLLOW_READ = "user-follow-read"

    //Listening History
    const val USER_READ_RECENTLY_PLAYED = "user-read-recently-played"
    const val USER_READ_PLAYBACK_POSITION = "user-read-playback-position"
    const val USER_TOP_READ = "user-top-read"

    //Playlists
    const val PLAYLIST_READ_COLLABORATIVE = "playlist-read-collaborative"
    const val PLAYLIST_MODIFY_PUBLIC = "playlist-modify-public"
    const val PLAYLIST_READ_PRIVATE = "playlist-read-private"
    const val PLAYLIST_MODIFY_PRIVATE = "playlist-modify-private"

    //Playback
    const val APP_REMOTE_CONTROL = "app-remote-control"
    const val STREAMING = "streaming"

    //Users
    const val USER_READ_EMAIL = "user-read-email"
    const val USER_READ_PRIVATE = "user-read-private"

    //Library
    const val USER_LIBRARY_MODIFY = "user-library-modify"
    const val USER_LIBRARY_READ = "user-library-read"
}