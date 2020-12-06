package advent2020.day6

import kotlin.test.assertEquals

const val sample = """abc

a
b
c

ab
ac

a
a
a
a

b"""

typealias Answers = MutableMap<Char, Int>

fun String.collectGroupAnswers(): MutableList<Answers> = splitToSequence("\n")
    .fold(mutableListOf(mutableMapOf())) { groupList, line ->
        if (line.isEmpty()) {
            groupList.add(mutableMapOf())
        } else {
            with (groupList.last()) {
                line.forEach { put(it, getOrDefault(it, 1)) }
            }
        }
        groupList
    }

fun defaultAnswers(): Answers = 'a'.rangeTo('z').associateWith { 0 }.toMutableMap()
fun String.collectGroupUnanimousAnswers(): MutableList<Answers> = splitToSequence("\n")
    .fold(mutableListOf(defaultAnswers())) { groupList, line ->
        if (line.isEmpty()) {
            groupList.add(defaultAnswers())
        } else {
            with (groupList.last()) {
                val agreedUpon = keys.intersect(line.asIterable()).associateWith { getOrDefault(it, 0) + 1 }
                clear()
                putAll(agreedUpon)
            }
        }
        groupList
    }


fun main() {
    val testAnswers = sample.collectGroupAnswers()
    assertEquals(setOf('a', 'b', 'c'), testAnswers[0].keys)
    assertEquals(setOf('a', 'b', 'c'), testAnswers[1].keys)
    assertEquals(setOf('a', 'b', 'c'), testAnswers[2].keys)
    assertEquals(setOf('a'), testAnswers[3].keys)
    assertEquals(setOf('b'), testAnswers[4].keys)
    assertEquals(11, testAnswers.sumBy { it.keys.size })

    val pt1 = input.collectGroupAnswers().sumBy { it.keys.size }
    println(pt1)
    assertEquals(6585, pt1)

    assertEquals(6, sample.collectGroupUnanimousAnswers().sumBy { it.keys.size })

    val pt2 = input.collectGroupUnanimousAnswers().sumBy { it.keys.size }
    println(pt2)
}
