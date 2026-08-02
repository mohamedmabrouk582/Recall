package com.mabrouk.recall.data.ai.store

import java.io.InputStream
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Sha256Verifier @Inject constructor() {

    fun hash(input: InputStream): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (true) {
            val read = input.read(buffer)
            if (read < 0) break
            digest.update(buffer, 0, read)
        }
        return digest.digest().joinToString("") { byte -> "%02x".format(byte) }
    }

    fun matches(expectedHex: String, input: InputStream): Boolean =
        hash(input).equals(expectedHex, ignoreCase = true)

    private companion object {
        const val DEFAULT_BUFFER_SIZE = 64 * 1024
    }
}
