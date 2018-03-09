## Description

This JAR application will help you to upload files on Box using command lines, it is based on the Box JWT Authentication and compatible with Mac, Windows and Linux. 

You Can find more information about the Box JWT authentication [here](https://developer.box.com/docs/authentication-with-jwt) 

> OAuth 2.0 with JWT allows your application to request OAuth 2.0 access tokens necessary to make calls to the Box Content API. Your application may request one of two token types: an enterprise access token and a user access token.
> -   Enterprise access token: Used to provision users via the /users endpoint.
> -   User access token: Used to make API calls to endpoints of the Content API.

# Installation

 1. Copy the jar file 
 2. Create & Configure the "config.properties" file
 3. Ensure to have BouncyCatle as Java provider 
 4. Launch the application

# Usage

      Options:
      * -f, --file The path to get the file to up
      * -n, --folderName  The name of the targeted Box folder
        -h, -?, --help

# Configuration 

## Box JWT token 
> box.configFile = configFile.json

## Share the uploaded file
When the file will be uploaded, you can share it with one or many box users. The email addresses must be separated by comma.
> box.collaboratorEmail = email1@domain.com,email2@domain.com

## Slack notifications

If you want to receive Slack notifications at the end of the process, you can configure the "config.properties" file with the [webhook](https://api.slack.com/incoming-webhooks) information previously created.

> slack.isEnabled = true 
> 
> slack.WebHook = https://hooks.slack.com/services/secret_token 
> 
> slack.Channel = bot
> 
> slack.UserName = up2box

# Bouncy Castle Configuration

_With JAVA 1.8_161 and above_

- Download the [BC](https://www.bouncycastle.org/latest_releases.html) jar file corresponding to your Java version 
- Copy the file in your lib/ext folder: jdk1.8.0_161.jdk/Contents/Home/jre/lib/ext
- In the JRE/lib/security folder
- Add this provider: security.provider.11=org.bouncycastle.jce.provider.BouncyCastleProvider
- Adapt the ID for your system

_With an earlier Java version_

Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 7 If you don't install this, you'll get an exception about key length or exception about parsing PKCS private key for Box Developer Edition. This is not a Box thing, this is a U.S. Government requirement concerning strong encryption. The listed jar is for Oracle JRE. There might be other similar JARs for diffenrent JRE versions like the one below for IBM JDK Java Cryptography Extension for IBM JDK.

Download the file and follow the instructions:
[Java 7](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html)
[Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)