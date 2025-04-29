package dev.meluhdy.melodia

import dev.meluhdy.melodia.command.MelodiaCommand
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin

abstract class MelodiaPlugin : JavaPlugin() {

    abstract fun getCommands() : ArrayList<MelodiaCommand>

    override fun onEnable() {

        println("FUCK!!!!")

        lifecycleManager.registerEventHandler(
            LifecycleEvents.COMMANDS
        ) { commands ->
            getCommands().forEach { command ->
                println("Registering ${command.literal}")
                commands.registrar().register(command.build())
            }
        }

    }

}