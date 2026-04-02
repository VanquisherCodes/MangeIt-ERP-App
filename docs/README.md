# ManageIt Documentation

Use this folder for project-level architecture and delivery artifacts.

- `uml/`: system, package, sequence, and navigation diagrams.
- `erd/`: MySQL schema and table-relationship diagrams.
- `api/`: REST endpoint contracts, request/response examples, auth flow notes.
- `ui/`: UI specification, Stitch-to-XML mapping, screen state behavior.

Recommended update rule:
- Any API/model change should be reflected in both `docs/api` and `docs/erd` (if DB-impacting).
