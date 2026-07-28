#!/usr/bin/env bash
# Concatenates the prompt rule files to generate the agent rule files (AGENTS.md / CLAUDE.md).

RULES_DIR=".agent/rules"
OUTPUT_FILES="AGENTS.md CLAUDE.md"

END="Now, please carry out the task according to the instructions.

<instructions>
{{instructions}}"

# Initialize the output files
for output_file in $OUTPUT_FILES; do
  echo "" > $output_file
done

# Concatenate the rule files
for file in "$RULES_DIR"/*.md; do
  if [[ -f "$file" ]]; then
    for output_file in $OUTPUT_FILES; do
      cat "$file" >> $output_file
      echo -e "\n\n" >> $output_file  # Add blank lines between files
    done
  fi
done

for output_file in $OUTPUT_FILES; do
  echo "$END" >> $output_file
done

echo "Generated $(echo $OUTPUT_FILES | tr ' ' ', ') from $(ls -1 "$RULES_DIR"/*.md | wc -l) prompt files"
