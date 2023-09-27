package ktaf.utilities.templates

import ktaf.assets.characters.PlayableCharacter

/**
 * Provides a template for producing [PlayableCharacter] objects.
 */
public open class PlayableCharacterTemplate {
    /**
     * Instantiate a new instance of the templated [PlayableCharacter].
     */
    public open fun instantiate(): PlayableCharacter {
        throw NotImplementedError()
    }
}
