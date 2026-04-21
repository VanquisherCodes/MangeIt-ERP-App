# Stitch MCP Plugin

This plugin exposes Google Stitch inside Codex through the official
`@google/stitch-sdk` proxy.

## What is included

- `.codex-plugin/plugin.json` for Codex plugin metadata
- `.mcp.json` to register the local Stitch MCP launcher
- `scripts/start-stitch-mcp.mjs` to bridge Codex stdio to the official Stitch MCP
- `package.json` for the required runtime dependencies

## Setup

1. Install the plugin dependencies:

   ```bash
   cd /Users/kanishkk/AndroidStudioProjects/ManageIt/plugins/stitch-mcp
   npm install
   ```

2. Export your Stitch API key before launching Codex:

   ```bash
   export STITCH_API_KEY="your-api-key"
   ```

3. Optional: point the plugin at a custom Stitch MCP URL:

   ```bash
   export STITCH_MCP_URL="https://stitch.googleapis.com/mcp"
   ```

## Usage

After Codex loads the plugin, ask for Stitch tasks like:

- "Create a Stitch project for this Android app"
- "Generate a mobile dashboard screen in Stitch"
- "Edit the last Stitch screen to use a calmer color palette"

## References

- Stitch MCP setup: https://stitch.withgoogle.com/docs/mcp/setup
- Official SDK: https://github.com/google-labs-code/stitch-sdk
