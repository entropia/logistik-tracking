import { Routes } from '@angular/router';
import {PackingListComponent} from './packing-list/packing-list.component';
import {EuroCrateComponent} from './euro-crate/euro-crate.component';
import {EuroPalletComponent} from './euro-pallet/euro-pallet.component';
import {SelectedEuroPalletComponent} from './selected-euro-pallet/selected-euro-pallet.component';

export const routes: Routes = [
  { path: '', component: PackingListComponent },
  { path: 'packingList', component: PackingListComponent },
  { path: 'packingList/:id', component: SelectedEuroPalletComponent },
  { path: 'euroCrate', component: EuroCrateComponent },
  { path: 'euroCrate/:id', component: SelectedEuroPalletComponent },
  { path: 'euroPallet', component: EuroPalletComponent },
  { path: 'euroPallet/:id', component: SelectedEuroPalletComponent }
];
