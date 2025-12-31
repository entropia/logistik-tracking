import {z} from "zod";
import {DeliveryState, OperationCenter} from "../../gen/graphql";

export const updateListDeliveryState = z.strictObject({
    deliveryState: z.enum(DeliveryState)
})

export const createList = z.strictObject({
    name: z.string()
})

export const updateTargettedListState = z.strictObject({
    deliveryState: z.enum(DeliveryState),
    id: z.string().regex(/^[Ll]\d+$/)
})