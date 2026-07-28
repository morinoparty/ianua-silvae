# Directory layout rules

## Project structure

The project is a single Gradle module (Kotlin DSL) plus a documentation site.

## Root

- `build.gradle.kts` — build script for the single module
- `settings.gradle.kts` — project settings
- `gradle/libs.versions.toml` — version catalog; all dependency versions live here

## `src/main/kotlin/party/morino/ianuasilvae/` — server source

- `Main.kt` — entry point: loads config, starts the Minestom server, enables Velocity forwarding
- `config/` — `@Serializable` config data classes, JSON loading, environment-variable overrides
- `world/` — void instance creation and schematic loading/pasting
- `listener/` — event handlers (player join/spawn, void-fall teleport, gamemode enforcement)
- `util/` — small generic helpers

One top-level class per file; the file name matches the class name.

## `src/main/resources/`

- Default configuration template and other bundled static resources

## `src/test/kotlin/party/morino/ianuasilvae/`

- Tests, mirroring the main source package structure

## `docs/` — documentation site

- `docs/content/` — Fumadocs mdx documents
- `docs/app/` — Next.js app source
- `docs/public/` — static assets

## `.github/`

- `workflows/` — GitHub Actions workflows (build/lint, documentation deployment)

## Runtime files (not committed)

- `config/config.json` — server configuration (created with defaults on first start)
- `schematics/lobby.schem` — the schematic used as world data, path set in the config
