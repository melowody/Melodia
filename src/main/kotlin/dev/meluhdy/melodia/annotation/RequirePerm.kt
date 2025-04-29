package dev.meluhdy.melodia.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequirePerm(val perm: String)
