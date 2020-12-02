package advent2020.day2

import java.io.File

fun day2Input(parent: String) = File(parent, "/2/input.txt").readLines().asSequence().map {
    val (limitText, characterMatch, pwd) = it.split(" ")
    val (lower, upper) = limitText.split('-').map { limit -> limit.toInt() }
    Triple(lower.rangeTo(upper), characterMatch[0], pwd)
}

fun main(repoPath: String = "") = with (day2Input(repoPath)) {
    val boundedMatches = count { (bounds, match, pwd) ->
        bounds.contains(pwd.count { element -> element == match })
    }
    println(boundedMatches)

    val exactMatches = count { (bounds, match, pwd) ->
        (pwd[bounds.first - 1] == match) xor (pwd[bounds.last - 1] == match)
    }
    println(exactMatches)
}
