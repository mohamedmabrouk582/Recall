package com.mabrouk.recall.data.ai.store

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.ByteArrayInputStream

class Sha256VerifierTest {

    private val verifier = Sha256Verifier()

    @Test
    fun hash_isStableLowercaseHex() {
        val bytes = "recall-model".toByteArray()
        val first = verifier.hash(ByteArrayInputStream(bytes))
        val second = verifier.hash(ByteArrayInputStream(bytes))
        assertThat(first).isEqualTo(second)
        assertThat(first).matches("[0-9a-f]{64}")
    }

    @Test
    fun matches_acceptsExpectedDigest() {
        val bytes = "recall-model".toByteArray()
        val digest = verifier.hash(ByteArrayInputStream(bytes))
        assertThat(verifier.matches(digest, ByteArrayInputStream(bytes))).isTrue()
        assertThat(verifier.matches(digest.uppercase(), ByteArrayInputStream(bytes))).isTrue()
        assertThat(verifier.matches("00".repeat(32), ByteArrayInputStream(bytes))).isFalse()
    }
}
