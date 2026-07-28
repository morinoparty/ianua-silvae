# Ianua Silvae

A fallback lobby server for the [morino.party](https://morino.party) Minecraft network. *Ianua Silvae* (Latin: "gate of the forest") is a tiny standalone server built on [Minestom](https://minestom.net/) that loads its world from a schematic file. When the main PaperMC lobby goes down, the Velocity plugin vlobby (`FIRST_AVAILABLE` strategy) routes players here instead of kicking them off the network.

## Features

- **Schematic-based world**: A void instance with the lobby schematic pasted at a configured origin — no world folder needed
- **Velocity modern forwarding**: Runs safely behind the proxy with the forwarding secret from config or environment
- **Tiny footprint**: Minestom core only; starts in seconds and idles on minimal memory
- **Safe by default**: Adventure gamemode, and players who fall into the void are teleported back to spawn
- **Simple configuration**: A single `config/config.json` (generated on first start) with `IANUA_*` environment-variable overrides

See the [documentation site](https://morinoparty.github.io/ianua-silvae/) for configuration and deployment details.

Release artifacts are signed with GitHub Attestations, so their SLSA provenance can be verified. Run `gh attestation verify <jar-file> --owner morinoparty` to check.

## Modules

| Module | Description |
|--------|-------------|
| `src` | Server implementation (config, world loading, listeners) |
| `docs` | Documentation site built with Fumadocs (Next.js) |

## Tech stack

- **Kotlin** 2.4 / **Java** 25 (Temurin)
- **Minestom** - standalone Minecraft server library
- **hollow-cube/schem** - schematic loading
- **kotlinx.serialization** - config file serialization
- **JUnit** - testing
- **Fumadocs** - documentation site (Next.js)

## Requirements

- **Java** 25 (Temurin recommended)
- **Gradle** 9.x (wrapper included)
- **Node.js** 22+ / **pnpm** 10+ (for building the docs)

## Build & test

```bash
./gradlew build        # compile + test + ktlint
./gradlew shadowJar    # runnable jar in build/libs/ianua-silvae-<version>.jar
```

## Run

```bash
# place your schematic at schematics/lobby.schem (path is configurable)
java -jar build/libs/ianua-silvae-<version>.jar
```

A default `config/config.json` is generated on first start. Every connection setting can be overridden with environment variables (`IANUA_BIND`, `IANUA_PORT`, `IANUA_VELOCITY_SECRET`, `IANUA_SCHEMATIC_PATH`, `IANUA_MOTD`, ...) — see the [configuration docs](https://morinoparty.github.io/ianua-silvae/docs/configuration).

When deploying, register Ianua Silvae **last** in vlobby's lobby list so it only receives players when every real lobby is down.

## Docs development

```bash
cd docs
pnpm install
pnpm dev
```

## License

Written in 2026 by Morinoparty developer team. No Rights Reserved.

To the extent possible under law, morinoparty has waived all copyright and related or neighboring rights to Ianua Silvae. This work is published from: Japan.

You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see http://creativecommons.org/publicdomain/zero/1.0/.

This CC0 dedication applies to the source code only. Non-code assets (images, icons, logos, and other media) are **not** covered and remain under their respective rights unless stated otherwise.
