package dev.meluhdy.melodia.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.meluhdy.melodia.Melodia
import dev.meluhdy.melodia.annotation.RequirePerm
import dev.meluhdy.melodia.annotation.UserOnly
import dev.meluhdy.melodia.command.MelodiaCommand.Companion.checkAnnotations
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KFunction

data class MelodiaArgument<T : Any>(val name: String, val type: ArgumentType<T>, val executor: KFunction<Int>, val suggestions: ((context: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder) -> CompletableFuture<Suggestions>)? = null) {

    fun toArgument(): RequiredArgumentBuilder<CommandSourceStack, T> {
        val out = Commands.argument(name, type)
        this.suggestions?.let { out.suggests(it) }
        out.executes { ctx ->
            Melodia.melodiaInstance.logger.debug("Attempting to execute $name")
            return@executes if (checkAnnotations(ctx, this.executor)) this.executor.call(ctx) else 0
        }
        return out
    }

}

/**
 * A wrapper for commands to make them easier to build, and allows for the added annotations to be used
 *
 * @param literal The name of the (sub-)command, i.e. "test" for "/test"
 */
abstract class MelodiaCommand(literal: String) : LiteralArgumentBuilder<CommandSourceStack>(literal) {

    companion object {
        fun checkAnnotations(ctx: CommandContext<CommandSourceStack>, function: KFunction<*>): Boolean {
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
    }

    /**
     * A list of any child commands this command has (i.e. the "bar" in "/foo bar")
     */
    abstract val children: List<MelodiaCommand>

    abstract val arguments: List<MelodiaArgument<*>>

    open fun register() {
        children.forEach { command ->
            command.register()
            this.then(command)
        }

        this.executes { ctx -> return@executes if (checkAnnotations(ctx, this::noArgs)) this.noArgs(ctx) else 0 }

        if (arguments.isEmpty()) return

        var curr: ArgumentBuilder<CommandSourceStack, *>? = null

        for (argument in arguments.reversed()) {

            val arg = argument.toArgument()
            curr?.let { arg.then(it) }
            curr = arg

        }

        curr?.let { this.then(it) }s
    }

    /**
     * The logic to run when this command is run
     *
     * @param context The CommandContext given by PaperSpigot and Brigadier
     */
    abstract fun noArgs(context: CommandContext<CommandSourceStack>): Int

}