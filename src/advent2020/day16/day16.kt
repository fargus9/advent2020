package advent2020.day16

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

fun String.toRules() = lineSequence()
    .fold(sequenceOf<LongRange>()) { bounds, line ->
        val newBounds = "^[a-z ]+: ([0-9]+)-([0-9]+) or ([0-9]+)-([0-9]+)$".toRegex().find(line)!!.destructured
            .let { (l, ml, mu, up) -> sequenceOf(l.toLong()..ml.toLong(), mu.toLong()..up.toLong()) }
        bounds + newBounds
    }

fun String.toTicket(): Sequence<Long> = splitToSequence(",").map { value -> value.toLong() }
fun String.toTickets(): Sequence<Sequence<Long>> = lineSequence().map { it.toTicket() }

fun sumOfInvalidValuesForNearbyTickets(rules: String, nearbyTickets: String): Long {
    val bounds = rules.toRules()
    return nearbyTickets.toTickets()
        .flatMap { it }
        .filter { value -> bounds.none { value in it } }
        .reduce { total, value -> total + value }
}

fun findColumnsForRules(rulesInput: String, nearbyTickets: String, ticket: Sequence<Long>): IntArray? {
    val rules = rulesInput.toRules().chunked(2).map { it.toTypedArray() }.toList().toTypedArray()
    val validTickets = nearbyTickets.toTickets()
        .filter { it.all { value -> rules.any { rule -> rule.any { range -> value in range } } } }

    val columnCount = ticket.count()
    val possibleMatches = validTickets.plus(sequenceOf(ticket))
        .fold(Array(rules.size) { BooleanArray(columnCount) { true } }) { possibilities, values ->
            values.forEachIndexed { valueIndex, value ->
                rules.forEachIndexed { rulesIndex, rule ->
                    possibilities[rulesIndex][valueIndex] = possibilities[rulesIndex][valueIndex] && rule.any { value in it }
                }
            }
            possibilities
        }

    return findCombination(IntArray(rules.size) { -1 }, possibleMatches)
}

fun findCombination(given: IntArray, possibilities: Array<BooleanArray>): IntArray? {
    if (given.none { it == -1 }) return given
    val undecidedRules = possibilities.indices.asSequence().filter { given[it] == -1 }
    val undecidedValues = possibilities.first().indices.asSequence().filterNot { given.contains(it) }
    if (undecidedValues.any { possibilities.count { column -> column[it] } == 0 }) return null
    undecidedValues.sortedBy { possibilities.count { column -> column[it] } }
        .forEach { trialValue ->
            undecidedRules.forEach { trialRule ->
                if (possibilities[trialRule][trialValue]) {
                    findCombination(given.copyOf().also { next -> next[trialRule] = trialValue }, possibilities)
                        ?.let { found -> return@findCombination found }
                }
            }
        }
    return null
}

fun main() {
    assertEquals(71, sumOfInvalidValuesForNearbyTickets(sampleRules, sampleNearbyTickets))

    val pt1Output = sumOfInvalidValuesForNearbyTickets(rules, nearbyTickets)
    assertEquals(19087, pt1Output)

    val sampleColumns = findColumnsForRules(sampleRules, sampleNearbyTickets, sampleTicket.toTicket())!!
    assertEquals(1, sampleColumns[0])
    assertEquals(0, sampleColumns[1])
    assertEquals(2, sampleColumns[2])

    val ruleColumns = findColumnsForRules(rules, nearbyTickets, ticket.toTicket())!!
    val yourTicket = ticket.split(",")
    val departingRules = rules.lineSequence().mapIndexed { index: Int, it: String ->
        if (it.startsWith("departure")) index else -1 }
        .filterNot { it == -1 }
    val pt2Output = departingRules.map { yourTicket[ruleColumns[it]].toLong() }
        .reduce { total, column -> total * column }

    assertEquals(1382443095281, pt2Output)
}
