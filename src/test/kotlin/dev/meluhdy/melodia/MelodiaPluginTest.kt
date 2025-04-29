package dev.meluhdy.melodia

import dev.meluhdy.melodia.command.MelodiaCommand

open class MelodiaPluginTest : MelodiaPlugin() {

    override fun getCommands(): ArrayList<MelodiaCommand> = arrayListOf()

}