package com.mabrouk.recall.data.repository

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.mabrouk.recall.core.database.RecallDatabase
import com.mabrouk.recall.core.database.crypto.NoteCrypto
import com.mabrouk.recall.core.model.SourceType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class NoteRepositoryEncryptionTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val dbName = "recall-encryption-test.db"

    private lateinit var database: RecallDatabase
    private lateinit var repository: NoteRepository
    private lateinit var crypto: NoteCrypto

    @Before
    fun setUp() {
        context.deleteDatabase(dbName)
        crypto = NoteCrypto()
        database = openDatabase()
        repository = NoteRepositoryImpl(database.noteDao(), crypto)
    }

    @After
    fun tearDown() {
        if (::database.isInitialized && database.isOpen) {
            database.close()
        }
        context.deleteDatabase(dbName)
        File(context.getDatabasePath(dbName).path + "-wal").delete()
        File(context.getDatabasePath(dbName).path + "-shm").delete()
    }

    @Test
    fun create_survivesRestart_andPlaintextAbsentFromDisk() = runBlocking {
        val title = "UNIQUE_TITLE_PLAINTEXT_MARKER_7f3a9c"
        val body = "UNIQUE_BODY_PLAINTEXT_MARKER_2b8e1d"

        val created = repository.create(
            title = title,
            body = body,
            sourceType = SourceType.TEXT,
            tags = listOf("alpha", "beta"),
        )

        database.close()

        val dbFile = context.getDatabasePath(dbName)
        assertThat(dbFile.exists()).isTrue()
        assertPlaintextAbsentFromDisk(dbFile, title, body)

        database = openDatabase()
        repository = NoteRepositoryImpl(database.noteDao(), crypto)

        val loaded = repository.get(created.id)
        assertThat(loaded).isNotNull()
        assertThat(loaded!!.title).isEqualTo(title)
        assertThat(loaded.body).isEqualTo(body)
        assertThat(loaded.sourceType).isEqualTo(SourceType.TEXT)
        assertThat(loaded.tags).containsExactly("alpha", "beta")
        assertThat(loaded.createdAt).isEqualTo(created.createdAt)
        assertThat(loaded.id).isEqualTo(created.id)
    }

    private fun openDatabase(): RecallDatabase =
        Room.databaseBuilder(context, RecallDatabase::class.java, dbName)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    private fun assertPlaintextAbsentFromDisk(dbFile: File, title: String, body: String) {
        val files = listOf(
            dbFile,
            File(dbFile.path + "-wal"),
            File(dbFile.path + "-shm"),
        )
        for (file in files) {
            if (!file.exists() || file.length() == 0L) continue
            val text = file.readBytes().toString(Charsets.UTF_8)
            assertThat(text).doesNotContain(title)
            assertThat(text).doesNotContain(body)
        }
    }
}
