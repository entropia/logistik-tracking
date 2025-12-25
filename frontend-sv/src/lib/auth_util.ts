import {error, redirect} from "@sveltejs/kit";
import type { TypedDocumentString } from "../gen/graphql";
import { execute, NetworkResponseNotOkError } from "./graphql";

export async function graphqlWithAuthHandling<TResult, TVariables>(
	redirBackUrl: URL,
	query: TypedDocumentString<TResult, TVariables>,
	fetchFn = window.fetch,
	...variables: TVariables extends Record<string, never> ? [] : [TVariables]
) {
	try {
		return (await execute<TResult, TVariables>(query, fetchFn,...variables));
	} catch (e) {
		if (e instanceof NetworkResponseNotOkError) {
			handleAuthError(redirBackUrl, e.resp)
			throw "should not reach here";
		} else {
			throw e;
		}
	}
}

export function handleAuthError(loadEventUrl: URL, resp: Response) {
	if (resp.status == 401) {
		let u = new URL("/users/login", loadEventUrl.origin)
		u.searchParams.set("message", "Eine Anmeldung ist erforderlich, um auf die Resource zuzugreifen.")
		let targetPath = loadEventUrl.href.substring(loadEventUrl.origin.length)
		if (targetPath == "") targetPath = "/";
		u.searchParams.set("redirect", targetPath)
		redirect(303, u)
	} else if (resp.status == 403) {
		error(401, "Du hast keine Berechtigung, auf diese Resource zuzugreifen.")
	} else {
		error(500, "Request returned "+resp.status)
	}
}