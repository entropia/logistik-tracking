> [!CAUTION]
> Diese repository wurde migriert: https://codeberg.org/entropia/logistik-tracking


# Logitrack
Internal tracking tool of GPN Logistik.

This tool is integrated pretty tightly with our internal systems and workflows, so it might not work for you out of the box.
If you want to maintain a fork to make it work for some other event, go ahead! Contributions are welcome.

## Prerequisites

- docker
- docker compose
- node and yarn (frontend)
- `JAVA_HOME` needs to point to the directory of your JDK25 install. Verify with `./mvnw --version`.

## Getting started
1. Modify application-entw.properties: Point the jira url to the proper instance
2. Place your jira key in `localdev.env` as `JIRA_KEY`
3. set up docker compose with the db/docker-compose.yml recipe. It starts a postgres db at localhost:5432, and an adminer instance at localhost:23646.
Credentials to the db can be found in the yml file as well
4. run the launch config from idea, found in .run. It should be imported automatically (name is LogistikTrackingApplication).
The `entw` spring profile uses the db created by docker, and sets up debugging logs 
5. Frontend: `yarn install`, then `yarn dev -p 8000 -h 127.0.0.1` (127.0.0.1 is important for some browsers to figure out cors properly!)
6. http://127.0.0.1:8000 (again, try not to use localhost, use 127.0.0.1 instead)
7. Create one user manually in the db to bootstrap everything: https://argon2.online/ (Argon2id w/ random salt, 1 parallel, 19456 cost, 2 iters, 32 length), put the encoded form in the db's password field

## Yarn?
see also https://yarnpkg.com/getting-started/install

`npm i -g corepack`, yarn will be installed when you run it

## Frontend
1. `yarn install` - install dependencies
2. DEVELOPMENT: `yarn dev -p 8000 -h 127.0.0.1` - starts dev server with hmr, ctrl c or q enter to quit
3. PROD: `yarn build` - builds server bundle (WITHOUT dependencies!) into frontend-sv/build

## backend
`mvn clean package`

Packages all relevant files (main fatjar, frontend, launcher script) into target/logistik-tracking-(version)-main

## build types
### `entw`
Local development. Uses locally set up database. Use the `LogistikTrackingApplication` run conf.

### `prod`
Production environment. Db should be on 127.0.0.1:5432. 
1. Build frontend: `yarn build`
2. Build backend: `mvn clean package`
3. Deploy both as described below

## Running architecture
The built bundle contains a systemd unit for the backend and frontend, along with some scripts for making them work.
1. Modify the backend application-prod.properties:
   1. Point the frontend url to the correct one to fix CORS
   2. Point the jira URL to the correct instance to make jira work
2. Unzip backend tarball into `/opt/logitrack`
3. Unzip frontend tarball into `/opt/logitrack_frontend`
4. Create `/opt/logitrack/env`:
   - `DB_PASSWORD=(password for db)`
   - `JIRA_TOKEN=(token for jira account)`
5. Link systemd services for frontend and backend
6. Start frontend and backend

By default, the app in prod assumes the following (via profiles):
- production environment
- systemd notify should be done on startup
- a proxy is serving the public; only listen to loopback (127.0.0.1)

If you want to change any of these things, edit the service_entry.sh file in src/assembly
