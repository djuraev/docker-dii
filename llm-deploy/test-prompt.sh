#!/usr/bin/env bash
# Quick sanity check: hit Ollama API directly.
# Usage: ./test-prompt.sh [model] [prompt]

MODEL="${1:-llama3.2:3b}"
PROMPT="${2:-Explain Docker in one sentence.}"

curl -s http://localhost:11434/api/generate -d "{
  \"model\": \"$MODEL\",
  \"prompt\": \"$PROMPT\",
  \"stream\": false
}" | jq -r '.response'
