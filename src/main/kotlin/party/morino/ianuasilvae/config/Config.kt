package party.morino.ianuasilvae.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minestom.server.coordinate.Pos
import java.nio.file.Files
import java.nio.file.Path

/**
 * A position in the lobby world, serializable to and from the config file.
 */
@Serializable
data class Position(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float = 0f,
    val pitch: Float = 0f,
)

/** Converts a config [Position] into a Minestom [Pos]. */
fun Position.toPos(): Pos = Pos(x, y, z, yaw, pitch)

/**
 * Server list (MOTD) settings.
 */
@Serializable
data class MotdConfig(
    val description: String = "morino.party | fallback lobby",
    val maxPlayers: Int = 200,
)

/**
 * Root configuration for the fallback lobby server.
 *
 * Loaded from `config/config.json` and optionally overridden by environment
 * variables (see [ConfigLoader.applyEnvOverrides]).
 */
@Serializable
data class LobbyConfig(
    /** Address the server binds to. */
    val bind: String = "0.0.0.0",
    /** Port the server listens on. */
    val port: Int = 25565,
    /**
     * Mojang online-mode authentication. Must stay `false` behind a Velocity
     * proxy; ignored entirely when [velocitySecret] is set.
     */
    val onlineMode: Boolean = false,
    /**
     * Velocity modern forwarding secret. When non-null and non-blank, the
     * server only accepts connections forwarded by the proxy.
     */
    val velocitySecret: String? = null,
    /** Path to the schematic pasted into the void world. */
    val schematicPath: String = "schematics/lobby.schem",
    /** World position the schematic is pasted at. */
    val schematicOrigin: Position = Position(0.0, 64.0, 0.0),
    /** Player spawn position. */
    val spawn: Position = Position(0.5, 65.0, 0.5, 0f, 0f),
    /** Players falling below this Y level are teleported back to spawn. */
    val voidY: Double = -32.0,
    val motd: MotdConfig = MotdConfig(),
)

/**
 * Loads [LobbyConfig] from disk, creating the file with defaults on first run,
 * then applies environment-variable overrides.
 */
object ConfigLoader {
    private val json =
        Json {
            prettyPrint = true
            encodeDefaults = true
            ignoreUnknownKeys = true
        }

    fun load(
        path: Path,
        env: Map<String, String> = System.getenv(),
    ): LobbyConfig {
        val base =
            if (Files.exists(path)) {
                json.decodeFromString(LobbyConfig.serializer(), Files.readString(path))
            } else {
                LobbyConfig().also { defaults ->
                    path.parent?.let(Files::createDirectories)
                    Files.writeString(path, json.encodeToString(LobbyConfig.serializer(), defaults))
                }
            }
        return applyEnvOverrides(base, env)
    }

    /**
     * Applies `IANUA_*` environment variables on top of [config].
     * Invalid numeric or boolean values are ignored.
     */
    fun applyEnvOverrides(
        config: LobbyConfig,
        env: Map<String, String>,
    ): LobbyConfig = config.copy(
        bind = env["IANUA_BIND"] ?: config.bind,
        port = env["IANUA_PORT"]?.toIntOrNull() ?: config.port,
        onlineMode = env["IANUA_ONLINE_MODE"]?.toBooleanStrictOrNull() ?: config.onlineMode,
        velocitySecret = env["IANUA_VELOCITY_SECRET"] ?: config.velocitySecret,
        schematicPath = env["IANUA_SCHEMATIC_PATH"] ?: config.schematicPath,
        motd = config.motd.copy(description = env["IANUA_MOTD"] ?: config.motd.description),
    )
}
