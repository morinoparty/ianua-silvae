package party.morino.ianuasilvae

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.Auth
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.instance.Instance
import net.minestom.server.ping.Status
import org.slf4j.LoggerFactory
import party.morino.ianuasilvae.config.ConfigLoader
import party.morino.ianuasilvae.config.LobbyConfig
import party.morino.ianuasilvae.config.toPos
import party.morino.ianuasilvae.world.SchematicWorld
import java.nio.file.Path

private val logger = LoggerFactory.getLogger("party.morino.ianuasilvae.Main")

/**
 * Entrypoint of Ianua Silvae, the fallback lobby for the morino.party network.
 */
fun main() {
    val config = ConfigLoader.load(Path.of("config/config.json"))

    val server =
        when {
            !config.velocitySecret.isNullOrBlank() -> {
                logger.info("Velocity modern forwarding enabled")
                MinecraftServer.init(Auth.Velocity(config.velocitySecret))
            }
            config.onlineMode -> {
                logger.info("Mojang online-mode authentication enabled")
                MinecraftServer.init(Auth.Online())
            }
            else -> {
                logger.warn("Running without authentication (offline mode). Keep this server behind the proxy firewall.")
                MinecraftServer.init()
            }
        }

    val instance = SchematicWorld.create(config)
    registerListeners(config, instance)

    server.start(config.bind, config.port)
    logger.info("Ianua Silvae is listening on {}:{}", config.bind, config.port)
}

private fun registerListeners(
    config: LobbyConfig,
    instance: Instance,
) {
    val events = MinecraftServer.getGlobalEventHandler()
    val spawn = config.spawn.toPos()

    // Runs during the configuration phase, including proxy server switches.
    // The spawning instance MUST be set here or the player gets kicked.
    events.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
        event.spawningInstance = instance
        event.player.respawnPoint = spawn
    }

    events.addListener(PlayerSpawnEvent::class.java) { event ->
        if (event.isFirstSpawn) {
            event.player.gameMode = GameMode.ADVENTURE
            Audiences.players().sendMessage(
                Component.text("${event.player.username} joined the lobby", NamedTextColor.YELLOW),
            )
        }
    }

    events.addListener(PlayerDisconnectEvent::class.java) { event ->
        Audiences.players().sendMessage(
            Component.text("${event.player.username} left the lobby", NamedTextColor.YELLOW),
        )
    }

    // Teleport players back to spawn when they fall into the void.
    events.addListener(PlayerMoveEvent::class.java) { event ->
        if (event.newPosition.y() < config.voidY) {
            event.player.teleport(spawn)
        }
    }

    // Keep this handler cheap: Velocity pings it to decide availability.
    events.addListener(ServerListPingEvent::class.java) { event ->
        event.setStatus(
            Status
                .builder()
                .description(Component.text(config.motd.description, NamedTextColor.GREEN))
                .playerInfo(
                    Status.PlayerInfo
                        .builder()
                        .onlinePlayers(MinecraftServer.getConnectionManager().onlinePlayerCount)
                        .maxPlayers(config.motd.maxPlayers)
                        .build(),
                ).build(),
        )
    }
}
