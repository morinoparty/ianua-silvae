package party.morino.ianuasilvae.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class ConfigLoaderTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `creates default config file on first load`() {
        val path = tempDir.resolve("config/config.json")
        val config = ConfigLoader.load(path, emptyMap())
        assertTrue(Files.exists(path), "config file should be created with defaults")
        assertEquals(LobbyConfig(), config)
    }

    @Test
    fun `reads values from an existing config file`() {
        val path = tempDir.resolve("config.json")
        Files.writeString(
            path,
            """
            {
              "bind": "127.0.0.1",
              "port": 25566,
              "schematicPath": "lobby.schem",
              "motd": { "description": "Hello", "maxPlayers": 42 }
            }
            """.trimIndent(),
        )
        val config = ConfigLoader.load(path, emptyMap())
        assertEquals("127.0.0.1", config.bind)
        assertEquals(25566, config.port)
        assertEquals("lobby.schem", config.schematicPath)
        assertEquals("Hello", config.motd.description)
        assertEquals(42, config.motd.maxPlayers)
        assertNull(config.velocitySecret)
    }

    @Test
    fun `environment variables override file values`() {
        val path = tempDir.resolve("config.json")
        Files.writeString(path, """{ "port": 25566, "bind": "127.0.0.1" }""")
        val env =
            mapOf(
                "IANUA_BIND" to "10.0.0.1",
                "IANUA_PORT" to "30066",
                "IANUA_VELOCITY_SECRET" to "hunter2",
                "IANUA_SCHEMATIC_PATH" to "custom/lobby.schem",
                "IANUA_MOTD" to "Overridden",
            )
        val config = ConfigLoader.load(path, env)
        assertEquals("10.0.0.1", config.bind)
        assertEquals(30066, config.port)
        assertEquals("hunter2", config.velocitySecret)
        assertEquals("custom/lobby.schem", config.schematicPath)
        assertEquals("Overridden", config.motd.description)
    }

    @Test
    fun `invalid numeric env values fall back to config values`() {
        val path = tempDir.resolve("missing.json")
        val config = ConfigLoader.load(path, mapOf("IANUA_PORT" to "not-a-number"))
        assertEquals(LobbyConfig().port, config.port)
    }
}
