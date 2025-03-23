import {Component, OnInit} from '@angular/core';
import {ApiService} from '../api/services/api.service';
import {EuroPalletDto} from '../api/models/euro-pallet-dto';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-euro-pallet',
  imports: [
    NgForOf
  ],
  templateUrl: './euro-pallet.component.html',
  styleUrl: './euro-pallet.component.scss'
})
export class EuroPalletComponent implements OnInit {
  euroPallets?: EuroPalletDto[];
  constructor(
    private apiService: ApiService
  ) {
  }

  ngOnInit(): void {
    this.apiService.getAllEuroPallets().subscribe({
      next: euroPallets => {
        this.euroPallets = euroPallets;
      }
    })
  }
}
