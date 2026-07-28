import "./global.css";
import type { Metadata } from "next";
import type { ReactNode } from "react";
import { Provider } from "@/components/provider";

export const metadata: Metadata = {
	title: {
		template: "%s | Ianua Silvae",
		default: "Ianua Silvae Documentation",
	},
	description: "Lightweight fallback lobby server for the morino.party Minecraft network, built on Minestom",
};

export default function RootLayout({ children }: { children: ReactNode }) {
	return (
		<html lang="en" suppressHydrationWarning>
			<body className="flex flex-col min-h-screen">
				<Provider>{children}</Provider>
			</body>
		</html>
	);
}
