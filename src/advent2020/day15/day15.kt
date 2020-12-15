package advent2020.day15

import kotlin.test.assertEquals

fun String.findNthSpoken(limit: Int): Int {
    val recent = splitToSequence(",")
        .mapIndexed { index, recent -> recent.toInt() to (0 to index + 1) }
        .associate { it }
        .toMutableMap()
    var lastSpoken = "${last()}".toInt()
    var turn = recent.count() + 1
    while (turn <= limit) {
        val spoken = recent[lastSpoken]?.let { if (it.first > it.second) it.first - it.second else 0 } ?: 0
        recent[spoken] = recent[spoken]?.let {
            if (it.first > it.second) turn to it.first else turn to it.second }
            ?: 0 to turn
        lastSpoken = spoken
        turn += 1
    }
    return lastSpoken
}

fun main() {
    samples.forEach { (input, expected) -> assertEquals(expected, input.findNthSpoken(2020)) }

    assertEquals(273, input.findNthSpoken(2020))

    largeSamples.forEach { (input, expected) -> assertEquals(expected, input.findNthSpoken(30000000)) }

    assertEquals(47205, input.findNthSpoken(30000000))
}
