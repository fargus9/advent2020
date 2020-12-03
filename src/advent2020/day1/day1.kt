package advent2020.day1

import java.io.File

fun day1Input(parent: String) = File(parent, "/1/input.txt").readLines()
    .asSequence()
    .map { it.toInt() }
    .sorted()
    .toList()
    .toIntArray()

tailrec fun find2020BySummingTwo(values: IntArray, lower: Int, upper: Int): Int {
    val lowerValue = values[lower]
    val upperValue = values[upper]
    val sum = lowerValue + upperValue
    return when {
        sum > 2020 -> find2020BySummingTwo(values, lower, upper - 1)
        sum < 2020 -> find2020BySummingTwo(values, lower + 1, upper)
        else -> lowerValue * upperValue
    }
}

tailrec fun find2020BySummingThree(values: IntArray, lower: Int, middle: Int, upper: Int): Int {
    val lowerValue = values[lower]
    val middleValue = values[middle]
    val upperValue = values[upper]
    val sum = lowerValue + middleValue + upperValue
    return when {
        middle == upper -> find2020BySummingThree(values, lower + 1, lower + 2, upper)
        sum > 2020 -> find2020BySummingThree(values, lower, middle, upper - 1)
        sum < 2020 -> find2020BySummingThree(values, lower, middle + 1, upper)
        else -> lowerValue * middleValue * upperValue
    }
}

fun main(repoPath: String = "./") = with (day1Input(repoPath)) {
    println(find2020BySummingTwo(this, 0, lastIndex))

    println(find2020BySummingThree(this, 0, 1, lastIndex))
}
