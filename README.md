# DevMob — AIsa integration starter

This repository has safe, server-side starters for AIsa's two API families:

- **LLM gateway:** `https://api.aisa.one/v1` (OpenAI-compatible chat)
- **Data/capability APIs:** `https://api.aisa.one/apis/v1`

## Security first

Do not put an AIsa key in source code, browser code, commits, issue text, terminal history, or screenshots. `.env` is ignored by Git and `.env.example` contains no secrets. Create a dedicated **development** key in the AIsa Console with a low spend cap, a narrow model allowlist, and rate limits. Rotate it at least every 90 days and immediately after exposure.

> A client-side app must call a server-side proxy that authenticates callers and rate-limits requests; it must never receive `AISA_API_KEY`.

## Local configuration

```bash
cp .env.example .env
# Edit .env locally and add a newly created restricted development key.
set -a; . ./.env; set +a
```

The scripts require Node 18+ and use its built-in `fetch`, so no package install is needed.

## Safe LLM smoke test

```bash
set -a; . ./.env; set +a
npm run aisa:chat -- "Reply with exactly: AIsa connection verified"
```

Defaults use `gpt-5-mini`, a conversational/coding model. Change `AISA_CHAT_MODEL` only to a model supported by `POST /v1/chat/completions`; do not use an image, video, embedding, Whisper, or TTS model for chat.

## Non-chat API calls

Check the exact request body in AIsa's API reference for the endpoint before sending a request. The helper only accepts a relative path and JSON via stdin:

```bash
set -a; . ./.env; set +a
echo '{"query":"AI agent frameworks"}' | npm run aisa:api -- /search
```

## Agent Skills

Skills are installed into the agent's local skills directory and are loaded by **new sessions**. Review a skill before installing it:

```bash
npm install -g @aisa-one/cli
aisa whoami
aisa skills search "search"
aisa skills show search
aisa skills install search
```

Set `AISA_API_KEY` in the agent runtime's secret store (preferred) or your ignored `.env` before `aisa whoami`; do not pass it as a command argument. Use `aisa skills remove <slug>` to remove a skill. Skills can invoke billable APIs, so install only those you need and review their `SKILL.md`.

## Production checklist

1. Use a server/runtime secret manager, not a repository `.env`.
2. Use separate dev, CI, staging, and production keys.
3. Apply spend caps, per-key rate limits, and a model allowlist in the AIsa Console.
4. Validate callers and impose per-user quotas before proxying requests.
5. Monitor Usage Logs, configure budget alerts, and rotate keys.

Official docs: [Quickstart](https://aisa.one/docs/agent-quickstart.md), [Authentication](https://aisa.one/docs/guides/authentication), [Models](https://aisa.one/docs/guides/models), and [Skills Quickstart](https://aisa.one/docs/agent-skills/quickstart).
