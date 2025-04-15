import {ApplicationConfig, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import {provideRouter, withComponentInputBinding} from '@angular/router';

import {routes} from './app.routes';
import {ApiModule} from './api/api.module';
import {provideHttpClient} from '@angular/common/http';
import {HashLocationStrategy, LocationStrategy} from '@angular/common';
import {MAT_FORM_FIELD_DEFAULT_OPTIONS} from '@angular/material/form-field';

export const appConfig: ApplicationConfig = {
	providers: [
		provideZoneChangeDetection({eventCoalescing: true}),
		provideRouter(routes, withComponentInputBinding()),
		// FIXME das sollte eigentlich besser aussehen. funktioniert aber um den api host auf die jetzige url zu setzen mit port 8080
		// nochmal FIXME https > http
		importProvidersFrom(ApiModule.forRoot({rootUrl: "http://"+window.location.hostname+":8080"})),
		provideHttpClient(),
		{provide: LocationStrategy, useClass: HashLocationStrategy},
		{ provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: { subscriptSizing: 'dynamic' } }
	]
};
