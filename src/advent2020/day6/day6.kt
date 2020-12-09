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
fun String.collectGroupAnswers(defaultAnswers:Answers = setOf(), combine: (Answers, Iterable<Char>) -> Answers): MutableList<Answers> = splitToSequence("\n")
    .fold(mutableListOf(defaultAnswers)) { groupList, line ->
        val groupAnswers = if (line.isEmpty()) {
            defaultAnswers
        } else {
            val answers = groupList.removeAt(groupList.lastIndex)
            combine(answers, line.asIterable())
        }
        groupList.also { it.add(groupAnswers) }
    }

fun defaultAnswers(): Answers = ('a'..'z').toMutableSet()
fun String.collectCommonGroupAnswers() = collectGroupAnswers { answers, line -> answers.plus(line) }
fun String.collectUnanimousGroupAnswers() = collectGroupAnswers(defaultAnswers()) { answers, line -> answers.intersect(line) }

fun main() {
    val testAnswers = sample.collectCommonGroupAnswers()
    assertEquals(setOf('a', 'b', 'c'), testAnswers[0])
    assertEquals(setOf('a', 'b', 'c'), testAnswers[1])
    assertEquals(setOf('a', 'b', 'c'), testAnswers[2])
    assertEquals(setOf('a'), testAnswers[3])
    assertEquals(setOf('b'), testAnswers[4])
    assertEquals(11, testAnswers.sumBy { it.size })

    val pt1 = input.collectCommonGroupAnswers().sumBy { it.size }
    assertEquals(6585, pt1)

    assertEquals(6, sample.collectUnanimousGroupAnswers().sumBy { it.size })

    val pt2 = input.collectUnanimousGroupAnswers().sumBy { it.size }
    assertEquals(3276, pt2)
}
