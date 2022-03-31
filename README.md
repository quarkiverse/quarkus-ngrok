# Quarkus - Ngrok

## Purpose

This Quarkus extension integrates [ngrok](https://ngrok.com/) into Quarkus Dev Mode thus allowing users to expose their application to the public internet while they are still developing it.

---
**NOTE**

The extension has absolutely no impact on the production Quarkus application
---

## Usage

* Add the extension the application's dependencies
* Start Quarkus in Dev Mode with the following configuration property `quarkus.ngrok.enabled=true`. It is also advised for users to sign up for a free ngrok account and use the obtained token using `quarkus.ngrok.auth-token=sometoken` 
* A short while after the application has started, you should see something like `ngrok is running and its web interface can be accessed at 'http://localhost:4040'` and ` The application can be accessed publicly over the internet using: 'http://7df6-109-242-49-171.ngrok.io'` in the application logs
* Use the latter URL to access the running application from the public internet
