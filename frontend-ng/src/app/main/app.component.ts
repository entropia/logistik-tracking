import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {NavigationHeaderComponent} from '../navigation-header/navigation-header.component';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    NavigationHeaderComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
}
