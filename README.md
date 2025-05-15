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

## build types
### `entw`
Local development. Uses locally set up database. Use the `LogistikTrackingApplication` run conf.

### `testprod`
Production environment on local machine. Uses mostly the same configuration as prod, still relies on local database. Use the `LogistikTrackingApplication testprod` conf.
1. build frontend: `yarn build_localtest`
2. start application

This configuration uses a self signed ssl cert to allow camera usage on ios safari. The certificate configuration for openssl is located at `testprod/self_signed_cert.cnf`.

The certificate can be generated with: `openssl req -x509 -nodes -days 730 -newkey rsa:2048 -keyout self_signed_pk.pem -out self_signed_cert.pem -config self_signed_cert.cnf`

### `prod`
Production environment. Relies on db on 127.0.0.1:5432. 
1. build frontend: `yarn build`
2. generate bundle: `mvn clean package`
3. deploy bundle as described below

## running architecture
the built bundle contains a systemd unit, along with some scripts for making it work. if you just want this thing to work, put it in `/opt/logitrack` such that `/opt/logitrack/bin` exists and is a folder, add an `env` file containing `DB_PASSWORD=(password)` to the logitrack folder, link the service, and start it.

the only requirement for running it is a running postgres db at port 5432, with a user logitrack with matching password in the env file, that has access to a db called logitrack. all else will be handled by the app.

by default, the app in prod assumes the following (via profiles):
- production environment
- systemd notify should be done on startup
- a proxy is serving the public; only listen to loopback (127.0.0.1)

if you want to change any of these things, edit the service_entry.sh file in src/assembly

### Example Ansible deploy script
Used by us in prod. Changes will have to be made. The same host this will run on already has a properly configured nginx. See our config below.
```yaml
- name: Get latest release
  ansible.builtin.uri:
    url: https://api.github.com/repos/entropia/logistik-tracking/releases/latest
    return_content: true
  register: latest_release

- name: Parse release & get url
  set_fact:
    lr_url: "{{ latest_release.json | json_query(\"assets[?ends_with(name, '.tar.gz')].url | [0]\") }}"

- name: Download release
  ansible.builtin.get_url:
    url: "{{ lr_url }}"
    headers:
      Accept: application/octet-stream
    dest: /tmp/logitrack_latest.tar.gz

- name: Remove old release
  ansible.builtin.file:
    path: /opt/logitrack
    state: absent

- name: Install release 1/2
  ansible.builtin.file:
    path: /opt/logitrack
    state: directory
    mode: '0755'

- name: Install release 2/2
  ansible.builtin.unarchive:
    src: /tmp/logitrack_latest.tar.gz
    dest: /opt/logitrack
    remote_src: yes
    extra_opts:
      - --strip-components
      - 1

- name: Populate env
  ansible.builtin.copy:
    content: |
      DB_PASSWORD={{ logitrack_postgres_pw }}
    dest: /opt/logitrack/env

- name: Install service
  ansible.builtin.file:
    src: /opt/logitrack/bin/logitrack.service
    dest: /lib/systemd/system/logitrack.service
    state: link

- name: Reload systemd, restart service
  ansible.builtin.systemd_service:
    name: "logitrack"
    state: "restarted"
    daemon_reload: true
```

### Example nginx config
```nginx configuration
server {
    listen 80;
    listen [::]:80;

    server_name example.de;
    
    location / {
      proxy_pass http://localhost:8080;
      proxy_connect_timeout 20s;
      proxy_send_timeout 10s;
      proxy_read_timeout 20s;  
    }
}
```