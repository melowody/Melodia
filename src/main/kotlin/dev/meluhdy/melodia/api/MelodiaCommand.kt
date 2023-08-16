package dev.meluhdy.melodia.api

import dev.meluhdy.melodia.Melodia
import dev.meluhdy.melodia.annotations.MissingAnnotationException
import dev.meluhdy.melodia.annotations.RequirePerm
import dev.meluhdy.melodia.annotations.UserOnly
import dev.meluhdy.melodia.utils.ChatUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class MelodiaCommand(command: String) : CommandExecutor {

    init {
        Melodia.plugin!!.getCommand(command)!!.setExecutor(this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
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
        return safeCommand(sender, command, label, args)
    }

    abstract fun safeCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean

}