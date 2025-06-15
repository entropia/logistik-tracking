import {Component} from '@angular/core';
import {RouterLink} from "@angular/router";
import {MatMenuItem} from '@angular/material/menu';
import {RequiresAuthorityDirective} from '../util/requires-permission.directive';
import {AuthorityEnumDto} from '../api/models';
import {MatIcon} from '@angular/material/icon';

@Component({
	selector: 'app-navigation-header',
	imports: [
		RouterLink, RequiresAuthorityDirective, MatMenuItem, MatIcon
	],
	templateUrl: './navigation-header.component.html',
	styleUrl: './navigation-header.component.scss'
})
export class NavigationHeaderComponent {

	protected readonly AuthorityEnumDto = AuthorityEnumDto;
}
