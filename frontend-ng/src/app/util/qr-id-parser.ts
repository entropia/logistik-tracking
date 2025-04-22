import {OperationCenterDto} from '../api/models/operation-center-dto';

function assertLength(c: string, i: number) {
	if (c.length != i) throw new Error("epxected id length = "+i+", got "+c.length)
}

function assertIndicator(c: string, e: string) {
	if (!c.startsWith(e)) throw new Error("expected indicator '"+e+"', got "+c);
}

function parseNInt(c: string, from: number, n: number): number {
	let ret = 0
	for(let i = from; i < from + n; i++) {
		ret = (ret << 8) | c.charCodeAt(i);
	}
	return ret
}

export function parsePalletId(encoded: string): number {
	assertIndicator(encoded, "P")
	encoded = atob(encoded.substring(1))
	assertLength(encoded, 8) // 8
	return parseNInt(encoded, 0, 8);
}

export type CrateId = {
	kind: IdKind.Crate,
	oc: OperationCenterDto,
	name: string
}

export function parseCrateId(data: string): CrateId {
	assertIndicator(data, "C")

	data = atob(data.substring(1))

	let a = data.charCodeAt(0)
	let name = data.substring(1)

	let availValues = Object.values(OperationCenterDto)
	if (a < 0 || a >= availValues.length) throw new Error("OC index invalid")
	if (name.length < 1) throw new Error("name length invalid")
	return {
		kind: IdKind.Crate,
		name,
		oc: availValues[a]
	}
}

export function parsePacklisteId(encoded: string): number {
	assertIndicator(encoded, "L")
	encoded = atob(encoded.substring(1))
	assertLength(encoded, 8) // 8
	return parseNInt(encoded, 0, 8);
}

export enum IdKind {
	Pallet,
	Crate,
	List
}

export type PalletId = {
	kind: IdKind.Pallet,
	id: number
}

export type ListId = {
	kind: IdKind.List,
	id: number
}

export type GenericId =
	CrateId | PalletId | ListId;

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
			return parseCrateId(binaryContent)
		default:
			throw new Error("unexpected indicator "+binaryContent.charAt(0))
	}
}
