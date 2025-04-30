package dev.meluhdy.melodia.annotation

/**
 * Forces a command to require a permission to run
 *
 * @param perm The permission required to run the command
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequirePerm(val perm: String)
