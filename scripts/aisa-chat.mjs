/**
 * Minimal server-side AIsa chat client. Requires Node 18+ and AISA_API_KEY.
 * Usage: AISA_API_KEY=... npm run aisa:chat -- "Explain rate limiting in one sentence."
 */
const apiKey = process.env.AISA_API_KEY;
const baseUrl = (process.env.AISA_BASE_URL || "https://api.aisa.one").replace(/\/$/, "");
const model = process.env.AISA_CHAT_MODEL || "gpt-5-mini";
const prompt = process.argv.slice(2).join(" ").trim();

if (!apiKey) throw new Error("AISA_API_KEY is required. Load it from your secret store or an ignored .env file.");
if (!prompt) throw new Error("Usage: npm run aisa:chat -- \"Your prompt\"");

const response = await fetch(`${baseUrl}/v1/chat/completions`, {
  method: "POST",
  headers: { Authorization: `Bearer ${apiKey}`, "Content-Type": "application/json" },
  body: JSON.stringify({
    model,
    messages: [{ role: "user", content: prompt }],
    max_tokens: 250
  })
});

if (!response.ok) {
  // Do not include request headers or key material in errors.
  throw new Error(`AIsa chat request failed (${response.status}): ${await response.text()}`);
}
const data = await response.json();
console.log(data.choices?.[0]?.message?.content ?? "No completion returned.");
