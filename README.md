# JourneyMap Bukkit

This is a Spigot plugin which provides the server-side components for servers that wish to
allow their users to use the [JourneyMap Forge mod](https://journeymap.info).

## Project State

This project is currently just a proof-of-concept. It implements the following features:

* WorldID support (tells JM which world is which to keep their maps separate)

## Building

1. Install Java - This plugin is built for Java 8. We recommend making use of the
[AdoptOpenJDK Project](https://adoptopenjdk.net/).
2. Run `./gradlew` (Just `gradlew` on Windows).
3. You'll find the JAR in `build/libs` - it's the one ending with `-all`.
