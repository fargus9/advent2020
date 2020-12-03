package advent2020.day3

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.io.File

const val test = """..##.......
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

fun Sequence<String>.countTreesWithSlope(x: Int, y: Int) = drop(y)
    .filterIndexed { row, _ -> row % y == 0 }
    .filterIndexed { iteration, tile ->
        val column = (iteration + 1) * x % tile.length
        tile[column] == '#'
    }.count()

fun main() = runBlocking {
    val slopesToCheck = flowOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2)
    with (day3Input("./")) {
        println(countTreesWithSlope(3, 1) == 289)

        val computation = slopesToCheck.map { (x, y) -> countTreesWithSlope(x, y).toBigInteger() }
            .buffer()
            .reduce { total, value -> total * value }
        println(computation)
    }

    with (test.split("\n").asSequence()) {
        println(slopesToCheck.map { (x, y) -> countTreesWithSlope(x, y) }
            .buffer()
            .reduce { total, value -> total * value } == 336)

        println(countTreesWithSlope(1, 1) == 2)
        println(countTreesWithSlope(3, 1) == 7)
        println(countTreesWithSlope(5, 1) == 3)
        println(countTreesWithSlope(7, 1) == 4)
        println(countTreesWithSlope(1, 2) == 2)
    }
}
