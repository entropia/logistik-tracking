export function prepare_id(id: string): string {
	let start_idx = 0;
	while ("0LClc".includes(id[start_idx])) start_idx++;
	return id.substring(start_idx);
}