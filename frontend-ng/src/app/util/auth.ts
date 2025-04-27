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
