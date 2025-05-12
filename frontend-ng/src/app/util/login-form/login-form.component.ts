import {Component} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-login-form',
	imports: [
		MatFormField,
		MatInput,
		MatButton,
		MatLabel

	],
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.scss'
})
export class LoginFormComponent {

	hadError: boolean = false;
	from?: string;

	constructor(
		private api: ApiService,
		route: ActivatedRoute
	) {
		route.queryParams.subscribe(it => {
			this.hadError = this.hadError || it["loginFailed"] != undefined;
			this.from = it["from"];
		})
	}

	apiUrl() {
		return this.api.rootUrl
	}

	getFromPart() {
		if (this.from) {
			// FIXME kinda hacky
			return "?redirect="+window.location.origin+"#"+this.from;
		}
		return '';
	}
}
