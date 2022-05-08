import de.dqmme.spotifyapiwrapper.SpotifyClient
import de.dqmme.spotifyapiwrapper.dataclass.SpotifyScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

suspend fun main() {
    val spotifyClient = SpotifyClient.Builder()
        .withClientId("f5c52b663c8f41219c2061b9d021ec01")
        .withClientSecret("6e3236d4dd24440495cdad1150ac4ac0")
        .withRedirectUri("https://example.com/callback")
        .build()

    println(spotifyClient.generateCodeUrl(scopes = setOf(SpotifyScope.USER_READ_CURRENTLY_PLAYING)))

    val code = readLine()!!

    val accessTokenResponse = spotifyClient.requestAccessToken(code)

    println("Got Access Token - Response:")
    println(accessTokenResponse)

    val scope = CoroutineScope(Dispatchers.Default)

    scope.launch {
        val refreshTokenResponse = spotifyClient.refreshToken()

        println("Refreshed Token - Response:")
        println(refreshTokenResponse)
        delay(30.minutes)
    }
}