package party.morino.ianuasilvae.world

import net.hollowcube.schem.reader.SchematicReader
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.instance.Chunk
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.LightingChunk
import org.slf4j.LoggerFactory
import party.morino.ianuasilvae.config.LobbyConfig
import party.morino.ianuasilvae.config.toPos
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

/**
 * The lobby instance together with the Y of the lowest pasted block
 * (`null` when no schematic was pasted).
 */
data class LobbyWorld(
    val instance: InstanceContainer,
    val schematicBottomY: Double?,
)

/**
 * Creates the void lobby instance and pastes the configured schematic into it.
 */
object SchematicWorld {
    private val logger = LoggerFactory.getLogger(SchematicWorld::class.java)

    /**
     * Creates a void [InstanceContainer] with lighting-capable chunks and
     * pastes the schematic from [config] at the configured origin.
     */
    fun create(config: LobbyConfig): LobbyWorld {
        val instance = MinecraftServer.getInstanceManager().createInstanceContainer()
        // Void world: no generator, only vanilla-style lighting.
        instance.setChunkSupplier(::LightingChunk)
        return LobbyWorld(instance, pasteSchematic(instance, config))
    }

    /** Pastes the schematic and returns the Y of its lowest block, or `null` if nothing was pasted. */
    private fun pasteSchematic(
        instance: InstanceContainer,
        config: LobbyConfig,
    ): Double? {
        val path = Path.of(config.schematicPath)
        if (!Files.exists(path)) {
            logger.warn("Schematic not found at {}. The lobby stays an empty void world.", path.toAbsolutePath())
            return null
        }
        val schematic = SchematicReader.detecting().read(Files.readAllBytes(path))
        val origin = config.schematicOrigin.toPos()
        val spawn = config.spawn.toPos()

        // The paste region may extend in any direction from the origin
        // (schematics carry their own offset), so cover every candidate corner
        // plus the spawn point when preloading chunks.
        val shifted = origin.add(schematic.offset())
        val corners =
            listOf(
                origin,
                shifted,
                origin.add(schematic.size()),
                shifted.add(schematic.size()),
                spawn,
            )
        preloadChunks(instance, corners)

        // Blocks land at origin + offset + (x, y, z), so the lowest one is at shifted.y.
        val bottomY = shifted.y()
        val minY = instance.cachedDimensionType.minY()
        if (bottomY < minY) {
            logger.warn(
                "Schematic {} reaches down to y={} but the dimension starts at y={}; blocks below are dropped",
                path.fileName,
                bottomY,
                minY,
            )
        }

        schematic.createBatch().apply(instance, origin) {
            LightingChunk.relight(instance, instance.chunks)
            logger.info("Schematic {} pasted at {}", path.fileName, origin)
        }
        return bottomY
    }

    private fun preloadChunks(
        instance: InstanceContainer,
        corners: List<Point>,
    ) {
        val min = Vec(corners.minOf { it.x() }, 0.0, corners.minOf { it.z() })
        val max = Vec(corners.maxOf { it.x() }, 0.0, corners.maxOf { it.z() })
        val futures = ArrayList<CompletableFuture<Chunk>>()
        for (chunkX in min.chunkX()..max.chunkX()) {
            for (chunkZ in min.chunkZ()..max.chunkZ()) {
                futures += instance.loadChunk(chunkX, chunkZ)
            }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).join()
        logger.info("Preloaded {} chunks for the lobby", futures.size)
    }
}
