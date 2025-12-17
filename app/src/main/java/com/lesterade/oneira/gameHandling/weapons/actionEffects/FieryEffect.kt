package com.lesterade.oneira.gameHandling.weapons.actionEffects

import com.lesterade.oneira.gameHandling.biomes.Biome
import com.lesterade.oneira.gameHandling.Creature

class FieryEffect(val fireEff: Int, val minFire: Int?): ActionEffect {
    override fun effect(from: Creature, to: Creature, located: Biome) {
        if(fireEff == 0)
            from.innerFire = 0
        else {
            if(minFire != null && from.innerFire + fireEff < minFire)
                return
            from.innerFire += fireEff
        }
    }
}