package dev.meluhdy.melodia.annotations

/**
 * Annotation for commands that require permissions
 *
 * @param perm The permission required for the command
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequirePerm(val perm: String)