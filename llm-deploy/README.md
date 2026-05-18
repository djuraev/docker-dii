# Deploy a Lightweight LLM with Docker

Run a real LLM locally. No cloud, no API key, no GPU required.

Stack:
- **Ollama** — model runtime (handles GGUF quantized models, exposes REST API on `:11434`)
- **Open WebUI** — ChatGPT-style web UI on `:3000`, talks to Ollama by service name

CPU-only friendly. ~4 GB RAM enough for small models. GPU optional.

---

## Step 1 — Boot the stack

```bash
docker compose up -d
```

Confirm both containers are healthy:

```bash
docker compose ps
```

## Step 2 — Pull a lightweight model

Pick one based on your hardware (RAM):

| Model            | Size  | RAM needed | Good for                |
|------------------|-------|------------|-------------------------|
| `tinyllama`      | 637 MB | 2 GB      | Tiny demo, fast replies |
| `phi3:mini`      | 2.2 GB | 4 GB      | Strong reasoning, small |
| `qwen2.5:1.5b`   | 1.0 GB | 2 GB      | Fast, multilingual      |
| `llama3.2:3b`    | 2.0 GB | 4 GB      | Best balance            |
| `gemma3:1b`      | 815 MB | 2 GB      | Google's tiny model     |

Pull it via Ollama (runs inside the container):

```bash
docker compose exec ollama ollama pull llama3.2:3b
```

Watch the download. Model is cached in the `ollama` volume — pull once, use forever.

## Step 3 — Quick test via API

```bash
curl http://localhost:11434/api/generate -d '{
  "model": "llama3.2:3b",
  "prompt": "Why is the sky blue? Answer in one sentence.",
  "stream": false
}'
```

You get a JSON response with the model's answer.

## Step 4 — Chat via Web UI

Open in browser:

```
http://localhost:3000
```

Pick `llama3.2:3b` from the model dropdown. Chat.

## Step 5 — Stream tokens (real-time)

```bash
curl http://localhost:11434/api/generate -d '{
  "model": "llama3.2:3b",
  "prompt": "Write a haiku about Docker."
}'
```

Without `"stream": false`, Ollama streams JSON chunks token-by-token.

## Step 6 — List installed models

```bash
docker compose exec ollama ollama list
```

## Step 7 — Pull another model and compare

```bash
docker compose exec ollama ollama pull tinyllama
```

Switch between them in the WebUI dropdown. Same prompt, different speed/quality.

---

## GPU acceleration (optional, NVIDIA only)

1. Install `nvidia-container-toolkit` on the host
2. Uncomment the `deploy.resources.reservations.devices` block in `compose.yaml`
3. Rebuild:

```bash
docker compose down
docker compose up -d
```

Verify GPU is seen:

```bash
docker compose exec ollama nvidia-smi
```

Token throughput typically jumps 5–20×.

Apple Silicon: Ollama on macOS uses Metal natively, but **only when installed on the host** — Docker on Mac runs in a Linux VM with no GPU passthrough. For max speed on Mac, install Ollama natively (`brew install ollama`) and skip Docker for the model server.

---

## Use it from your own app

Ollama speaks an OpenAI-compatible API too:

```bash
curl http://localhost:11434/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama3.2:3b",
    "messages": [{"role":"user","content":"Hello"}]
  }'
```

Point any OpenAI client (`openai` SDK, LangChain, etc.) at `http://localhost:11434/v1` with a dummy API key.

---

## Service discovery (ties back to the network demo)

In `compose.yaml`, the WebUI talks to Ollama using its **service name**:

```yaml
OLLAMA_BASE_URL: http://ollama:11434
```

Both services on the auto-created compose network. No IPs. Same pattern as the `mini-app` stack.

---

## Teardown

```bash
docker compose down          # stop, keep models
docker compose down -v       # stop + delete all downloaded models
```

## Troubleshooting

- **Slow responses on CPU**: pick a smaller model (`tinyllama`, `qwen2.5:1.5b`)
- **Out of memory**: smaller model, or close other apps
- **WebUI shows no models**: confirm `ollama list` returns the model; check `OLLAMA_BASE_URL` env
- **`pull` fails**: network issue. Retry. Models hosted on `registry.ollama.ai`
