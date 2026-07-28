import type { MDXComponents } from "mdx/types";
import defaultComponents from "fumadocs-ui/mdx";
import { Mermaid } from "./components/mdx/mermaid";

export function getMDXComponents(components?: MDXComponents): MDXComponents {
	return {
		...defaultComponents,
		Mermaid,
		...components,
	};
}

export function useMDXComponents(components: MDXComponents): MDXComponents {
	return {
		...defaultComponents,
		Mermaid,
		...components,
	};
}
