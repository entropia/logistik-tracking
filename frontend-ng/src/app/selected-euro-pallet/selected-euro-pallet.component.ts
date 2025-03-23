import {Component, Input, OnInit} from '@angular/core';
import {ApiService} from '../api/services/api.service';
import {EuroPalletDto} from '../api/models/euro-pallet-dto';
import {LocationComponent} from '../location/location.component';

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
  set id(id: string) {
    this.apiService.getEuroPallet({
      euroPalletId: id
    }).subscribe({
      next: euroPallet => {
        this.pallet = euroPallet;
      }
    });
  }

  pallet?: EuroPalletDto;

  constructor(
    private apiService: ApiService
  ) {
  }

  ngOnInit(): void {
  }
}
