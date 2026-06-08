package dev.meluhdy.melodia.listener

import dev.meluhdy.melodia.Melodia
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.util.*

object PromptListener : Listener {

    val prompts: HashMap<UUID, (TextComponent) -> Unit> = HashMap()

    @EventHandler(priority = EventPriority.LOWEST)
    fun on(e: AsyncChatEvent) {
        val player = e.player
        Melodia.plugin.logger.debug("Received Message: ${(e.message() as TextComponent).content()}")
        if (!prompts.containsKey(player.uniqueId)) return
        Melodia.plugin.logger.debug("Prompted Player: ${player.name}")
        e.isCancelled = true
        val callback = prompts[player.uniqueId]
        prompts.remove(player.uniqueId)
        Bukkit.getScheduler().scheduleSyncDelayedTask(Melodia.plugin) {
            callback?.invoke(e.message() as TextComponent)
        }
    }

}