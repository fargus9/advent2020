package advent2020.day3

import java.io.File

val test = """..##.......
#...#...#..
.#....#..#.
..#.#...#.#
.#...##..#.
..#.##.....
.#.#.#....#
.#........#
#.##...#...
#...##....#
.#..#...#.#"""

fun day3Input(parent: String) = File(parent, "/3/input.txt").readLines().asSequence()

// something about the filtering and column calculation isn't right with the larger tileset
fun Sequence<String>.countTreesBySlope(x: Int, y: Int, output: Boolean = false) = drop(y)
    .filterIndexed { row, _ -> row % y == 0 }
    .foldIndexed(0) { row, total, tile ->
        val column = (row + 1) * x % tile.length
        if (output) {
            val replacement = if (tile[column] == '#') 'X' else 'O'
            println(tile.replaceRange(column, column + 1, replacement.toString()))
        }
        total + if (tile[column] == '#') 1 else 0
    }

fun main() {
    with (day3Input("../../../")) {
        println(drop(2).filterIndexed { row, _ -> row % 2 == 0 }.count())

        println(countTreesBySlope(3, 1) == 289)

        // this value is supposed to be wrong but the simpler data works
        arrayOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2).fold(1) { total, (x, y) ->
            total * countTreesBySlope(x, y)
        }
    }  // 1227434288 bad 307034024 too

    arrayOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2).fold(1) { total, (x, y) ->
        total * test.split("\n").asSequence().countTreesBySlope(x, y)
    } == 336

    test.split("\n").asSequence().countTreesBySlope(1, 1) == 2
    test.split("\n").asSequence().countTreesBySlope(3, 1) == 7
    test.split("\n").asSequence().countTreesBySlope(5, 1) == 3
    test.split("\n").asSequence().countTreesBySlope(7, 1) == 4
    test.split("\n").asSequence().countTreesBySlope(1, 2) == 2
}
