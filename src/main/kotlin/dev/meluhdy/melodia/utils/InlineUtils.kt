package dev.meluhdy.melodia.utils

/**
 * Returns the next enum value as declared in the class. If this is the last enum declared,
this will wrap around to return the first declared enum.
 *
 * Taken from https://stackoverflow.com/questions/15018439/android-get-the-next-or-previous-enum
 *
 * @param values an optional array of enum values to be used; this can be used in order to
 * cache access to the values() array of the enum type and reduce allocations if this is
 * called frequently.
 */
inline fun <reified T : Enum<T>> Enum<T>.next(values: Array<T> = enumValues()) =
    values[(ordinal + 1) % values.size]

/**
 * Returns the previous enum value as declared in the class. If this is the first enum declared,
this will wrap around to return the last declared enum.
 *
 * Taken from https://stackoverflow.com/questions/15018439/android-get-the-next-or-previous-enum
 *
 * @param values an optional array of enum values to be used; this can be used in order to
 * cache access to the values() array of the enum type and reduce allocations if this is
 * called frequently.
 */
inline fun <reified T : Enum<T>> Enum<T>.prev(values: Array<T> = enumValues()) =
    values[(ordinal - 1) % values.size]