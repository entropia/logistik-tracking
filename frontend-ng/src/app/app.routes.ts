import { Routes } from '@angular/router';
import {PackingListComponent} from './packlist/packing-list/packing-list.component';
import {SelectedEuroPalletComponent} from './pallet/selected-euro-pallet/selected-euro-pallet.component';
import {EuroCrateComponent} from './crate/euro-crate/euro-crate.component';
import {EuroPalletComponent} from './pallet/euro-pallet/euro-pallet.component';
import {SelectedEuroCrateComponent} from './crate/selected-euro-crate/selected-euro-crate.component';
import {SelectedPackingListComponent} from './packlist/selected-packing-list/selected-packing-list.component';

export const routes: Routes = [
  { path: '', component: PackingListComponent },
  { path: 'packingList', component: PackingListComponent },
  { path: 'packingList/:id', component: SelectedPackingListComponent },
  { path: 'euroCrate', component: EuroCrateComponent },
  { path: 'euroCrate/:id', component: SelectedEuroCrateComponent },
  { path: 'euroPallet', component: EuroPalletComponent },
  { path: 'euroPallet/:id', component: SelectedEuroPalletComponent }
];
