import {z} from "zod";
import {DeliveryState, OperationCenter} from "../../gen/graphql";

export const allowedAuthorities = ["MANAGE_RESOURCES", "MANAGE_USERS", "PRINT"] as const;

export const createUser = z.strictObject({
    name: z.string().regex(/^[a-zA-Z0-9_\-]+$/),
    active: z.boolean().default(true),
    password: z.string().min(8),
    authorities: z.enum(allowedAuthorities).array()
})