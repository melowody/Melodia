package dev.meluhdy.melodia.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.meluhdy.melodia.Melodia
import dev.meluhdy.melodia.annotation.RequirePerm
import dev.meluhdy.melodia.annotation.UserOnly
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player

/**
 * A wrapper for commands to make them easier to build, and allows for the added annotations to be used
 *
 * @param literal The name of the (sub-)command, i.e. "test" for "/test"
 */
@Suppress("UnstableApiUsage")
abstract class MelodiaCommand(literal: String) : LiteralArgumentBuilder<CommandSourceStack>(literal) {

    /**
     * A list of any child commands this command has (i.e. the "bar" in "/foo bar")
     */
    abstract val children: ArrayList<MelodiaCommand>

    fun register() {
        children.forEach { command ->
            command.register()
            this.then(command)
        }

        this.executes { ctx ->
            if (!checkAnnotations(ctx)) return@executes Command.SINGLE_SUCCESS
            return@executes onCommand(ctx)
        }
    }

    private fun checkAnnotations(ctx: CommandContext<CommandSourceStack>): Boolean {
        Melodia.logger.trace("Checking Annotations for ${this::class.simpleName}")
        val safeCommandMethod = this::class.java.getDeclaredMethod("onCommand", CommandContext::class.java)
        safeCommandMethod.annotations.forEach { annotation ->
            Melodia.logger.debug("Found Annotation: ${annotation::class.simpleName}")
            val sender = ctx.source.sender
            when (annotation) {
                is UserOnly -> {
                    if (sender !is Player) {
                        sender.sendPlainMessage("You must be a player to execute this command!")
                        return false
                    }
                }
                is RequirePerm -> {
                    if (sender.hasPermission(annotation.perm)) {
                        sender.sendPlainMessage("You don't have permission to use this command!")
                        return false
                    }
                }
            }
        }
        return true
    }

    /**
     * The logic to run when this command is run
     *
     * @param context The CommandContext given by PaperSpigot and Brigadier
     */
    abstract fun onCommand(context: CommandContext<CommandSourceStack>): Int

}