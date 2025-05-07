import {Component} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {NavigationHeaderComponent} from '../navigation-header/navigation-header.component';
import {ScanEverythingButtonComponent} from '../util/scan-everything-button/scan-everything-button.component';
import {MatIconRegistry} from '@angular/material/icon';

@Component({
	selector: 'app-root',
	imports: [
		RouterOutlet,
		NavigationHeaderComponent,
		ScanEverythingButtonComponent
	],
	templateUrl: './app.component.html',
	styleUrl: './app.component.scss'
})
export class AppComponent {
	constructor(
		iconReg: MatIconRegistry,
		protected router: Router
	) {
		iconReg.setDefaultFontSetClass("material-symbols", "mat-ligature-font")
	}
}
