package de.dqmme.spotifyapiwrapper.dataclass

/**
 * All values you can use at a spotify_includes request
 */

class SpotifyIncludeGroup {
    companion object {
        const val ALBUM = "album"
        const val SINGLE = "single"
        const val APPEARS_ON = "appears_on"
        const val COMPILATION = "compilation"
    }
}