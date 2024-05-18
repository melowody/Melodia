package dev.meluhdy.melodia.annotations

import java.lang.Exception

/**
 * Thrown when an annotation is expected but is not found.
 *
 * @param errorMessage The message for the error to contain when thrown.
 */
class MissingAnnotationException(errorMessage: String) : Exception(errorMessage)