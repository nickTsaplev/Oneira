package com.lesterade.oneira.ui.gameHandling

enum class element {
    water {
        override fun effect(other: element): Int {
            if (other == element.wood)
                return 1
            if (other == element.fire)
                return -1
            return 0
        }
        override val tochar
            get() = "Wt"
    },
    wood {
        override fun effect(other: element): Int {
            if (other == element.fire)
                return 1
            if (other == element.earth)
                return -1
            return 0
        }
        override val tochar
            get() = "Wd"
    },
    fire {
        override fun effect(other: element): Int {
            if (other == element.earth)
                return 1
            if (other == element.metal)
                return -1
            return 0
        }
        override val tochar
            get() = "F"
    },
    earth {
        override fun effect(other: element): Int {
            if (other == element.metal)
                return 1
            if (other == element.water)
                return -1
            return 0
        }
        override val tochar
            get() = "E"
    },
    metal {
        override fun effect(other: element): Int {
            if (other == element.water)
                return 1
            if (other == element.wood)
                return -1
            return 0
        }

        override val tochar
            get() = "M"
    };

    abstract fun effect(other: element): Int
    abstract val tochar: String

    companion object {
        fun fromString(s: String): element = when (s) {
            "water" -> water
            "fire" -> fire
            "earth" -> earth
            "wood" -> wood
            "metal" -> metal
            else -> fire
        }
    }
}
