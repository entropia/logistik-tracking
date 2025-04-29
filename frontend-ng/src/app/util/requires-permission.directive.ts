import {Directive, Input, OnInit} from '@angular/core';
import {AuthorityStatus, UserService} from './user.service';
import {AuthorityEnumDto} from '../api/models/authority-enum-dto';
import {MatTooltip} from '@angular/material/tooltip';

@Directive({
	selector: "[requireAuthority]",
	hostDirectives: [
		MatTooltip
	]
})
export class RequiresAuthorityDirective implements OnInit{
	@Input() requireAuthority!: AuthorityEnumDto;
	@Input() buttonToDisable!: {disabled: boolean};

	constructor(
		private api: UserService,
		private mt: MatTooltip
	) {
	}

	ngOnInit() {
		this.buttonToDisable.disabled = true
		this.api.hasAuthority(this.requireAuthority).then(it => {
			switch (it) {
				case AuthorityStatus.HasIt:
					this.buttonToDisable.disabled = false
					this.mt.message = ""
					break;
				case AuthorityStatus.Idk:
					this.buttonToDisable.disabled = true
					this.mt.message = "Failed to get user information"
					break;
				case AuthorityStatus.NotLoggedIn:
					this.mt.message = `Not logged in; this action requires authority ${this.requireAuthority}`
					this.buttonToDisable.disabled = true
					break;
				case AuthorityStatus.DoesNotHave:
					this.mt.message = `This action requires authority ${this.requireAuthority}`
					this.buttonToDisable.disabled = true
					break;
			}
		})
		this.mt.disabled = false;
	}
}
