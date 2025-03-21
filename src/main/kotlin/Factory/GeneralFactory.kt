package Factory

import General.General

abstract class GeneralFactory {
    abstract fun createRandomGeneral(): General?
}