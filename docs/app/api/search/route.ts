import { createFromSource } from "fumadocs-core/search/server";
import { source } from "@/lib/source";

// Build a static search index at export time
export const revalidate = false;

export const { staticGET: GET } = createFromSource(source);
