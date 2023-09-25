package ktaf.utilities

import ktaf.ExaminableObject
import ktaf.assets.locations.Direction
import ktaf.assets.locations.Region
import ktaf.assets.locations.Room
import ktaf.assets.locations.ViewPoint
import ktaf.extensions.ensureFinishedSentence
import ktaf.extensions.getObjectifier
import ktaf.extensions.toSentenceCase
import ktaf.helpers.newline
import java.util.*

/**
 * Provides utility functions to help with handling [String] values.
 */
internal object StringUtilities {
    /**
     * Construct a sentence describing a collection of one or more [examinables].
     */
    internal fun constructExaminablesAsSentence(examinables: List<ExaminableObject>): String {
        if (examinables.isNullOrEmpty()) {
            return ""
        }

        var examinabesAsList = ""
        val examinableNames = examinables
            .filter { it.isPlayerVisible }
            .map { it.identifier.name }
            .toList()

        if (examinableNames.size == 1) {
            return "${examinableNames.first().getObjectifier().toSentenceCase()} ${examinableNames.first()}."
        }

        for (index in examinableNames.indices) {
            val examinable = examinableNames[index]

            examinabesAsList += if (index == 0 && examinableNames.size > 2) {
                "${examinable.getObjectifier().toSentenceCase()} $examinable, "
            } else if (index == 0) {
                "${examinable.getObjectifier().toSentenceCase()} $examinable "
            } else if (index < examinableNames.size - 2) {
                "${examinable.getObjectifier()} $examinable, "
            } else if (index < examinableNames.size - 1) {
                "${examinable.getObjectifier()} $examinable "
            } else {
                "and ${examinable.getObjectifier()} $examinable."
            }
        }

        return examinabesAsList
    }

    /**
     * Cut a line from [paragraph].
     */
    internal fun cutLineFromParagraph(paragraph: String, maxWidth: Int): ExtractedString {
        var chunk = ""
        var remaining = paragraph
        val newline = newline()

        while (chunk.length < maxWidth) {
            val extractedString = extractNextWordFromString(remaining)
            var word = extractedString.extractedWord
            remaining = extractedString.rest

            if (word.isEmpty()) {
                break
            }

            if (chunk.length + word.length > maxWidth) {
                remaining = "$word$remaining"
                break
            }

            if (word.isNotEmpty() && !word.endsWith(newline)) {
                word += " "
            }

            chunk += word

            if (chunk.endsWith(newline)) {
                break
            }
        }

        return ExtractedString(chunk.trimEnd(), remaining)
    }

    /**
     * Extract the next word from a string.
     */
    internal fun extractNextWordFromString(value: String): ExtractedString {
        val space = ' '
        val lf = '\n'
        val cr = '\r'
        val specialCharacters: Array<Char> = arrayOf(space, lf)
        var word = ""
        val trimmed = value.trimStart(space)

        for (c in trimmed) {
            if (!specialCharacters.contains(c)) {
                word += c
            } else {
                if (c == lf) {
                    word += c
                }
                break
            }
        }

        val remaining = trimmed.substring(word.length)
        word = word.trim(space, cr)
        return ExtractedString(word, remaining)
    }

    /**
     * Create a description of all NPCs in a specified [room] as a [String].
     */
    internal fun createNPCString(room: Room): String {
        val characters = room.characters.filter { it.isPlayerVisible && it.isAlive }

        if (characters.isEmpty()) {
            return ""
        }

        if (characters.size == 1) {
            return "${characters.first().identifier} is in ${room.identifier}."
        }

        var charactersAsString = ""

        for (i in characters.indices) {
            val character = characters[i]

            charactersAsString += if (i == characters.size - 1) {
                "${character.identifier} are in the ${room.identifier}."
            } else if (i == characters.size - 2) {
                "${character.identifier} and "
            } else {
                "${character.identifier}, "
            }
        }

        return charactersAsString
    }

    /**
     * Create a [String] detailing a [ViewPoint] from the specified [viewPoint] and [room].
     */
    internal fun createViewpointAsString(room: Room, viewPoint: ViewPoint): String {
        var view = ""

        for (direction in Region.allDirections) {
            val roomInDirection = viewPoint[direction] ?: continue
            val roomDescription = if (room[direction]?.isLocked == true) "a locked exit" else "the ${roomInDirection.identifier.name}"

            if (view.isEmpty()) {
                view += when (direction) {
                    Direction.UP -> "Above is $roomDescription, "
                    Direction.DOWN -> "Below is $roomDescription, "
                    else -> "To the ${direction.toString().lowercase()} is $roomDescription, "
                }
            } else {
                view += when (direction) {
                    Direction.UP -> "above is $roomDescription, "
                    Direction.DOWN -> "below is $roomDescription, "
                    else -> "${direction.toString().lowercase()} is $roomDescription, "
                }
            }
        }

        return if (view.isEmpty()) "" else view.substring(0, view.length - 2).ensureFinishedSentence()
    }
}
