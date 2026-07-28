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
# The jar keeps its versioned release name (ianua-silvae-<version>.jar);
# the Pelican egg's startup command matches it with a wildcard
# (ianua-silvae-*.jar), so old jars must be deleted before uploading.
set -euo pipefail

api() {
  local method="$1" path="$2"
  shift 2
  curl -fsS -X "$method" "$PANEL_URL/api/client/servers/$SERVER_UUID$path" \
    -H "Authorization: Bearer $API_KEY" -H "Accept: application/json" "$@"
}

src="./artifacts/ianua-silvae-${SERVER_VERSION}.jar"
[ -f "$src" ] || { echo "Jar not found: $src" >&2; exit 1; }

# Delete any previous server jar first so the wildcard startup command
# matches exactly one file. Only ianua-silvae jars are touched.
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
curl -fsS -X POST "$upload_url&directory=%2F" -F "files=@$src"
echo "Uploaded $(basename "$src")."

api POST "/power" -H "Content-Type: application/json" -d '{"signal":"restart"}'
echo "Restart signal sent."
