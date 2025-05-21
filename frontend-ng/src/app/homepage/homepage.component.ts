import {Component, OnInit} from '@angular/core';

import * as MastodonTimeline from "@idotj/mastodon-embed-timeline";
import "@idotj/mastodon-embed-timeline/dist/mastodon-timeline.min.css";

@Component({
  selector: 'app-homepage',
  imports: [],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.scss'
})
export class HomepageComponent implements OnInit {
	ngOnInit() {
		const myTimeline = new MastodonTimeline.Init({
			instanceUrl: "https://chaos.social",
			timelineType: "profile",
			userId: "109819324541414738",
			profileName: "@gulasch@gulas.ch",
			dateFormatLocale: "de-DE"
		});
	}
}
