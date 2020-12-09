package advent2020.day2

import java.io.File
import kotlin.test.assertEquals

fun day2Input(parent: String) = File(parent, "/2/input.txt").readLines().map {
    val (limitText, characterMatch, pwd) = it.split(" ")
    val (lower, upper) = limitText.split('-').map { limit -> limit.toInt() }
    Triple(lower..upper, characterMatch[0], pwd)
}

fun main() = with (day2Input("./")) {
    val part1 = count { (bounds, match, pwd) ->
        pwd.count { element -> element == match } in bounds
    }
    assertEquals(546, part1)

    val part2 = count { (bounds, match, pwd) ->
        (pwd[bounds.first - 1] == match) xor (pwd[bounds.last - 1] == match)
    }
    assertEquals(275, part2)
}
