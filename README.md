<div align="center">
<img src="https://github.com/quarkiverse/quarkus-ngrok/blob/main/docs/modules/ROOT/assets/images/quarkus.svg" width="67" height="70" ><img src="https://github.com/quarkiverse/quarkus-ngrok/blob/main/docs/modules/ROOT/assets/images/plus-sign.svg" height="70" ><img src="https://github.com/quarkiverse/quarkus-ngrok/blob/main/docs/modules/ROOT/assets/images/ngrok.svg" height="70" >

# Quarkus Ngrok
</div>
<br>

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.ngrok/quarkus-ngrok?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.ngrok/quarkus-ngrok)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/quarkiverse/quarkus-ngrok/actions/workflows/build.yml/badge.svg)](https://github.com/quarkiverse/quarkus-ngrok/actions/workflows/build.yml)

<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-4-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

## Purpose

This Quarkus extension integrates [ngrok](https://ngrok.com/) into Quarkus Dev Mode thus allowing users to expose their application to the public internet while they are still developing it.

> [!IMPORTANT]  
> NOTE: The extension has absolutely no impact on the production Quarkus application

## Getting started

Read the full [Ngrok documentation](https://docs.quarkiverse.io/quarkus-ngrok/dev/index.html).

* Create or use an existing Quarkus application
* Add the Ngrok extension with the [Quarkus CLI](https://quarkus.io/guides/cli-tooling) or Apache Maven:

```bash
quarkus ext add io.quarkiverse.ngrok:quarkus-ngrok
```

```bash
./mvnw quarkus:add-extension -Dextensions="io.quarkiverse.ngrok:quarkus-ngrok"
```

## Usage

1. Get a [ngrok auth token](https://ngrok.com/docs/getting-started/#step-2-connect-your-account) and configure it using ngrok or set it via `quarkus.ngrok.authtoken`

2. A short while after the application has started, you should see something in the application logs like:

```bash
ngrok is running and its web interface can be accessed at: 'http://localhost:4040'
The application can be accessed publicly over the internet using: 'http://4f59-68-81-186-238.ngrok-free.app'
```

For further instructions and tips to run using stable domains and additional config see https://docs.quarkiverse.io/quarkus-ngrok/dev/

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/geoand"><img src="https://avatars.githubusercontent.com/u/4374975?v=4?s=100" width="100px;" alt="Georgios Andrianakis"/><br /><sub><b>Georgios Andrianakis</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-ngrok/commits?author=geoand" title="Code">💻</a> <a href="#maintenance-geoand" title="Maintenance">🚧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://melloware.com"><img src="https://avatars.githubusercontent.com/u/4399574?v=4?s=100" width="100px;" alt="Melloware"/><br /><sub><b>Melloware</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-ngrok/commits?author=melloware" title="Code">💻</a> <a href="#maintenance-melloware" title="Maintenance">🚧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://www.activi.link"><img src="https://avatars.githubusercontent.com/u/56843747?v=4?s=100" width="100px;" alt="zakhdar"/><br /><sub><b>zakhdar</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-ngrok/commits?author=zakhdar" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://xam.dk"><img src="https://avatars.githubusercontent.com/u/54129?v=4?s=100" width="100px;" alt="Max Rydahl Andersen"/><br /><sub><b>Max Rydahl Andersen</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-ngrok/commits?author=maxandersen" title="Code">💻</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
