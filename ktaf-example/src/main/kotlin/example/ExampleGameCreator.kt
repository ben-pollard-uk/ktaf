package example

import example.everglades.rooms.InnerCave
import example.hub.Hub
import example.player.Player
import ktaf.assets.Item
import ktaf.assets.interaction.Reaction
import ktaf.assets.interaction.ReactionResult
import ktaf.assets.locations.Overworld
import ktaf.assets.locations.Region
import ktaf.commands.CustomCommand
import ktaf.extensions.tryParseInt
import ktaf.interpretation.CommandHelp
import ktaf.io.IOConfiguration
import ktaf.logic.EndCheckResult
import ktaf.logic.Game
import ktaf.logic.GameCreator
import ktaf.logic.OverworldCreator

/**
 * Provides an example [GameCreator].
 */
public object ExampleGameCreator {
    private fun determineIfGameIsComplete(game: Game): EndCheckResult {
        return if (InnerCave.NAME == game.overworld.currentRegion?.currentRoom?.identifier?.name) {
            EndCheckResult(
                false,
                "Game Over",
                "You have reached the end of the game, thanks for playing!",
            )
        } else {
            EndCheckResult.notEnded
        }
    }

    private fun determineIfGameOver(game: Game): EndCheckResult {
        return if (!game.player.isAlive) {
            EndCheckResult(false, "Game Over", "You are dead.")
        } else {
            EndCheckResult.notEnded
        }
    }

    private fun populateHub(hub: Region, overworld: Overworld, otherRegions: List<Region>) {
        val room = hub.currentRoom ?: return

        otherRegions.forEach { region ->
            room.addItem(
                Item(
                    "${region.identifier.name} Sphere",
                    "A glass sphere, about the size of a snooker ball. Inside you can see a swirling mist.",
                    true,
                ).also {
                    it.commands = listOf(
                        CustomCommand(
                            CommandHelp(
                                "Warp ${it.identifier.name}",
                                "Use the ${it.identifier.name} Sphere to warp to the ${it.identifier.name}.",
                            ),
                            true,
                        ) { game, _ ->
                            val move = overworld.move(region)
                            if (!move) {
                                Reaction(
                                    ReactionResult.ERROR,
                                    "Could not move to ${region.identifier.name}.",
                                )
                            }
                            game.displayTransition(
                                "",
                                "You peer inside the sphere and feel faint. When the sensation passes you open " +
                                    "you eyes and have been transported to the ${region.identifier.name}.",
                            )

                            Reaction(ReactionResult.INTERNAL, "")
                        },
                    )
                },
            )
        }
    }

    /**
     * Create a new instance of the example [GameCreator] using a [ioConfiguration].
     */
    public fun create(ioConfiguration: IOConfiguration): GameCreator {
        val player = Player().instantiate()

        val overworldCreator: OverworldCreator = { playableCharacter ->
            val regions = emptyList<Region>()
            val overworld = Overworld("Test Overworld", "This is a test overworld")
            val hub = Hub().instantiate(playableCharacter)
            populateHub(hub, overworld, regions)
            overworld.addRegion(hub)

            regions.forEach {
                overworld.addRegion(it)
            }

            overworld.commands = listOf(
                CustomCommand(
                    CommandHelp(
                        "Jump",
                        "Jump to a location in a region.",
                    ),
                    false,
                ) { game, args ->
                    var x = 0
                    var y = 0
                    var z = 0

                    if (args.size >= 3) {
                        x = args[0].tryParseInt() ?: 0
                        y = args[1].tryParseInt() ?: 0
                        z = args[2].tryParseInt() ?: 0
                    }

                    if (game.overworld.currentRegion?.jumpToRoom(x, y, z) == true) {
                        Reaction(ReactionResult.OK, "Jumped to $x $y $z.")
                    } else {
                        Reaction(ReactionResult.ERROR, "Failed to jump to $x $y $z.")
                    }
                },
            )
            overworld
        }

        val about = "This is a short demo of the BP.AdventureFramework made up from test chunks of games that were " +
            "build to test different features during development."

        return Game.create(
            "ktav dmo",
            about,
            about,
            "Ben Pollard",
            overworldCreator,
            { player },
            { determineIfGameIsComplete(it) },
            { determineIfGameOver(it) },
            ioConfiguration = ioConfiguration,
        )
    }
}
