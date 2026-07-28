# Recommended patterns

## Configuration

- Define configuration as immutable `@Serializable` data classes with sensible defaults for every field.
- Resolve values in this order: environment variable → config file → default. Document the environment variable name for every option.
- Validate configuration at startup (e.g. the schematic file exists, the port is valid) and fail fast with a clear error message.

## Minestom events and instances

- Register event listeners on dedicated `EventNode`s grouped by concern, and attach them to the global event handler at startup — avoid scattering listener registration across the codebase.
- Create the void instance once at startup and reuse it; this server hosts a single lobby instance.
- Implement the void-fall check via the player move event with a configurable Y threshold, and teleport the player back to the configured spawn position.
- Force the adventure gamemode on spawn so players cannot break or place blocks.

## Messages

- Use Adventure components for all player-facing text; prefer MiniMessage strings over legacy formatting codes.
- Keep player-facing strings (MOTD, messages) configurable rather than hard-coded where reasonable.

## Robustness

- This is a last-resort fallback server: prefer boring, dependency-light solutions and fast startup over features.
- Handle failures gracefully — a malformed schematic or config must produce a clear startup error, not a half-started server.
- Log with a proper logger (slf4j), not `println`; use English log messages.

## Tests

- Keep tests to covering the main branch points; avoid excessive exhaustiveness.
- Write test case descriptions in English, short, using `@DisplayName`.
- Focus tests on pure logic (config resolution/overrides, spawn/threshold math) rather than spinning up a full server.
