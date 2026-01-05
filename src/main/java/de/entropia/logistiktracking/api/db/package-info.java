/**
 * What goes here? simple operations relating to the individual resources' db tables. part of their public api.
 * as a guideline, methods in this package should ONLY interact with the db given a few values. they shouldn't implement
 * 	any complex logic or mapping to other layers. they're given db related objects and only interact with the db.
 *
 * Most of the time, a method in this package should be:
 * public ReturnValue doSomething(String someValue) {
 *		return jooq.doSomethingWithTheDb().with(someValue).query();
 * }
 */
package de.entropia.logistiktracking.api.db;