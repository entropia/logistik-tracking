import {Component, Input, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {handleDefaultError} from '../auth';
import {IdKind, parseId} from '../qr-id-parser';

@Component({
  selector: 'app-qr-resolver',
  imports: [],
  templateUrl: './qr-resolver.component.html',
  styleUrl: './qr-resolver.component.scss'
})
export class QrResolverComponent implements OnInit{
	@Input()
	id!: string;
	constructor(private router: Router) {}

	ngOnInit() {
		try {
			let genericId = parseId(this.id);
			switch (genericId.kind) {
				case IdKind.List:
					this.router.navigate(["/", "packingList", genericId.id])
					break;
				case IdKind.Crate:
					this.router.navigate(["/", "euroCrate", genericId.id])
					break;
				case IdKind.Pallet:
					this.router.navigate(["/", "euroPallet", genericId.id])
					break
			}
		} catch (e) {
			handleDefaultError(e)
		}
	}
}
