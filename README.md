## a funny
some people looking at the commit history might've seen that at some point, the db password for prod was stored in plain text.
this was for testing.

do not fret, for we have already changed the password and moved to a better alternative for storing it.

## Prerequisites

- docker
- docker compose
- node and yarn (frontend)
- `JAVA_HOME` needs to point to the directory of your JDK23 install. Verify with `mvnw --version`.

## getting started
1. set up docker compose with the db/docker-compose.yml recipe. it starts a postgres db at localhost:5432, and an adminer instance at localhost:23646.
credentials to the db can be found in the yml file as well
2. run the launch config from idea, found in .run. it should be imported automatically (name is LogistikTrackingApplication).
the entw profile has db set up locally, and also configures sql logging 
3. setup: `yarn install` and start frontend: `yarn start`
4. http://localhost:4200

## installing yarn
see also https://yarnpkg.com/getting-started/install

`npm i -g corepack`

## frontend
1. `yarn install` - install dependencies
2. DEVELOPMENT: `yarn start` - starts dev server with hmr, ctrl c or q enter to quit
3. PROD: `yarn build` - builds with angular compiler (esbuild) into `./dist`

## backend
`mvn clean package`

requires frontend to be built

packages all relevant files (main fatjar, frontend, launcher script) into target/logistik-tracking-(version)-main

## running architecture
the built bundle contains a systemd unit, along with some scripts for making it work. if you just want this thing to work, put it in `/opt/logitrack` such that `/opt/logitrack/bin` exists and is a folder, add an `env` file containing `DB_PASSWORD=(password)` to the logitrack folder, link the service, and start it.

the only requirement for running it is a running postgres db at port 5432, with a user logitrack with matching password in the env file, that has access to a db called logitrack. all else will be handled by the app.