package dev.meluhdy.melodia.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.meluhdy.melodia.annotation.RequirePerm
import dev.meluhdy.melodia.annotation.UserOnly
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player

abstract class MelodiaCommand(literal: String) : LiteralArgumentBuilder<CommandSourceStack>(literal) {

    abstract val children: ArrayList<MelodiaCommand>

    init {
        children.forEach { command ->
            this.then(command)
        }

        this.executes { ctx ->
            if (!checkAnnotations(ctx)) return@executes Command.SINGLE_SUCCESS
            return@executes onCommand(ctx)
        }
    }

    private fun checkAnnotations(ctx: CommandContext<CommandSourceStack>): Boolean {
        val safeCommandMethod = this::class.java.getDeclaredMethod("onCommand", CommandContext::class.java)
        safeCommandMethod.annotations.forEach { annotation ->
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

    abstract fun onCommand(context: CommandContext<CommandSourceStack>): Int

}