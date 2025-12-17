package com.lesterade.oneira.gameHandling

import kotlinx.serialization.Serializable

@Serializable
enum class Element {
    water {
        override fun effect(other: Element): Int {
            if (other == wood)
                return 1
            if (other == fire)
                return -1
            return 0
        }
        override val tochar
            get() = "Wt"
    },
    wood {
        override fun effect(other: Element): Int {
            if (other == fire)
                return 1
            if (other == earth)
                return -1
            return 0
        }
        override val tochar
            get() = "Wd"
    },
    fire {
        override fun effect(other: Element): Int {
            if (other == earth)
                return 1
            if (other == metal)
                return -1
            return 0
        }
        override val tochar
            get() = "F"
    },
    earth {
        override fun effect(other: Element): Int {
            if (other == metal)
                return 1
            if (other == water)
                return -1
            return 0
        }
        override val tochar
            get() = "E"
    },
    metal {
        override fun effect(other: Element): Int {
            if (other == water)
                return 1
            if (other == wood)
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
