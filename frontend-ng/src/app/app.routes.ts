import {Routes} from '@angular/router';

export const routes: Routes = [
	{
		path: "",
		loadComponent: () => import("./packlist/packing-list/packing-list.component")
			.then(it => it.PackingListComponent)
	},
	{path: 'packingList', loadComponent: () => import("./packlist/packing-list/packing-list.component")
			.then(it => it.PackingListComponent)},
	{path: 'packingList/:id', loadComponent: () => import("./packlist/selected-packing-list/selected-packing-list.component")
			.then(it => it.SelectedPackingListComponent)},
	{path: 'packingList/:id/delivery', loadComponent: () => import("./packlist/packliste-auslieferung/packliste-auslieferung.component")
			.then(it => it.PacklisteAuslieferungComponent)},

	{path: 'euroCrate', loadComponent: () => import("./crate/euro-crate/euro-crate.component").then(it => it.EuroCrateComponent)},
	{path: 'euroCrate/:id', loadComponent: () => import("./crate/selected-euro-crate/selected-euro-crate.component")
			.then(it => it.SelectedEuroCrateComponent)},
	{path: 'euroPallet', loadComponent: () => import("./pallet/euro-pallet/euro-pallet.component").then(it => it.EuroPalletComponent)},
	{path: 'euroPallet/:id', loadComponent: () => import("./pallet/selected-euro-pallet/selected-euro-pallet.component")
			.then(it => it.SelectedEuroPalletComponent)},
	{path: "login", loadComponent: () => import("./util/login-form/login-form.component")
			.then(it => it.LoginFormComponent)},
	{path: "users", loadComponent: () => import("./users/user-list/user-list.component").then(it => it.UserListComponent)},
	{
		path: "users/:id",
		loadComponent: () => import("./users/selected-user/selected-user.component").then(it => it.SelectedUserComponent)
	},
	{
		path: "printMultiple",
		loadComponent: () => import("./multiple-print-builder/multiple-print-builder.component").then(it => it.MultiplePrintBuilderComponent)
	},
	{
		path: "overview",
		loadComponent: () => import("./overview-page/overview-page.component").then(it => it.OverviewPageComponent)
	},
	{
		path: "departures",
		loadComponent: () => import("./departures/departures.component").then(it => it.DeparturesComponent)
	},
];
