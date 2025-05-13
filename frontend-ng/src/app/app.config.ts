import {ApplicationConfig, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import {provideRouter, withComponentInputBinding} from '@angular/router';

import {routes} from './app.routes';
import {ApiModule} from './api/api.module';
import {HttpHandlerFn, HttpRequest, provideHttpClient, withInterceptors} from '@angular/common/http';
import {HashLocationStrategy, LocationStrategy} from '@angular/common';
import {MAT_FORM_FIELD_DEFAULT_OPTIONS} from '@angular/material/form-field';
import {environment} from './environments/environment';

export const appConfig: ApplicationConfig = {
	providers: [
		provideZoneChangeDetection({eventCoalescing: true}),
		provideRouter(routes, withComponentInputBinding()),
		importProvidersFrom(ApiModule.forRoot({rootUrl: environment.backendUrl})),
		provideHttpClient(
			withInterceptors([
				(request: HttpRequest<any>, next: HttpHandlerFn) => {
					request = request.clone({
						withCredentials: true
					});

					return next(request);
				}
			])
		),
		{provide: LocationStrategy, useClass: HashLocationStrategy},
		{ provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: { subscriptSizing: 'dynamic' } }
	]
};
