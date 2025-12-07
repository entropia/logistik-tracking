import {error, redirect} from "@sveltejs/kit";

export function handleAuthError(loadEventUrl: URL, resp: Response) {
	if (resp.status == 401) {
		let u = new URL("/users/login", loadEventUrl.origin)
		u.searchParams.set("message", "Eine Anmeldung ist erforderlich, um auf die Resource zuzugreifen.")
		let targetPath = loadEventUrl.href.substring(loadEventUrl.origin.length)
		if (targetPath == "") targetPath = "/";
		u.searchParams.set("redirect", targetPath)
		console.log(u)
		redirect(303, u)
	} else if (resp.status == 403) {
		error(401, "Du hast keine Berechtigung, auf diese Resource zuzugreifen.")
	} else {
		error(500, "Request returned "+resp.status)
	}
}