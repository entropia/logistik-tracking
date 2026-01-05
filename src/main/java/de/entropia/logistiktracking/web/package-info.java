/**
 * The web facing part of the application.
 * What goes here? controllers providing public apis, such as rest apis, graphql, websockets, etc.
 * these controllers should mostly look like this:
 * public Something doSomething() {
 * 		SomethingDto something = theRelevantService.doSomething();
 * 		return somethingConverter.toXyz(something);
 * }
 * Basic aggregations or mapping is allowed, but major functionality should be implemented by services, not by the access layer.
 * If it makes sense to have as a public api (example: create record, update record, print resource label), put it in the service.
 * A common exception to this is layer-specific infrastructure. Example: get the current session's UserDto.
 * 	This wouldn't make much sense in a public api, since at that point, we don't care about the current session (in general).
 */
package de.entropia.logistiktracking.web;