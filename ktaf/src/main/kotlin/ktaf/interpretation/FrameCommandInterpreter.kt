package ktaf.interpretation

import ktaf.commands.frame.CommandsOff
import ktaf.commands.frame.CommandsOn
import ktaf.commands.frame.KeyOff
import ktaf.commands.frame.KeyOn
import ktaf.extensions.insensitiveEquals
import ktaf.logic.Game

/**
 * Provides an [Interpreter] for frame commands.
 */
public class FrameCommandInterpreter : Interpreter {
    override val supportedCommands: List<CommandHelp>
        get() = FrameCommandInterpreter.supportedCommands

    override fun interpret(input: String, game: Game): InterpretationResult {
        if (input.insensitiveEquals(commandsOff)) {
            return InterpretationResult(true, CommandsOff())
        }

        if (input.insensitiveEquals(commandsOn)) {
            return InterpretationResult(true, CommandsOn())
        }

        if (input.insensitiveEquals(keyOn)) {
            return InterpretationResult(true, KeyOn())
        }

        if (input.insensitiveEquals(keyOff)) {
            return InterpretationResult(true, KeyOff())
        }

        return InterpretationResult.fail
    }

    override fun getContextualCommandHelp(game: Game): List<CommandHelp> {
        return emptyList()
    }

    public companion object {
        /**
         * Get the string for the [CommandsOff] command.
         */
        public const val commandsOff: String = "CommandsOff"

        /**
         * Get the string for the [CommandsOn] command.
         */
        public const val commandsOn: String = "CommandsOn"

        /**
         * Get the string for the [KeyOff] command.
         */
        public const val keyOff: String = "KeyOff"

        /**
         * Get the string for the [KeyOn] command.
         */
        public const val keyOn: String = "KeyOn"

        /**
         * Get the supported commands.
         */
        public val supportedCommands: List<CommandHelp> = listOf(
            CommandHelp("$commandsOn / $commandsOn", "Turn commands on/off"),
            CommandHelp("$keyOn / $keyOff", "Turn the key on/off")
        )
    }
}
