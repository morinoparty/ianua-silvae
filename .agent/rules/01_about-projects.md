# About this project

## Overview

"ianua-silvae" (Latin: "gate of the forest") is a lightweight fallback lobby server for the morino.party Minecraft network.

The main lobby runs on PaperMC. When it goes down, the Velocity proxy plugin "vlobby" (using its FIRST_AVAILABLE strategy) routes players to this server as the last-resort lobby. This server therefore has to be simple, robust, and fast to start.

It is a standalone server built on Minestom — NOT a Bukkit/Paper plugin. There is no vanilla world folder: the server creates a void-world instance and pastes a schematic file (e.g. `lobby.schem`) into it as the world data.

Key behavior:

- Void-world instance with the schematic pasted at a configured origin
- Players spawn at a configured position and are teleported back to spawn when they fall into the void
- Adventure (cheat-safe) gamemode for players
- Runs behind Velocity, so it supports Velocity modern forwarding (secret provided via config or environment variable)
- Configuration is a simple JSON file (kotlinx-serialization) with environment-variable overrides: bind address/port, Velocity secret, schematic path, spawn position, MOTD

## Tech stack

### Server (`src/`)

- **Minestom** — lightweight Minecraft server library (standalone, no Bukkit/Paper API)
- **Kotlin** — implementation language
- **kotlinx-serialization** — JSON configuration loading
- **Gradle (Kotlin DSL)** — build system, single module, with a version catalog (`gradle/libs.versions.toml`)
- **ktlint** — code formatting/linting, following the shared morinoparty conventions (group `party.morino`)

The package root is `party.morino.ianuasilvae`.

### Documentation site (`docs/`)

- **Next.js + Fumadocs** — documentation site
- **GitHub Pages** — deployment target for the documentation

### Infrastructure / CI

- **GitHub Actions** — build, lint, and documentation deployment workflows
