# Ianua Silvae

[![Check pull request](https://github.com/morinoparty/ianua-silvae/actions/workflows/check_pull_request.yml/badge.svg)](https://github.com/morinoparty/ianua-silvae/actions/workflows/check_pull_request.yml)
[![License: CC0-1.0](https://img.shields.io/badge/license-CC0--1.0-lightgrey.svg)](https://creativecommons.org/publicdomain/zero/1.0/)

*Ianua Silvae* (Latin: "gate of the forest") is a lightweight fallback lobby server for the [morino.party](https://morino.party) Minecraft network.

## 📌 Overview

The main lobby of morino.party runs on PaperMC. When it goes down, the Velocity proxy plugin **vlobby** (using its `FIRST_AVAILABLE` strategy) routes players to Ianua Silvae as the last-resort lobby, so players are never kicked from the network entirely.

Ianua Silvae is built on [Minestom](https://minestom.net/) — not Bukkit/Paper — and loads its world from a schematic file instead of a vanilla world folder. It does one job: stay up, hold players, and hand them back once a real lobby returns.

## ✨ Features

- **Schematic-based world** - A void instance with your lobby schematic pasted at a configured origin; no world folder needed
- **Velocity modern forwarding** - Runs safely behind the proxy with the forwarding secret from config or environment
- **Tiny footprint** - Minestom core only; starts in seconds and idles on minimal memory
- **Safe by default** - Adventure gamemode, and players who fall into the void are teleported back to spawn

## 🚀 Quick Start

Requires JDK 25 or newer.

1. Clone and build:

   ```sh
   git clone https://github.com/morinoparty/ianua-silvae.git
   cd ianua-silvae
   ./gradlew shadowJar
   ```

   The runnable (shadow) jar is written to `build/libs/ianua-silvae-<version>.jar`.

2. Place your schematic at `schematics/lobby.schem` next to the jar (the path is configurable)
3. Run the server:

   ```sh
   java -jar ianua-silvae-<version>.jar
   ```

   A default `config/config.json` is generated on first start.

### Configuration

Settings are read from `config/config.json` (kotlinx-serialization JSON); most of them can be overridden with environment variables:

| Config key         | Environment variable    | Default                         | Description                                    |
| ------------------ | ----------------------- | ------------------------------- | ---------------------------------------------- |
| `bind`             | `IANUA_BIND`            | `0.0.0.0`                       | Address to bind to                             |
| `port`             | `IANUA_PORT`            | `25565`                         | Port to listen on                              |
| `onlineMode`       | `IANUA_ONLINE_MODE`     | `false`                         | Mojang authentication (keep `false` behind Velocity) |
| `velocitySecret`   | `IANUA_VELOCITY_SECRET` | *(null)*                        | Velocity modern forwarding secret              |
| `schematicPath`    | `IANUA_SCHEMATIC_PATH`  | `schematics/lobby.schem`        | Path to the schematic used as the world        |
| `schematicOrigin`  | —                       | `{0, 64, 0}`                    | Position the schematic is pasted at            |
| `spawn`            | —                       | `{0.5, 65, 0.5}`                | Spawn position (`x`/`y`/`z`/`yaw`/`pitch` object) |
| `voidY`            | —                       | `-32`                           | Below this Y, players are teleported to spawn  |
| `motd.description` | `IANUA_MOTD`            | `morino.party \| fallback lobby`| Server list MOTD                               |
| `motd.maxPlayers`  | —                       | `200`                           | Max player count shown in the server list      |

Environment variables always take precedence over the config file — convenient for containers. See the [Configuration docs](https://ianua-silvae.morino.party/docs/configuration) for details.

## 🛰️ Deployment

Register Ianua Silvae **last** in vlobby's lobby list. With the `FIRST_AVAILABLE` strategy, vlobby picks the first reachable lobby in order, so placing this server at the end means it only receives players when every real lobby is down:

```yaml
strategy: FIRST_AVAILABLE
lobbies:
  - lobby-1
  - lobby-2
  - ianua-silvae   # fallback: keep this last
```

Set the same forwarding secret on the proxy and in `IANUA_VELOCITY_SECRET`.

## 🛠️ Development

- JDK 25, Kotlin + Gradle (Kotlin DSL) with a version catalog (`gradle/libs.versions.toml`)
- Code style is enforced with ktlint:

  ```sh
  ./gradlew ktlintCheck ktlintFormat
  ```

- Build a runnable jar with `./gradlew shadowJar`

## 📚 Documentation

Docs: https://ianua-silvae.morino.party

## 📄 License

[CC0-1.0](https://creativecommons.org/publicdomain/zero/1.0/) — to the extent possible under law, [morinoparty](https://github.com/morinoparty) has waived all copyright and related rights to this work (2026). See [LICENSE](LICENSE).
