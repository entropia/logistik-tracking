export function prepare_id(id: string): string {
	let start_idx = 0;
	while (id.length > start_idx && "0LClc".includes(id.charAt(start_idx))) start_idx++;
	return id.substring(start_idx);
}