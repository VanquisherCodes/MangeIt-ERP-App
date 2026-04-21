import { StitchProxy } from "@google/stitch-sdk";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";

const apiKey = process.env.STITCH_API_KEY?.trim();

if (!apiKey) {
  console.error(
    "Missing STITCH_API_KEY. Export it before launching Codex or set it in the plugin MCP env."
  );
  process.exit(1);
}

const proxy = new StitchProxy({
  apiKey,
  url: process.env.STITCH_MCP_URL?.trim() || undefined,
  name: "stitch",
  version: "0.1.0",
});

const transport = new StdioServerTransport();

const shutdown = async () => {
  await proxy.close();
  process.exit(0);
};

process.on("SIGINT", shutdown);
process.on("SIGTERM", shutdown);

await proxy.start(transport);
