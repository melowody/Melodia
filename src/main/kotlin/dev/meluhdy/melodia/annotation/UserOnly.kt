package dev.meluhdy.melodia.annotation

/**
 * Requires the command to be run by a player
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class UserOnly