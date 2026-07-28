import type { BaseLayoutProps } from "fumadocs-ui/layouts/shared";

/**
 * Shared layout options used by the docs layout.
 */
export function baseOptions(): BaseLayoutProps {
	return {
		nav: {
			title: <span className="text-lg font-bold">Ianua Silvae</span>,
			transparentMode: "top",
		},
		githubUrl: "https://github.com/morinoparty/ianua-silvae",
	};
}
