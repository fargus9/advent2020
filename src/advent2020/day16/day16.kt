package advent2020.day16

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

data class RuleBounds(var lower: Int, var gapLower: Int, var gapUpper: Int, var upper: Int)

typealias Rules = Sequence<LongRange>
fun String.toRules() = lineSequence()
    .fold(sequenceOf<LongRange>()) { bounds, line ->
        val newBounds = "^[a-z ]+: ([0-9]+)-([0-9]+) or ([0-9]+)-([0-9]+)$".toRegex().find(line)!!.destructured
            .let { (l, ml, mu, up) -> sequenceOf(l.toLong()..ml.toLong(), mu.toLong()..up.toLong()) }
        bounds + newBounds
    }
fun String.toTickets(): Sequence<Sequence<Long>> = lineSequence()
    .map { it.splitToSequence(",").map { value -> value.toLong() } }

fun sumOfInvalidValuesForNearbyTickets(rules: String, nearbyTickets: String): Long {
    val bounds = rules.toRules()
    return nearbyTickets.toTickets()
        .flatMap { it }
        .filter { value -> bounds.none { value in it } }
        .reduce { total, value -> total + value }
}

fun findColumnsForRules(rulesInput: String, nearbyTickets: String) {
    val validBounds = rulesInput.toRules()
    val validRanges = nearbyTickets.toTickets()
        .filterNot { it.any { value -> validBounds.any { rule -> value !in rule } } }
        .fold(Array(validBounds.count() / 2) { LongArray(2) { if (it == 0) Long.MAX_VALUE else Long.MIN_VALUE }}) {
                ranges, values ->
            values.forEachIndexed { index, value ->
                ranges[index].let {
                    if (value < it[0]) { it[0] = value }
                    if (value > it[1]) { it[1] = value }
                }
            }
            ranges
        }
    val rules = validBounds.chunked(2).map { it.toTypedArray() }.toList().toTypedArray()
    val possibleMatches = rules.foldIndexed(Array(rules.size) { BooleanArray(rules.size) { true } }) { ruleIndex, possible, rule ->
        val validity = possible[ruleIndex]
        validRanges.forEachIndexed { valueIndex, (lower, upper) ->
            validity[valueIndex] = rule.all { range -> lower in range && upper in range }
        }
        possible
    }
    val valid = possibleMatches.indices.first { possibleMatches.all { possibilities -> possibilities[it] } }

}

fun main() {
    assertEquals(71, sumOfInvalidValuesForNearbyTickets(sampleRules, sampleNearbyTickets))

    val pt1Output = sumOfInvalidValuesForNearbyTickets(rules, nearbyTickets)
    assertEquals(19087, pt1Output)


}
