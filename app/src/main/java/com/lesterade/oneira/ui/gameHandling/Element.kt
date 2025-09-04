package com.lesterade.oneira.ui.gameHandling

import kotlinx.serialization.Serializable

@Serializable
enum class Element {
    water {
        override fun effect(other: Element): Int {
            if (other == Element.wood)
                return 1
            if (other == Element.fire)
                return -1
            return 0
        }
        override val tochar
            get() = "Wt"
    },
    wood {
        override fun effect(other: Element): Int {
            if (other == Element.fire)
                return 1
            if (other == Element.earth)
                return -1
            return 0
        }
        override val tochar
            get() = "Wd"
    },
    fire {
        override fun effect(other: Element): Int {
            if (other == Element.earth)
                return 1
            if (other == Element.metal)
                return -1
            return 0
        }
        override val tochar
            get() = "F"
    },
    earth {
        override fun effect(other: Element): Int {
            if (other == Element.metal)
                return 1
            if (other == Element.water)
                return -1
            return 0
        }
        override val tochar
            get() = "E"
    },
    metal {
        override fun effect(other: Element): Int {
            if (other == Element.water)
                return 1
            if (other == Element.wood)
                return -1
            return 0
        }

        override val tochar
            get() = "M"
    };

    abstract fun effect(other: Element): Int
    abstract val tochar: String

    companion object {
        fun fromString(s: String): Element = when (s) {
            "water" -> water
            "fire" -> fire
            "earth" -> earth
            "wood" -> wood
            "metal" -> metal
            else -> fire
        }
    }
}
