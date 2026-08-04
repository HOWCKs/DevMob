/**
 * Generic server-side client for AIsa's non-chat APIs (/apis/v1).
 * Usage: echo '{"query":"AI agents"}' | AISA_API_KEY=... npm run aisa:api -- /search
 */
const apiKey = process.env.AISA_API_KEY;
const baseUrl = (process.env.AISA_BASE_URL || "https://api.aisa.one").replace(/\/$/, "");
const path = process.argv[2];

if (!apiKey) throw new Error("AISA_API_KEY is required. Load it from your secret store or an ignored .env file.");
if (!path?.startsWith("/") || path.includes("://") || path.includes("..")) {
  throw new Error("Pass a safe relative API path, for example: /search");
}
let body = "";
for await (const chunk of process.stdin) body += chunk;
if (!body.trim()) throw new Error("Provide a JSON request body on standard input.");
try { JSON.parse(body); } catch { throw new Error("Standard input must be valid JSON."); }

const response = await fetch(`${baseUrl}/apis/v1${path}`, {
  method: "POST",
  headers: { Authorization: `Bearer ${apiKey}`, "Content-Type": "application/json" },
  body
});
if (!response.ok) throw new Error(`AIsa API request failed (${response.status}): ${await response.text()}`);
console.log(JSON.stringify(await response.json(), null, 2));
