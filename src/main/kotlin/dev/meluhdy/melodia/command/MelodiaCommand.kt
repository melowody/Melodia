package dev.meluhdy.melodia.command

import dev.meluhdy.melodia.annotations.MissingAnnotationException
import dev.meluhdy.melodia.annotations.RequirePerm
import dev.meluhdy.melodia.annotations.UserOnly
import dev.meluhdy.melodia.utils.ChatUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 * Wrapper class for Commands to make them easier to deal with.
 *
 * @param command The name of the command or sub-command.
 */
abstract class MelodiaCommand(var command: String) : CommandExecutor, TabCompleter {

    /**
     * List of commands that are sub-commands of this one,
     * e.g. If you have a MelodiaCommand GuiCommand("gui") that lists the guis,
     * and you have a MelodiaCommand MainGuiCommand("main") that opens the MainGui upon /gui main,
     * then in GuiCommand you would add MainGuiCommand here.
     */
    abstract val subCommands: ArrayList<MelodiaCommand>

    /**
     * Checks if a command has an annotation.
     *
     * @param method The function to check for the annotation.
     * @param clazz The annotation to check for.
     *
     * @return True if the annotation is found, else false.
     */
    private fun hasAnnotation(method: Method, clazz: KClass<out Annotation>) : Boolean {
        return method.isAnnotationPresent(clazz.java)
    }

    /**
     * Checks if a command has any of the command-specific annotations and returns if their conditions are met.
     *
     * @param sender The sender of the command.
     *
     * @return True if the conditions of the given annotations are met, false otherwise.
     */
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

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>?): Boolean {
        if (!args.isNullOrEmpty())
            for (subCommand in subCommands)
                if (subCommand.command == args[0])
                    return subCommand.onCommand(sender, command, label, args.copyOfRange(1, args.size))
        if (!checkAnnotations(sender)) return false
        return safeCommand(sender, label, args)
    }

    /**
     * The logic for the command.
     *
     * @param sender The entity that triggered the command.
     * @param label Which label the command used in the case of aliases (e.g. "h" if the player entered /h instead of /home).
     * @param args The arguments for the command.
     *
     * @return Whether the command encountered an error or not.
     */
    abstract fun safeCommand(sender: CommandSender, label: String, args: Array<String>?): Boolean

}