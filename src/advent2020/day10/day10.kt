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
val cache = mutableMapOf<String, Long>()
fun findValidCombinations(consider: Joltage, considering: Int, target: Int): Long = cache.getOrPut("$considering:$target") {
    val leastPossible = consider.indexOfFirst { (target - it) <= 3 }
    if (leastPossible == -1) { return if (target <= 3) 1L else 0L }
    return considering.downTo(leastPossible).asSequence()
        .map { it - 1 to consider[it] }
        .map { (nowConsidering, newTarget) -> findValidCombinations(consider, nowConsidering, newTarget) }
        .sum() + if (target <= 3) 1L else 0L
}

fun Joltage.indexOfLastFrom(from: Int, predicate: (Int) -> Boolean): Int {
    for (index in (from..lastIndex).reversed()) {
        if (predicate(this[index])) {
            return index
        }
    }
    return -1
}

fun findFromBottom(values: Joltage, target: Int): Long = sequence {
    val consider = arrayOf(0) + values + arrayOf(target)
    var considering = 0
    var from = 0
    while (from < target) {
        val lastFit = consider.indexOfLastFrom(considering) { it - from <= 3 }
        var combos = when (lastFit - considering) {
            3 -> 7L
            2 -> 3L
            else -> 1
        }
        var reduce = 1
        while (considering < lastFit) {
            considering += 1
            from = consider[considering]
            if (considering != lastFit && consider.indexOfLastFrom(considering) { it - from <= 3 } <= lastFit) {
                combos -= reduce
            }
            reduce += 1
        }
        yield(combos)
    }
}.reduce { total, count -> total * count }

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
        assertEquals(8L, findFromBottom(this, last() + 3))
        findValidCombinations(this, lastIndex, last() + 3)
    }
    assertEquals(8L, smallCombinations)
    cache.clear()

    val largeCombinations = with (largeJolts.toList().toTypedArray()) {
        assertEquals(19208L, findFromBottom(this, last() + 3))
        findValidCombinations(this, lastIndex,last() + 3)
    }
    assertEquals(19208L, largeCombinations)
    cache.clear()

    val pt2output = with (inputJolts.toList().toTypedArray()) { findFromBottom(this,last() + 3) }
    assertEquals(6044831973376, pt2output)
}
