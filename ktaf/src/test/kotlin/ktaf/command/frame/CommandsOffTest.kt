package ktaf.command.frame

import ktaf.assets.interaction.ReactionResult
import ktaf.commands.frame.CommandsOff
import ktaf.logic.GameTestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CommandsOffTest {
    @Test
    fun `given valid game when invoke then return ok`() {
        // Given
        val command = CommandsOff()

        // When
        val result = command.invoke(GameTestHelper.getBlankGame())

        // Then
        Assertions.assertEquals(ReactionResult.OK, result.result)
    }
}
