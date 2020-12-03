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
fun Sequence<String>.countTreesBySlope(x: Int, y: Int) = drop(y)
    .filterIndexed { row, _ -> row % y == 0 }
    .filterIndexed { iteration, tile ->
        val column = (iteration + 1) * x % tile.length
        tile[column] == '#'
    }.count()

fun main(args: Array<String>) {
    with (day3Input(args.firstOrNull() ?: "./")) {
        println(countTreesBySlope(3, 1) == 289)

        // this value is supposed to be wrong but the simpler data works
        arrayOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2).fold(1.toBigInteger()) { total, (x, y) ->
            total * countTreesBySlope(x, y).toBigInteger()
        }
    }

    arrayOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2).fold(1) { total, (x, y) ->
        total * test.split("\n").asSequence().countTreesBySlope(x, y)
    } == 336

    with (test.split("\n").asSequence()) {
        println(countTreesBySlope(1, 1) == 2)
        println(countTreesBySlope(3, 1) == 7)
        println(countTreesBySlope(5, 1) == 3)
        println(countTreesBySlope(7, 1) == 4)
        println(countTreesBySlope(1, 2) == 2)
    }
}
