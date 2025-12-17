package com.lesterade.oneira.gameHandling.weapons.actionEffects

import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.creature

class FieryEffect(val fireEff: Int, val minFire: Int?): ActionEffect {
    override fun effect(from: creature, to: creature, located: biome) {
        if(fireEff == 0)
            from.innerFire = 0
        else {
            if(minFire != null && from.innerFire + fireEff < minFire)
                return
            from.innerFire += fireEff
        }
    }
}