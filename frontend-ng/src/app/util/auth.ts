import {HttpErrorResponse} from '@angular/common/http';

export function checkErrorAndAlertUser(e: any): boolean {
	if (e instanceof HttpErrorResponse) {
		if (e.status == 401) {
			alert("Für diese Aktion musst du angemeldet sein (response code 401)")
			return true;
		} else if (e.status == 403) {
			alert("Für diese Aktion fehlt dir die berechtigung (response code 403)")
			return true;
		}
	}
	return false;
}

export function handleDefaultError(e: any) {
	console.error(e)
	if (!checkErrorAndAlertUser(e)) {
		let msg: string;
		if (e instanceof HttpErrorResponse) {
			msg = `Operation failed: Server responded with status code ${e.status}\n${JSON.stringify(e.error, undefined, 4)}`
		} else if (e instanceof Error) {
			msg = `Operation failed: Error: ${e.message}`
		} else {
			msg = "Operation failed: unknown error"
		}
		alert(msg)
	}
}
