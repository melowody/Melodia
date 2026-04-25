package dev.meluhdy.melodia.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.meluhdy.melodia.Melodia
import dev.meluhdy.melodia.annotation.RequirePerm
import dev.meluhdy.melodia.annotation.UserOnly
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.entity.Player
import java.util.function.Consumer
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KFunction

data class MelodiaArgument<T : Any>(val name: String, val type: ArgumentType<T>, val executor: KFunction<Int>) {

    fun toArgument(): RequiredArgumentBuilder<CommandSourceStack, T> {
        return Commands.argument(name, type)
    }

}

/**
 * A wrapper for commands to make them easier to build, and allows for the added annotations to be used
 *
 * @param literal The name of the (sub-)command, i.e. "test" for "/test"
 */
abstract class MelodiaCommand(literal: String) : LiteralArgumentBuilder<CommandSourceStack>(literal) {

    /**
     * A list of any child commands this command has (i.e. the "bar" in "/foo bar")
     */
    abstract val children: List<MelodiaCommand>

    abstract val arguments: List<MelodiaArgument<*>>

    internal fun register() {
        children.forEach { command ->
            command.register()
            this.then(command)
        }

        this.executes { ctx -> return@executes if (checkAnnotations(ctx, this::noArgs)) this.noArgs(ctx) else Command.SINGLE_SUCCESS }

        if (arguments.isEmpty()) return

        var curr: ArgumentBuilder<CommandSourceStack, *> = this

        for (argument in arguments) {

            val arg = argument.toArgument()
            arg.executes { ctx -> return@executes if (checkAnnotations(ctx, argument.executor)) argument.executor.call(ctx) else Command.SINGLE_SUCCESS }

            curr.then(arg)
            curr = arg

        }
    }

    private fun checkAnnotations(ctx: CommandContext<CommandSourceStack>, function: KFunction<*>): Boolean {
        Melodia.melodiaInstance.logger.trace("Checking Annotations for ${function.name}")
        function.annotations.forEach { annotation ->
            Melodia.melodiaInstance.logger.debug("Found Annotation: ${annotation::class.simpleName}")
            val sender = ctx.source.sender
            when (annotation) {
                is UserOnly -> {
                    if (sender !is Player) {
                        sender.sendPlainMessage("You must be a player to execute this command!")
                        return false
                    }
                }
                is RequirePerm -> {
                    if (!sender.hasPermission(annotation.perm)) {
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
    abstract fun noArgs(context: CommandContext<CommandSourceStack>): Int

}