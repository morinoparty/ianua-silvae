// Next.js static export writes the static search index to `out/api/search` (a file).
// Static hosting (GitHub Pages / S3) needs it at `out/api/search/index.json`
// so the client can fetch `/api/search/index.json`.
import { existsSync, mkdirSync, renameSync } from "fs";
import { join } from "path";

const outDir = "out";
const searchFile = join(outDir, "api", "search");

if (existsSync(searchFile)) {
	const tempFile = join(outDir, "api", "search.tmp");
	renameSync(searchFile, tempFile);
	mkdirSync(join(outDir, "api", "search"), { recursive: true });
	renameSync(tempFile, join(outDir, "api", "search", "index.json"));
}
