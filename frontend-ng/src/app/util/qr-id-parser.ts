function assertIndicator(c: string, e: string) {
	if (!c.startsWith(e)) throw new Error("expected indicator '"+e+"', got "+c);
}

export function extractIdFromUrl(u: string): string {
	let theUrl = new URL(u)
	let pathname = theUrl.hash.substring(1)
	if (pathname.endsWith("/")) pathname = pathname.substring(0, pathname.length-1)
	return pathname.substring(pathname.lastIndexOf('/') + 1)
}

export function parsePalletId(encoded: string): number {
	assertIndicator(encoded, "P")
	encoded = encoded.substring(1)
	return parseInt(encoded)
}

export function parseCrateId(encoded: string): number {
	assertIndicator(encoded, "C")
	encoded = encoded.substring(1)
	return parseInt(encoded)
}

export function parsePacklisteId(encoded: string): number {
	assertIndicator(encoded, "L")
	encoded = encoded.substring(1)
	return parseInt(encoded)
}

export enum IdKind {
	Pallet,
	Crate,
	List
}

export type GenericId = {
	kind: IdKind,
	id: number
};

export function parseId(binaryContent: string): GenericId {
	switch (binaryContent.charAt(0)) {
		case "P":
			return {
				kind: IdKind.Pallet,
				id: parsePalletId(binaryContent)
			}
		case "L":
			return {
				kind: IdKind.List,
				id: parsePacklisteId(binaryContent)
			}
		case "C":
			return {
				kind: IdKind.Crate,
				id: parseCrateId(binaryContent)
			}
		default:
			throw new Error("unexpected indicator "+binaryContent.charAt(0))
	}
}
