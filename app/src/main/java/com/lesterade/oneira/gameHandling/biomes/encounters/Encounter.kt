package com.lesterade.oneira.gameHandling.biomes.encounters

import kotlinx.serialization.Serializable

@Serializable
class Encounter(var description: String = "", val enemyName: String) {
    var outcomes: List<Outcome> = listOf()
}