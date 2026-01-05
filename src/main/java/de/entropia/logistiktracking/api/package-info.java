/**
 * What goes here? more complex operations with the resources, that can be part of a public api.
 * most of the time, these are operations that require at least 2 logical steps:
 * 	- create new crate record, AND store it, AND return the record with feedback from the db
 * 	- update the crate record, AND store it, AND notify the jira ticket
 * services in this package should not implement much mapping logic. they should only work with the
 * base backend format of their data. mapping is to be implemented by the interface facing services.
 */
package de.entropia.logistiktracking.api;