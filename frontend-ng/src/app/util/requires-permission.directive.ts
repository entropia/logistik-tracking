import {Directive, ElementRef, Input, OnInit} from '@angular/core';
import {AuthorityStatus, UserService} from './user.service';
import {AuthorityEnumDto} from '../api/models/authority-enum-dto';

@Directive({
	selector: "[requireAuthority]"
})
export class RequiresAuthorityDirective implements OnInit{
	@Input() requireAuthority!: AuthorityEnumDto;

	constructor(
		private api: UserService,
		private el: ElementRef
	) {
	}

	ngOnInit() {
		this.el.nativeElement.disabled = true
		this.api.hasAuthority(this.requireAuthority).then(it => {
			switch (it) {
				case AuthorityStatus.HasIt:
					this.el.nativeElement.disabled = false
					break;
				case AuthorityStatus.Idk:
					this.el.nativeElement.disabled = true
					break;
				case AuthorityStatus.NotLoggedIn:
				case AuthorityStatus.DoesNotHave:
					this.el.nativeElement.disabled = true
					break;
			}
		})
	}
}
