package com.mabrouk.recall.data.ai.store

import android.content.Context
import com.mabrouk.recall.data.ai.model.ModelSpec
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelStore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sha256Verifier: Sha256Verifier,
) {

    fun modelFile(spec: ModelSpec): File =
        File(context.filesDir, "models/${spec.id}/${spec.version}/model.tflite")

    fun tempFile(spec: ModelSpec): File =
        File(context.filesDir, "models/${spec.id}/${spec.version}/model.tflite.tmp")

    fun existsAndValid(spec: ModelSpec): Boolean {
        val file = modelFile(spec)
        if (!file.isFile || file.length() == 0L) return false
        return file.inputStream().use { stream ->
            sha256Verifier.matches(spec.sha256, stream)
        }
    }

    fun deleteCorrupt(spec: ModelSpec) {
        modelFile(spec).delete()
        tempFile(spec).delete()
    }

    /**
     * Publishes [temp] to the final model path after hash verification.
     * Deletes corrupt temps and throws if the hash does not match.
     */
    fun publishIfValid(spec: ModelSpec, temp: File): File {
        require(temp.isFile) { "Temp model missing: ${temp.absolutePath}" }
        val valid = temp.inputStream().use { sha256Verifier.matches(spec.sha256, it) }
        if (!valid) {
            temp.delete()
            throw IllegalStateException("SHA-256 mismatch for model ${spec.id}@${spec.version}")
        }
        val target = modelFile(spec)
        target.parentFile?.mkdirs()
        if (target.exists()) target.delete()
        if (!temp.renameTo(target)) {
            temp.copyTo(target, overwrite = true)
            temp.delete()
        }
        return target
    }
}
