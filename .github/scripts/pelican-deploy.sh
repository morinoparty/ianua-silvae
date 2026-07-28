#!/usr/bin/env bash
# Replace the server jar on the production server via the Pelican Panel
# client API, then restart the server.
#
# Required environment variables:
#   PANEL_URL      - panel URL (e.g. https://panel.example.com)
#   SERVER_UUID    - target server UUID
#   API_KEY        - client API key (pacc_...)
#   SERVER_VERSION - version being deployed
#
# Upload source:
#   ./artifacts/ianua-silvae-<version>.jar
#
# Unlike a Bukkit plugin, this is a standalone server jar: the startup
# command references a fixed file name, so the jar is uploaded to the
# server root as "ianua-silvae.jar" regardless of version.
set -euo pipefail

api() {
  local method="$1" path="$2"
  shift 2
  curl -fsS -X "$method" "$PANEL_URL/api/client/servers/$SERVER_UUID$path" \
    -H "Authorization: Bearer $API_KEY" -H "Accept: application/json" "$@"
}

src="./artifacts/ianua-silvae-${SERVER_VERSION}.jar"
[ -f "$src" ] || { echo "Jar not found: $src" >&2; exit 1; }

# Rename to the stable file name the startup command expects.
cp "$src" ./artifacts/ianua-silvae.jar

# Delete any previous server jar first so stale or versioned copies
# cannot linger next to the new one. Only ianua-silvae jars are touched.
pattern='^ianua-silvae([-_].*)?\.jar$'
old=$(api GET "/files/list?directory=%2F" \
  | jq -r --arg re "$pattern" '.data[] | select(.attributes.is_file) | .attributes.name | select(test($re))')
if [ -n "$old" ]; then
  files=$(printf '%s\n' "$old" | jq -R . | jq -cs .)
  api POST "/files/delete" -H "Content-Type: application/json" \
    -d "{\"root\":\"/\",\"files\":$files}"
  echo "Removed old jar(s):"
  echo "$old"
else
  echo "No old server jar found."
fi

upload_url=$(api GET "/files/upload" | jq -r '.attributes.url')
curl -fsS -X POST "$upload_url&directory=%2F" -F "files=@./artifacts/ianua-silvae.jar"
echo "Uploaded ianua-silvae.jar (version ${SERVER_VERSION})."

api POST "/power" -H "Content-Type: application/json" -d '{"signal":"restart"}'
echo "Restart signal sent."
