# Coding practices

## Implementation order

1. **Design the types first**
   - Define types (interfaces / data classes) before writing logic

2. **Implement pure functions first**
   - Implement functions without external dependencies before wiring them into the server

## Practices

- Start small and extend incrementally
- Avoid excessive abstraction
- Prefer types over code
- Adjust the approach to the actual complexity of the problem

## Code style (general)

- Always follow the design and conventions of the existing code.
- Always apply best practices in the spirit of "The Art of Readable Code".
- Actively add comments in English explaining the intent and background of the code.
- After writing code, confirm that the build passes with `./gradlew build`.
- Run `./gradlew ktlintCheck` (or `ktlintFormat`) and keep the code ktlint-clean.

## Kotlin / Minestom

- Target the Minestom API directly — do NOT use Bukkit/Paper/Spigot APIs or assume their lifecycle (there are no plugins, no `plugin.yml`, no Bukkit scheduler).
- The entry point is a plain `main` function; server setup (instance creation, event registration, Velocity forwarding) happens explicitly at startup.
- Use Minestom's event API (`EventNode`, global event handler) for player join, spawn, and move/void-fall handling.
- Use Adventure components (Minestom ships with Adventure) for chat messages and the MOTD — prefer MiniMessage over legacy color codes.
- Keep the world logic schematic-based: the void instance and schematic pasting are the only sources of world data; never introduce a vanilla world folder.
- Configuration classes are `@Serializable` data classes loaded with kotlinx-serialization JSON; every option must also be overridable via an environment variable.
- Never hard-code secrets (e.g. the Velocity forwarding secret); they come from the config file or environment variables only.
- Split code into small, focused files: one top-level class per file, organized by package (see the directory rules).
- Add KDoc comments (`/** ... */`) to public classes and functions.
- Manage all dependency versions in the version catalog (`gradle/libs.versions.toml`); do not inline version numbers in build scripts.

## Build and verification commands

- Build: `./gradlew build`
- Lint check: `./gradlew ktlintCheck`
- Auto-format: `./gradlew ktlintFormat`
- Run the server locally: `./gradlew run`
- Run tests: `./gradlew test`
