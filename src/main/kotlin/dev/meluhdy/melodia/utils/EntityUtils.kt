package dev.meluhdy.melodia.utils

import org.bukkit.FireworkEffect
import org.bukkit.entity.Firework
import org.bukkit.entity.Player

object EntityUtils {

    fun detonateFirework(p: Player, vararg effects: FireworkEffect) {

        val firework = p.world.spawn(p.location, Firework::class.java)
        val fireworkMeta = firework.fireworkMeta

        for (effect in effects) {
            fireworkMeta.addEffect(effect)
        }
        fireworkMeta.power = 0

        firework.fireworkMeta = fireworkMeta
        firework.detonate()

    }

}