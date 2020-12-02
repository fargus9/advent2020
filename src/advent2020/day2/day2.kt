package advent2020.day2

import java.io.File

fun day2Input(parent: String = "") = File(parent, "/2/input.txt").readLines().asSequence().map {
    val (limitText, characterMatch, pwd) = it.split(" ")
    val (lower, upper) = limitText.split('-').map { limit -> limit.toInt() }
    Triple(lower.rangeTo(upper), characterMatch[0], pwd)
}

fun Sequence<Triple<IntRange, Char, String>>.boundedMatches() = count { (bounds, match, pwd) ->
    bounds.contains(pwd.count { element -> element == match })
}

fun Sequence<Triple<IntRange, Char, String>>.exactMatches() = count { (bounds, match, pwd) ->
    (pwd[bounds.first - 1] == match) xor (pwd[bounds.last - 1] == match)
}

fun main() = with (day2Input()) {
    println(boundedMatches())

    println(exactMatches())
}
