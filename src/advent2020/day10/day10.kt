package advent2020.day10

import kotlin.test.assertEquals

typealias Jolts = Sequence<Int>
fun String.toJolts() = lineSequence().map { it.toInt() }.sorted()

typealias JoltDistribution = Map<Int, Int>
fun Jolts.simpleJoltDistribution(): JoltDistribution = windowed(2)
    .fold(mutableMapOf(1 to 1, 3 to 1)) { distribution, (first, second) ->
        val difference = second - first
        distribution[difference] = distribution[difference]!! + 1
        distribution
    }

fun JoltDistribution.product() = asSequence().map { (_, count) -> count }.reduce { total, count -> total * count }

typealias Joltage = Array<Int>
val matchCache = mutableMapOf<Pair<Int, Int>, Long>()
fun findValidCombinations(consider: Joltage, considering: Int, target: Int): Long {
    val leastPossible = consider.indexOfFirst { (target - it) <= 3 }
    if (leastPossible == -1) { return if (target <= 3) 1L else 0L }
    return (((leastPossible..considering).asSequence()
        .map { it - 1 to consider.elementAt(it) }
        .map { (nowConsidering, newTarget) -> findValidCombinations(consider, nowConsidering, newTarget) })
            + sequenceOf(if (target <= 3) 1L else 0L ))
        .sum()
}

fun main() {
    val smallJolts = sample1.toJolts()
    val smallProduct = smallJolts.simpleJoltDistribution().product()
    assertEquals(35, smallProduct)

    val largeJolts = sample2.toJolts()
    val largeProduct = largeJolts.simpleJoltDistribution().product()
    assertEquals(220, largeProduct)

    val inputJolts = input.toJolts()
    val pt1Output = inputJolts.simpleJoltDistribution().product()
    assertEquals(2346, pt1Output)

    val smallCombinations = with (smallJolts.toList().toTypedArray()) {
        findValidCombinations(this, lastIndex, last() + 3)
    }
    assertEquals(8L, smallCombinations)

    val largeCombinations = with (largeJolts.toList().toTypedArray()) {
        findValidCombinations(this, lastIndex,last() + 3)
    }
    assertEquals(19208L, largeCombinations)

    val pt2output = with (inputJolts.toList().toTypedArray()) {
        findValidCombinations(this, lastIndex,last() + 3)
    }
    println(pt2output)
}
