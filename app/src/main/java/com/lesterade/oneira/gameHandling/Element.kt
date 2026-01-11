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

        override fun transmute(mainSeq: Boolean, dir: Boolean): Element =
            if (mainSeq)
                if (dir)
                    wood
                else
                    metal
            else
                if(dir)
                    earth
                else
                    fire


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
        override fun transmute(mainSeq: Boolean, dir: Boolean): Element =
            if (mainSeq)
                if (dir)
                    fire
                else
                    water
            else
                if(dir)
                    metal
                else
                    earth

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

        override fun transmute(mainSeq: Boolean, dir: Boolean): Element =
            if (mainSeq)
                if (dir)
                    earth
                else
                    wood
            else
                if(dir)
                    water
                else
                    metal

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

        override fun transmute(mainSeq: Boolean, dir: Boolean): Element =
            if (mainSeq)
                if (dir)
                    metal
                else
                    fire
            else
                if(dir)
                    wood
                else
                    water

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

        override fun transmute(mainSeq: Boolean, dir: Boolean): Element =
            if (mainSeq)
                if (dir)
                    water
                else
                    earth
            else
                if(dir)
                    fire
                else
                    wood

        override val tochar
            get() = "M"
    };

    abstract fun effect(other: Element): Int
    abstract fun transmute(mainSeq: Boolean, dir: Boolean): Element
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
