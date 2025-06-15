import {Component, OnInit} from '@angular/core';

import * as MastodonTimeline from "@idotj/mastodon-embed-timeline";
import "@idotj/mastodon-embed-timeline/dist/mastodon-timeline.min.css";
import {MatIcon} from '@angular/material/icon';
import {RouterLink} from '@angular/router';
import {AuthorityEnumDto} from '../api/models/authority-enum-dto';
import {RequiresAuthorityDirective} from '../util/requires-permission.directive';

interface RouteEntry {
	icon: string;
	name: string;
	target: string;
	auth?: AuthorityEnumDto;
}

const theRoutes: RouteEntry[] = [
	{
		icon: "assignment",
		name: "Packlisten",
		target: "/packingList"
	},
	{
		icon: "pallet",
		name: "Paletten",
		target: "/euroPallet"
	},
	{
		icon: "box",
		name: "Boxen",
		target: "/euroCrate"
	},
	{
		icon: "dashboard",
		name: "Übersicht",
		target: "/overview"
	},
	{
		icon: "flight_takeoff",
		name: "Fahrplan",
		target: "/departures"
	},
	{
		icon: "printer",
		name: "Drucken",
		target: "/printMultiple",
		auth: AuthorityEnumDto.Print
	},
	{
		icon: "manage_accounts",
		name: "Admin",
		target: "/users",
		auth: AuthorityEnumDto.ManageUsers
	}
]

@Component({
  selector: 'app-homepage',
	imports: [
		MatIcon,
		RouterLink,
		RequiresAuthorityDirective
	],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.scss'
})
export class HomepageComponent implements OnInit {
	ngOnInit() {
		new MastodonTimeline.Init({
			instanceUrl: "https://chaos.social",
			timelineType: "profile",
			userId: "109819324541414738",
			profileName: "@gulasch@gulas.ch",
			dateFormatLocale: "de-DE"
		});
	}

	protected readonly theRoutes = theRoutes;
}
