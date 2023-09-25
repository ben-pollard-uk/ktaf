package ktaf.commands.game

import ktaf.assets.Item
import ktaf.assets.interaction.InteractionEffect
import ktaf.assets.interaction.InteractionTarget
import ktaf.assets.interaction.Reaction
import ktaf.assets.interaction.ReactionResult
import ktaf.commands.Command
import ktaf.logic.Game

/**
 * Provides a command to use a specified [item] on a specified [target].
 */
internal class UseOn(private val item: Item?, private val target: InteractionTarget?) : Command {
    override fun invoke(game: Game): Reaction {
        if (item == null) {
            return Reaction(ReactionResult.ERROR, "You must specify an item.")
        }

        if (target == null) {
            return Reaction(ReactionResult.ERROR, "You must specify a target.")
        }

        val interaction = target.interact(item)

        return when (interaction.effect) {
            InteractionEffect.FATAL_EFFECT -> {
                game.player.kill()
                Reaction(ReactionResult.FATAL, interaction.description)
            }
            InteractionEffect.ITEM_USED_UP -> {
                val currentRoom = game.overworld.currentRegion?.currentRoom
                if (currentRoom != null) {
                    currentRoom.removeItem(item)
                } else if (game.player.hasItem(item)) {
                    game.player.decquireItem(item)
                }
                Reaction(ReactionResult.OK, interaction.description)
            }
            else -> {
                Reaction(ReactionResult.OK, interaction.description)
            }
        }
    }
}
