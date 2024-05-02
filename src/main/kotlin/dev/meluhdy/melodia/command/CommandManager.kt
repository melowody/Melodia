package dev.meluhdy.melodia.command

import dev.meluhdy.melodia.pluginInstance
import org.bukkit.Bukkit

object CommandManager {

    private val commandList: ArrayList<MelodiaCommand> = ArrayList()
    private val subCommandList: ArrayList<MelodiaCommand> = ArrayList()

    private fun addSubcommand(command: MelodiaCommand) {

        command.subCommands.forEach { subCommand -> addSubcommand(subCommand) }

        if (subCommandList.any { subCommand -> subCommand.command == command.command }) return

        subCommandList.add(command)

    }

    fun addCommand(command: MelodiaCommand) {

        // Handle subcommands
        command.subCommands.forEach { subCommand -> addSubcommand(subCommand) }

        // If already added, return
        if (subCommandList.any { subCommand -> subCommand.command == command.command }) return
        if (commandList.any { comm -> comm.command == command.command }) return

        // Add command
        commandList.add(command)

    }

    fun registerCommands() {
        for (command in commandList) {
            Bukkit.getCommandMap().register(command.command, command)
            pluginInstance.getCommand(command.command)!!.tabCompleter = command
        }
    }

}