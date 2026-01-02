import {z} from "zod";

export const findResource = z.strictObject({
    id: z.string().regex(/^[lLcC]\d+$/)
})