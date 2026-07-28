"use client";
import { useDocsSearch } from "fumadocs-core/search/client";
import {
	SearchDialog,
	SearchDialogClose,
	SearchDialogContent,
	SearchDialogHeader,
	SearchDialogIcon,
	SearchDialogInput,
	SearchDialogList,
	SearchDialogOverlay,
	type SharedProps,
} from "fumadocs-ui/components/dialog/search";

const basePath = process.env.NEXT_PUBLIC_BASE_PATH || "";

/**
 * Static Orama search dialog.
 *
 * The site is a static export, so the search index is prebuilt and fetched
 * from /api/search/index.json (relocated there by scripts/postbuild.mjs).
 */
export default function DefaultSearchDialog(props: SharedProps) {
	const { search, setSearch, query } = useDocsSearch({
		type: "static",
		from: `${basePath}/api/search/index.json`,
	});

	return (
		<SearchDialog search={search} onSearchChange={setSearch} isLoading={query.isLoading} {...props}>
			<SearchDialogOverlay />
			<SearchDialogContent>
				<SearchDialogHeader>
					<SearchDialogIcon />
					<SearchDialogInput />
					<SearchDialogClose />
				</SearchDialogHeader>
				<SearchDialogList items={query.data !== "empty" ? query.data : null} />
			</SearchDialogContent>
		</SearchDialog>
	);
}
