import {Component, OnInit} from '@angular/core';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import {NavigationHeaderComponent} from '../navigation-header/navigation-header.component';
import {ScanEverythingButtonComponent} from '../util/scan-everything-button/scan-everything-button.component';
import {MatIcon, MatIconRegistry} from '@angular/material/icon';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatToolbar} from '@angular/material/toolbar';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {ApiConfiguration} from '../api/api-configuration';
import {UserService} from '../util/user.service';
import {UserDto} from '../api/models/user-dto';

@Component({
	selector: 'app-root',
	imports: [
		RouterOutlet,
		ScanEverythingButtonComponent,
		NavigationHeaderComponent,
		MatToolbar,
		MatIconButton,
		MatIcon,
		MatMenu,
		MatMenuItem,
		MatMenuTrigger,
		RouterLink,
		MatButton
	],
	templateUrl: './app.component.html',
	styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
	protected backendUrl: string;
	protected actualU?: UserDto;
	constructor(
		private user: UserService, conf: ApiConfiguration,
		iconReg: MatIconRegistry,
		protected router: Router
	) {
		this.backendUrl = conf.rootUrl
		iconReg.setDefaultFontSetClass("material-symbols", "mat-ligature-font")
	}

	ngOnInit() {
		this.user.getUser().then(it => {
			this.actualU = it;
		})
	}
}
