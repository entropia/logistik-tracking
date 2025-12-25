import {z} from "zod";
import {DeliveryState, OperationCenter} from "../../gen/graphql";

export const updateCrate = z.strictObject({
	operationCenter: z.enum(OperationCenter),
	deliveryState: z.enum(DeliveryState),
	info: z.nullable(z.string()),
	jiraTicket: z.nullable(z.number().gte(1))
})

export const createCrate = updateCrate.safeExtend({
	name: z.string(),
	createMultiple: z.boolean().default(false)
})

export type UpdateCrateType = typeof updateCrate;
export type CreateCrateType = typeof createCrate;