# JBLS - Java Battle.net Logon Server
**J**ava **B**attle.net **L**ogon **S**erver is a Java emulation of a BNLS server, which assists bots with connecting to the classic Battle.net servers (used by games such as Diablo 2). It's primary function is to store the most recent versions of supported game files and to run checksum algorithms over these files as needed by bots during the client verification step of the login process. It also has functions for product key decoding and account password hashing, though the protocol for these are insecure and should not be used unless absolutely necesarry.

This repository is a fork of the [original JBLS code](https://github.com/LexManos/JBLS), hosted by [Hdx](https://github.com/LexManos). It includes fixes for some minor changes to Battle.net over the last several years, cosmetic improvements, and a few added convenience features.

An active copy of this server is run on [jbls.davnit.net](https://jbls.davnit.net/), formerly known as pyro.no-ip.biz.

## BNLS Protocol
For information on the BNLS protocol run on this server, refer to [BNETDocs](https://bnetdocs.org/document/22/bnls-packet-guide).

## Running a server
To run the server, use the command `java -jar JBLS.jar`. The server runs on port 9367 and an optional status page will be available on port 81. Both ports are configurable in settings.ini, which will be generated when you first run the server.

JBLS uses the following directory structure by default:
- JBLS
  - \DLLs\
    - lockdown-IX86-XX.dll (where XX is numbers 00-19)
  - \IX86\
    - \D2DV\
    - \WAR3\
    - etc... (sub-folder for each product)
  - JBLS.jar
  - settings.ini

You only need files in the DLLs and IX86 directories if you intend to serve clients that require them. For example, if you only need to support D2DV then you only need the IX86\D2DV directory and its associated files. A list of required files for each product can be found on [BNETDocs](https://bnetdocs.org/document/47/checkrevision).

The required game files and libraries can be acquired from [BNFTP](https://bnetdocs.org/document/5/file-transfer-protocol-version-1) and/or from official game installations.

## Disclaimer
JBLS is offered as-is and without warranty, expressed or implied. JBLS is not sponsored by Blizzard Entertainment or its subsidiaries in absolutely any way. Battle.net&trade; is a registered trademark of Blizzard Entertainment in the United States and other countries.
