package dev.meluhdy.melodia.command

import dev.meluhdy.melodia.annotations.MissingAnnotationException
import dev.meluhdy.melodia.annotations.RequirePerm
import dev.meluhdy.melodia.annotations.UserOnly
import dev.meluhdy.melodia.utils.ChatUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class MelodiaCommand(var command: String) : CommandExecutor {

    abstract fun registerSubCommands(): ArrayList<MelodiaCommand>

    private fun checkAnnotations(sender: CommandSender) : Boolean {
        val safeCommandMethod = this::class.java.getDeclaredMethod("safeCommand", CommandSender::class.java, Command::class.java, String::class.java, Array<String>::class.java)
        for (annotation in safeCommandMethod.annotations) {
            when (annotation) {
                is UserOnly -> {
                    if (sender !is Player) {
                        // TODO: Add language support!
                        sender.sendMessage(ChatUtils.colorize("&cYou must be a player to execute this command!"))
                        return false
                    }
                }
                is RequirePerm -> {
                    if (!(safeCommandMethod.isAnnotationPresent(UserOnly::class.java))) {
                        throw MissingAnnotationException("@RequirePerm requires @UserOnly to be present.")
                    }
                    if (!(sender as Player).hasPermission(annotation.perm)) {
                        // TODO: Add language support!
                        sender.sendMessage(ChatUtils.colorize("&cYou don't have permission to run this command!"))
                        return false
                    }
                }
            }
        }
        return true
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isNotEmpty())
            for (subCommand in this.registerSubCommands())
                if (subCommand.command == args[0])
                    return subCommand.onCommand(sender, command, label, args.copyOfRange(1, args.size))
        if (!checkAnnotations(sender)) return false
        return safeCommand(sender, command, label, args)
    }

    abstract fun safeCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean

}