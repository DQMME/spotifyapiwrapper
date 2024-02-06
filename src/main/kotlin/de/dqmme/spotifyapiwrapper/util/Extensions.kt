package de.dqmme.spotifyapiwrapper.util

import java.net.URL

/**
 * Extracts a url parameter
 * @param[parameterName] The wanted parameter
 * @see "https://www.baeldung.com/kotlin/parsing-url-string"
 * @return the wanted parameter value or null
* */

fun URL.findParameterValue(parameterName: String): String? {
    return query.split('&').map {
        val parts = it.split('=')
        val name = parts.firstOrNull() ?: ""
        val value = parts.drop(1).firstOrNull() ?: ""
        Pair(name, value)
    }.firstOrNull{it.first == parameterName}?.second
}