package advent2020.day9

import kotlin.test.assertEquals

const val sample = """35
20
15
25
47
40
62
55
65
95
102
117
150
182
127
219
299
277
309
576"""

// A slight tweak on the solution from day 1 pt1
typealias Values = Array<Long>
tailrec fun findNumberBySummingTwo(values: Values, lower: Int, upper: Int, search: Long): Boolean {
    val lowerValue = values[lower]
    val upperValue = values[upper]
    val sum = lowerValue + upperValue
    return when {
        upper == lower && sum != search -> false
        sum > search -> findNumberBySummingTwo(values, lower, upper - 1, search)
        sum < search -> findNumberBySummingTwo(values, lower + 1, upper, search)
        sum == search -> true
        else -> false
    }
}

fun Values.takingSubset(drop: Int, taking: Int) = asSequence().drop(drop).take(taking)
typealias Subset = Pair<Sequence<Long>, Long>
fun Values.rollingSubsets(taking: Int): Sequence<Subset> = sequence {
    (0..lastIndex - taking).forEach { yield(takingSubset(it, taking) to this@rollingSubsets[it + taking]) }
}

fun Values.findFirst(taking: Int): Long = rollingSubsets(taking).first { (values, search) ->
    values.sorted().toList().toTypedArray().let { !findNumberBySummingTwo(it, 0, it.lastIndex, search) }
}.second

fun Values.findFirstRuleBreaker(taking: Int): Long {
    var lower = 0
    var search: Int
    var foundValue: Boolean
    do {
        search = lower + taking
        val consider = takingSubset(lower, taking).sorted().toList().toTypedArray()
        foundValue = findNumberBySummingTwo(consider,0, consider.lastIndex, this[search])
        lower += 1
    } while (foundValue && (lower + taking) < lastIndex)
    return this[search]
}

fun Values.findRangeBySumming(search: Long): Long {
    var lower = 0
    var considering = 2
    var runningTotal = this[0] + this[1]
    while (runningTotal != search) {
        if (runningTotal > search) {
            runningTotal -= this[lower]
            lower += 1
        } else {
            runningTotal += this[considering]
            considering += 1
        }
        if (runningTotal == 0L) {
            considering = lower + 1
        }
    }

    return takingSubset(lower, considering - lower).run { minOf { it } + maxOf { it } }
}

fun main() {
    val sampleValues = sample.lineSequence().map { it.toLong() }.toList().toTypedArray()
    val sampleOutput = sampleValues.findFirstRuleBreaker(5)
    assertEquals(127L, sampleOutput)
    assertEquals(127L, sampleValues.findFirst(5))

    val values = input.lineSequence().map { it.toLong() }.toList().toTypedArray()
    val pt1Output = values.findFirstRuleBreaker(25)
    assertEquals(2089807806L, pt1Output)
    assertEquals(2089807806L, values.findFirst(25))

    val sampleOutput2 = sampleValues.findRangeBySumming(127L)
    assertEquals(62L, sampleOutput2)

    val pt2Output = values.findRangeBySumming(2089807806L)
    assertEquals(245848639, pt2Output)
}
