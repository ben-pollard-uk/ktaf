<div align="center">

# Ktaf

A Kotlin library that provides a framework for creating text adventures and interactive stories for JVM. Ktaf is a Kotlin implementation of the BP.AdventureFramework. 
Ktaf aims to provide all of the basic building blocks required to start writing simple games.

[![main-ci](https://github.com/benpollarduk/ktaf/actions/workflows/main-ci.yml/badge.svg?branch=main)](https://github.com/benpollarduk/ktaf/actions/workflows/main-ci.yml)
[![codecov](https://codecov.io/gh/benpollarduk/ktaf/graph/badge.svg?token=1C7HRFJ6C9)](https://codecov.io/gh/benpollarduk/ktaf)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=benpollarduk_ktaf&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=benpollarduk_ktaf)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=benpollarduk_ktaf&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=benpollarduk_ktaf)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=benpollarduk_ktaf&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=benpollarduk_ktaf)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=benpollarduk_ktaf&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=benpollarduk_ktaf)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=benpollarduk_ktaf&metric=bugs)](https://sonarcloud.io/summary/new_code?id=benpollarduk_ktaf)
[![GitHub release](https://img.shields.io/github/release/benpollarduk/ktaf.svg)](https://github.com/benpollarduk/ktaf/releases)
[![License](https://img.shields.io/github/license/benpollarduk/ktaf.svg)](https://opensource.org/licenses/MIT)
[![Documentation Status](https://img.shields.io/badge/docs-latest-brightgreen.svg)](https://benpollarduk.github.io/ktaf-docs/)

</div>

## Overview
Included in the repo are example projects that show how Ktaf can be used to write games that run in a terminal window as originally intended, and also an example Swing application and an example Ktor webserver are also included that render the game in HTML.

![ktaf_example](https://github.com/benpollarduk/ktaf/assets/129943363/10ee7024-f3ea-4679-b50c-011503eac836)

Ktaf provides simple classes for developing game elements:
  * Interface and base class for examinable objects:
    * Examination returns a description of the object.
    * Descriptions can be conditional, with different results generated from the game state.
    * All items can contain custom commands.
  * Hierarchical environments:
    * Overworld
      * Acts as a container of regions.
    * Region
      * Acts as a container of rooms.
    * Room
      * The player traverses through the rooms.
      * Provides a description of itself.
      * Supports up to 6 exits. Each exit can be locked until a condition is met.
      * Can contain multiple items.
  * NPC's:
    * Support provided for conversations with the player.
    * Can interact with items.
    * Can contain multiple items.
  * Items
    * Support interaction with the player, rooms, other items and NPC's.
    * Items can morph in to other items. For example, using item A on item B may cause item B to morph into item C.

### Commands
  
Ktaf provides commands for interacting with game elements:
  * **Drop X** - drop an item.
  * **Examine X** - allows items, characters and environments to be examined.
  * **Take X** - take an item.
  * **Talk to X** - talk to a NPC, where X is the NPC.
  * **Use X on Y** - use an item. Items can be used on a variety of targets. Where X is the item and Y is the target.
  * **N, S, E, W, U, D** - traverse through the rooms in a region.

Ktaf also provides global commands to help with game flow and option management:
  * **About** - display version information.
  * **CommandsOn / CommandsOff** - toggle commands on/off.
  * **Exit** - exit the game.
  * **Help** - display the help screen.
  * **KeyOn / KeyOff** - turn the Key on/off.
  * **Map** - display the map.
  * **New** - start a new game.

Custom commands can be added to games without the need to extend the existing interpretation.

### Interpretation

Ktaf provides classes for handling interpretation of input. Interpretation is extensible with the ability for custom interpreters to be added outside of the core Ktaf library.

### Conversations

Conversations can be held between the player and a NPC. Conversations support multiple lines of dialogue and responses.

![image](https://github.com/ben-pollard-uk/ktaf/assets/129943363/3adc4210-2732-4f79-9d19-000af0287f07)

### Rendering

Ktaf provides frames for rendering the various game screens. These are fully extensible and customisable. These include:
   * Scene frame.
   * Help frame.
   * Map frame.
   * Title frame.
   * Completion frame.
   * Game over frame.
   * Transition frame.
   * Conversation frame.

### Maps
  
Maps are automatically generated for regions and rooms, and can be viewed with the **map** command:

![image](https://github.com/ben-pollard-uk/ktaf/assets/129943363/b8e52974-dad7-4c27-8c0a-6861964a2fbe)

Maps display visited rooms, exits, player position, if an item is in a room, lower floors and more.

### Dynamic Loading

Precompiled games can also be discovered and loaded from .jar files at runtime with the GameCatalogResolver.
```Kotlin
// load a .jar file and discover all GameTemplate instances
val catalog = GameCatalogResolver.resolveCatalogFromJar(File(path))

// in this case take the first GameTemplate from the catalog
val template = catalog.get().first()

// begin execution of the game
GameExecutor.execute(gameTemplate, ioConfiguration = AnsiConsoleConfiguration)
```

## Prerequisites
The default frame collections for rendering in a terminal assume that a terminal capable of handling ANSI is being used. If a terminal that doesn't support ANSI is used the game will still render but ANSI will also be displayed as text.

## Getting Started
 * Clone the repo.
 * Build and run the included terminal application.
```bash
./gradlew clean :app-ktaf-example-console:build
cd app-ktaf-example-console/build/libs
java -jar app-ktaf-example-console-all.jar
```
 * Run included Swing example application to run with a basic UI.
```bash
./gradlew :app-ktaf-example-swing:run
```
 * Run included ktor example webserver then navigate to localhost:8080 in browser.
```bash
./gradlew :app-ktaf-example-ktor:run
```

## Hello World
```kotlin
// create a game template. this template can be use to instantiate instances of the game
val template = object : GameTemplate() {

    override fun instantiate(ioConfiguration: IOConfiguration): Game {

        // create the player. this is the character the user plays as
        val player = PlayableCharacter("Dave", "A young boy on a quest to find the meaning of life.")

        // create region maker. the region maker simplifies creating in game regions. a region contains a series of rooms
        val regionMaker = RegionMaker("Mountain", "An imposing volcano just East of town.").also {
            // add a room to the region at position x 0, y 0, z 0
            it[0, 0, 0] = Room("Cavern", "A dark cavern set in to the base of the mountain.")
        }

        // create overworld maker. the overworld maker simplifies creating in game overworlds. an overworld contains a series or regions
        val overworldMaker = OverworldMaker("Daves World", "An ancient kingdom.", listOf(regionMaker))

        // return a new instance of the game
        return Game(
            GameInformation(
                "The Life Of Dave",
                "Dave awakes to find himself in a cavern...",
                "A very low budget adventure.",
                "Me",
            ),
            player,
            overworldMaker.make(),
            { EndCheckResult.notEnded },
            { EndCheckResult.notEnded },
        )
    }
}

// begin execution of the game. the ioConfiguration determines how input is received and output is displayed
GameExecutor.execute(template, ioConfiguration = ioConfiguration)
```

## Documentation
Please visit [https://benpollarduk.github.io/ktaf-docs/](https://benpollarduk.github.io/ktaf-docs/) to view the ktaf documentation.

## For Open Questions
Visit https://github.com/benpollarduk/ktaf/issues
