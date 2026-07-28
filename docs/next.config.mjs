import { createMDX } from "fumadocs-mdx/next";

const withMDX = createMDX();

/** @type {import('next').NextConfig} */
const config = {
	reactStrictMode: true,
	// Fully static export for GitHub Pages
	output: "export",
	trailingSlash: true,
	// BASE_PATH is set by CI for subpath deployments (e.g. PR previews)
	basePath: process.env.BASE_PATH || "",
	images: {
		unoptimized: true,
	},
};

export default withMDX(config);
