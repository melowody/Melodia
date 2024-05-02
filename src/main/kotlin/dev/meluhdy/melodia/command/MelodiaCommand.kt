package dev.meluhdy.melodia.command

import dev.meluhdy.melodia.annotations.MissingAnnotationException
import dev.meluhdy.melodia.annotations.RequirePerm
import dev.meluhdy.melodia.annotations.UserOnly
import dev.meluhdy.melodia.utils.ChatUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 * Wrapper class for Commands to make them easier to deal with
 *
 * @param command The name of the command or sub-command
 */
abstract class MelodiaCommand(var command: String) : Command(command), TabCompleter {

    /**
     * List of commands that are sub-commands of this one
     */
    abstract val subCommands: ArrayList<MelodiaCommand>

    private fun hasAnnotation(method: Method, clazz: KClass<out Annotation>) : Boolean {
        return method.isAnnotationPresent(clazz.java)
    }

    private fun checkAnnotations(sender: CommandSender) : Boolean {
        val safeCommandMethod = this::class.java.getDeclaredMethod("safeCommand", CommandSender::class.java, String::class.java, Array<String>::class.java)
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
                    if (!hasAnnotation(safeCommandMethod, UserOnly::class)) {
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

    override fun execute(sender: CommandSender,label: String, args: Array<String>): Boolean {
        if (args.isNotEmpty())
            for (subCommand in subCommands)
                if (subCommand.command == args[0])
                    return subCommand.execute(sender, label, args.copyOfRange(1, args.size))
        if (!checkAnnotations(sender)) return false
        return safeCommand(sender, label, args)
    }

    /**
     * The logic for the command
     *
     * @param sender The entity that triggered the command
     * @param label I do not know
     * @param args The arguments for the command
     *
     * @return Whether the command encountered an error or not
     */
    abstract fun safeCommand(sender: CommandSender, label: String, args: Array<String>): Boolean

}