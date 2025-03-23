import {Component, OnInit} from '@angular/core';
import {ApiService} from '../api/services/api.service';
import {EuroPalletDto} from '../api/models/euro-pallet-dto';
import {NgForOf} from '@angular/common';
import {LocationTypeDto} from '../api/models/location-type-dto';
import {LogisticsLocationDto} from '../api/models/logistics-location-dto';
import {MatButton} from '@angular/material/button';
import {Router} from '@angular/router';

@Component({
  selector: 'app-euro-pallet',
  imports: [
    NgForOf,
    MatButton
  ],
  templateUrl: './euro-pallet.component.html',
  styleUrl: './euro-pallet.component.scss'
})
export class EuroPalletComponent implements OnInit {
  euroPallets?: EuroPalletDto[];
  constructor(
    private apiService: ApiService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.apiService.getAllEuroPallets().subscribe({
      next: euroPallets => {
        this.euroPallets = euroPallets;
      }
    })
  }

  createPallet() {
    this.apiService.createEuroPallet({
      body: {
        location: {
          locationType: LocationTypeDto.Logistics,
          logisticsLocation: LogisticsLocationDto.Entropia
        },
        information: ''
      }
    }).subscribe({
      next: pallet => {
            this.router.navigate(['euroPallet/' + pallet.euroPalletId]).catch(reason => {
              console.log("Failed to redirect to newly created pallet because: " + reason);
            });
      }
    });
  }
}
