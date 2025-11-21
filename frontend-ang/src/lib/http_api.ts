import { PUBLIC_API_URL } from '$env/static/public';

export function printMultiple(ids: { type: string; id: string }[], fetchFn = window.fetch) {
	return fetchFn(PUBLIC_API_URL+"/api/printMultiple", {
		method: "POST",
		credentials: "include",
		body: JSON.stringify(ids),
		headers: {
			"Content-Type": "application/json"
		}
	})
}