package dev.meluhdy.melodia.command

import dev.meluhdy.melodia.MelodiaPlugin

/**
 * Handles all the command logic, including primary and sub-commands.
 */
object CommandManager {

    private val commandList: ArrayList<MelodiaCommand> = ArrayList()
    private val subCommandList: ArrayList<MelodiaCommand> = ArrayList()

    /**
     * Adds a MelodiaCommand as a subcommand in order to avoid registering invalid commands or overwriting existing commands.
     *
     * @param command The MelodiaCommand subcommand to add.
     */
    private fun addSubcommand(command: MelodiaCommand) {

        command.subCommands.forEach { subCommand -> addSubcommand(subCommand) }

        if (subCommandList.any { subCommand -> subCommand.command == command.command }) return

        subCommandList.add(command)

    }

    /**
     * Adds a command or a subcommand to automatically be registered on plugin load.
     *
     * @param command The MelodiaCommand to be added for registry.
     */
    fun addCommand(command: MelodiaCommand) {

        // Handle subcommands
        command.subCommands.forEach { subCommand -> addSubcommand(subCommand) }

        // If already added, return
        if (subCommandList.any { subCommand -> subCommand.command == command.command }) return
        if (commandList.any { comm -> comm.command == command.command }) return

        // Add command
        commandList.add(command)

    }

    /**
     * Registers the commands with Bukkit/Spigot in order to be used in-game.
     *
     * @param plugin An instance of the plugin in order to load the command.
     */
    fun registerCommands(plugin: MelodiaPlugin) {
        for (command in commandList) {
            val pluginCommand = plugin.getCommand(command.command) ?: throw IllegalArgumentException("Could not find command ${command.command}. Make sure it's in your plugin.yml!")
            pluginCommand.setExecutor(command)
            pluginCommand.tabCompleter = command
        }
    }

}