package com.lesterade.oneira.gameHandling

import com.lesterade.oneira.IFileHandler
import com.lesterade.oneira.game.GameMaster
import com.lesterade.oneira.game.TurnReport
import com.lesterade.oneira.game.biomes.SimpleBiome
import com.lesterade.oneira.gameHandling.msgFormatting.MsgFormatter
import com.lesterade.oneira.gameHandling.msgFormatting.msgPatterns
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlinx.serialization.json.jsonObject

data class dispTool(var imageId: String = "none",
                    var header: String = "",
                    var description: String = "",
                    var transmutable: Boolean = false)

class GameHandler(private val patterns: msgPatterns, private val master : GameMaster) {
    var tools = buildList { master.display.forEach{add(dispTool(it.imageId, it.header, it.description, it.transmutable && master.scene is SimpleBiome))} }
    var inventory = listOf<dispTool>()
    var msg = ""

    val sceneHead
        get() = master.scene.dispHeader

    val sceneDesc
        get() = master.scene.desc

    fun startGame(playName: String = "player") {
        master.startNewGame(playName)
    }

    val creatureName
        get() = if(master.enemy.name != "?")
            master.enemy.name
        else
            null

    val sceneName
        get() = master.scene.name

    val ended
        get() = master.ended

    val left_bar
        get() = master.us.percent_hp

    val right_bar
        get() = master.enemy.percent_hp

    val transmutation_left
        get() = master.us.directions[0].main to master.us.directions[0].dir

    val transmutation_right
        get() = master.us.directions[1].main to master.us.directions[1].dir


    private fun compileTurnReport(formatter: MsgFormatter, report: TurnReport, isEnemy: Boolean): String {
        if (isEnemy) {
            formatter.addEffect(patterns.hits_pattern, report.hit)
            formatter.addEffect(patterns.bleeds_pattern, report.bleed)
            formatter.addEffect(patterns.suffers_pattern, report.suffer)
            formatter.addEffect(patterns.heals_pattern, report.heal)
        } else {
            formatter.addEffect(patterns.hit_pattern, report.hit)
            formatter.addEffect(patterns.bleed_pattern, report.bleed)
            formatter.addEffect(patterns.suffer_pattern, report.suffer)
            formatter.addEffect(patterns.heal_pattern, report.heal)
        }
        return formatter.format()
    }

    fun update() {
        tools = buildList { master.display.forEach{add(dispTool(it.imageId, it.header, it.description, it.transmutable && master.scene is SimpleBiome))} }
        inventory = buildList { master.us.inventory.forEach{add(dispTool(it.imageId, it.header, it.description, it.transmutable && master.scene is SimpleBiome))} }
    }

    fun activateTool(id: Int) {
        val report = master.activateTool(id)

        msg = ""
        msg = buildString {
            report.first?.let {
                val enemyMsg =
                    MsgFormatter(master.enemy.header, patterns.and_name, patterns.does_pattern)
                append(compileTurnReport(enemyMsg, it, true))
                append("\n")
            }
            report.second?.let {
                val usMsg = MsgFormatter(patterns.self_name, patterns.and_name, patterns.do_pattern)
                append(compileTurnReport(usMsg, it, false))
            }
        }

        update()
    }

    fun transmuteTool(id: Int, dir: Int) {
        msg = ""
        val report = master.transmuteTool(id, dir)

        if (report != null) {
            val enemyMsg =
                MsgFormatter(master.enemy.header, patterns.and_name, patterns.does_pattern)
            msg = compileTurnReport(enemyMsg, report, true)
        }
        update()
    }

    fun useItem(id: Int) {
        master.useCollectible(id)

        update()
    }

    fun loadGame(fileHandler: IFileHandler) {
        if(!fileHandler.saveExists())
            return

        val data = parseToJsonElement(fileHandler.readSaveText()).jsonObject

        master.load(data)
    }

    fun saveGame(fileHandler: IFileHandler) {
        if(fileHandler.saveExists())
            fileHandler.deleteSaveFile()

        val obj = master.save()
        fileHandler.writeSave(obj.toString())
    }
}

