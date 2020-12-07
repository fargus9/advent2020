package advent2020.day6.array

import advent2020.day6.input
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

typealias Answers = Array<Boolean?>
val alphabet = 'a'.rangeTo('z')
fun defaultAnswers(initializer: (Int) -> Boolean? = { false }): Answers = Array(alphabet.count(), initializer)
fun String.collectGroupAnswers(combine: (Answers, String) -> Answers): MutableList<Answers> = lineSequence()
    .fold(mutableListOf(defaultAnswers())) { groupList, line ->
        val groupAnswers = if (line.isEmpty()) {
            defaultAnswers()
        } else {
            val answers = groupList.removeAt(groupList.lastIndex)
            combine(answers, line)
        }
        groupList.also { it.add(groupAnswers) }
    }

fun String.collectCommonGroupAnswers() = collectGroupAnswers { answers, line ->
    line.forEach { answers[it - 'a'] = answers[it - 'a']?.or(true) }
    answers
}

fun String.collectUnanimousGroupAnswers() = collectGroupAnswers { answers, line ->
    val intersection = defaultAnswers { null }
    line.forEach { intersection[it - 'a'] = answers[it - 'a']?.or(true) }
    intersection
}

fun Answers.countValid() = count { value -> value?.equals(true) == true }
fun List<Answers>.accumulate() = sumBy { it.countValid() }
fun main() {
    val testAnswers = sample.collectCommonGroupAnswers()
    assertEquals(11, testAnswers.accumulate())

    val pt1 = input.collectCommonGroupAnswers().accumulate()
    assertEquals(6585, pt1)

    assertEquals(6, sample.collectUnanimousGroupAnswers().accumulate())

    val pt2 = input.collectUnanimousGroupAnswers().accumulate()
    assertEquals(3276, pt2)
}
