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

typealias Answers = Set<Char>
fun String.collectGroupAnswers(): MutableList<Answers> = splitToSequence("\n")
    .fold(mutableListOf(mutableSetOf())) { groupList, line ->
        val groupAnswers = if (line.isEmpty()) {
            setOf()
        } else {
            val answers = groupList.removeAt(groupList.lastIndex)
            answers.plus(line.asIterable())
        }
        groupList.also { it.add(groupAnswers) }
    }

fun defaultAnswers(): Answers = 'a'.rangeTo('z').toMutableSet()
fun String.collectGroupUnanimousAnswers(): MutableList<Answers> = splitToSequence("\n")
    .fold(mutableListOf(defaultAnswers())) { groupList, line ->
        val groupAnswers = if (line.isEmpty()) {
            defaultAnswers()
        } else {
            val groupAnswers = groupList.removeAt(groupList.lastIndex)
            groupAnswers.intersect(line.asIterable())
        }
        groupList.also { it.add(groupAnswers) }
    }

fun main() {
    val testAnswers = sample.collectGroupAnswers()
    assertEquals(setOf('a', 'b', 'c'), testAnswers[0])
    assertEquals(setOf('a', 'b', 'c'), testAnswers[1])
    assertEquals(setOf('a', 'b', 'c'), testAnswers[2])
    assertEquals(setOf('a'), testAnswers[3])
    assertEquals(setOf('b'), testAnswers[4])
    assertEquals(11, testAnswers.sumBy { it.size })

    val pt1 = input.collectGroupAnswers().sumBy { it.size }
    println(pt1)
    assertEquals(6585, pt1)

    assertEquals(6, sample.collectGroupUnanimousAnswers().sumBy { it.size })

    val pt2 = input.collectGroupUnanimousAnswers().sumBy { it.size }
    println(pt2)
    assertEquals(3276, pt2)
}
