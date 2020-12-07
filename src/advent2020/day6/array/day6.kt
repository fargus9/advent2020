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

typealias Answers = Array<Int?>
val alphabet = 'a'.rangeTo('z')
fun blankArray(initializer: (Int) -> Int? = { 0 }): Answers = Array(alphabet.count(), initializer)
fun String.collectGroupAnswers(combine: (Answers, String) -> Answers): MutableList<Answers> = lineSequence()
    .fold(mutableListOf(blankArray())) { groupList, line ->
        val groupAnswers = if (line.isEmpty()) {
            blankArray()
        } else {
            val answers = groupList.removeAt(groupList.lastIndex)
            combine(answers, line)
        }
        groupList.also { it.add(groupAnswers) }
    }

fun String.collectCommonGroupAnswers() = collectGroupAnswers { answers, line ->
    line.forEach { answers[it - 'a'] = answers[it - 'a']?.plus(1) ?: 1 }
    answers
}

fun String.collectUnanimousGroupAnswers() = collectGroupAnswers { answers, line ->
    val intersection = blankArray { null }
    line.forEach { intersection[it - 'a'] = answers[it - 'a']?.plus(1) }
    intersection
}

fun main() {
    val testAnswers = sample.collectCommonGroupAnswers()
    assertEquals(11, testAnswers.sumBy { it.count { value -> value != null && value > 0 } })

    val pt1 = input.collectCommonGroupAnswers().sumBy { it.count { value -> value != null && value > 0 } }
    assertEquals(6585, pt1)

    assertEquals(6, sample.collectUnanimousGroupAnswers().sumBy { it.count { value -> value != null } })

    val pt2 = input.collectUnanimousGroupAnswers().sumBy { it.count { value -> value != null } }
    assertEquals(3276, pt2)
}
