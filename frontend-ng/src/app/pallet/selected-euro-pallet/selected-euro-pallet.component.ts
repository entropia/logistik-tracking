import {Component, Input, OnInit} from '@angular/core';
import {LocationComponent} from '../../location/location.component';
import {EuroPalletDto} from '../../api/models/euro-pallet-dto';
import {ApiService} from '../../api/services/api.service';

@Component({
  selector: 'app-selected-euro-pallet',
  imports: [
    LocationComponent,
  ],
  templateUrl: './selected-euro-pallet.component.html',
  styleUrl: './selected-euro-pallet.component.scss'
})
export class SelectedEuroPalletComponent implements OnInit {
  @Input()
  set id(id: number) {
    this.apiService.getEuroPallet({
      euroPalletId: id
    }).subscribe({
      next: euroPallet => {
        this.pallet = euroPallet;
      },
      error: err => {
        alert("Failed to load pallet. See console for error")
        console.error(err)
      }
    });
  }

  pallet?: EuroPalletDto;

  constructor(
    private apiService: ApiService
  ) {
  }

  getInfos(): string {
    let existing = this.pallet?.information ?? "";
    existing = existing.trim()
    if (existing.length == 0) return "Keine Informationen";
    return existing;
  }

  ngOnInit(): void {
  }
}
