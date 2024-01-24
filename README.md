
<div align="center">
<img src="https://github.com/quarkiverse/quarkus-ngrok/blob/main/docs/modules/ROOT/assets/images/quarkus.svg" width="67" height="70" ><img src="https://github.com/quarkiverse/quarkus-ngrok/blob/main/docs/modules/ROOT/assets/images/plus-sign.svg" height="70" ><img src="https://github.com/quarkiverse/quarkus-ngrok/blob/main/docs/modules/ROOT/assets/images/ngrok.svg" height="70" >

# Quarkus Ngrok
</div>
<br>

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.ngrok/quarkus-ngrok?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.ngrok/quarkus-ngrok)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/quarkiverse/quarkus-ngrok/actions/workflows/build.yml/badge.svg)](https://github.com/quarkiverse/quarkus-ngrok/actions/workflows/build.yml)


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
