package dev.meluhdy.melodia.annotations

/**
 * Annotation for commands that can only be run by users
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class UserOnly
