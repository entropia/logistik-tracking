import { PUBLIC_API_URL } from '$env/static/public';
import type {paths} from "../gen/openapi";
import createClient from "openapi-fetch";

export const client = createClient<paths>({
	baseUrl: PUBLIC_API_URL+"/api",
	credentials: "include"
})