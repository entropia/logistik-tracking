import {Directive, Input, TemplateRef, ViewContainerRef, ViewRef} from '@angular/core';
import {AuthorityStatus, UserService} from './user.service';
import {AuthorityEnumDto} from '../api/models/authority-enum-dto';

@Directive({
	selector: "[requireAuthority]"
})
export class RequiresAuthorityDirective {
	@Input()
	set requireAuthority(it: AuthorityEnumDto) {
		this.api.hasAuthority(it).then(w => {
			this.show(w == AuthorityStatus.HasIt)
		})
	}

	constructor(
		private api: UserService,
		private templateRef: TemplateRef<any>,
		private vcr: ViewContainerRef
	) {
	}

	existingVR?: ViewRef;

	private show(yeah: boolean) {
		if (yeah) {
			// not yet instantiated, create
			if (!this.existingVR) this.existingVR = this.vcr.createEmbeddedView(this.templateRef)
		} else {
			this.vcr.clear()
			this.existingVR = undefined
		}
	}
}
