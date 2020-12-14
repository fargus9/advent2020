package advent2020.day13

import kotlin.test.assertEquals

typealias DepartingBus = Pair<Int, Int>
class SimpleSchedule(input: String) {
    private val earliestDeparture: Int
    private val ids: Sequence<Int>

    init {
        with (input.lineSequence()) {
            earliestDeparture = first().toInt()
            ids = last().splitToSequence(",").filterNot { it == "x" }.map { it.toInt() }
        }
    }

    fun nextBus(): DepartingBus = ids.map { it to (earliestDeparture / it + 1) * it }
        .minByOrNull { (_, departure) -> departure }!!
        .let { (id, departure) -> id to departure - earliestDeparture }
}

val DepartingBus.departingId: Int
    get() = first * second

class StaggeredSchedule(input: String) {
    private val ids: Array<DepartingBus> =
        input.splitToSequence(",").foldIndexed(mutableListOf<DepartingBus>()) { index, combined, value ->
            if (value != "x") { combined.add(value.toInt() to index) }
            combined
        }.toTypedArray()

    fun nextStaggeredArrivalStartTime(): Long {
        val firstId = ids.first().first.toLong()
        var increment = firstId
        var checkFirst = firstId
        var matchLength = 0
        var highestMatchLength = 0
        do {
            if (matchLength > highestMatchLength) {
                increment *= ids[matchLength].first
                highestMatchLength = matchLength
            }
            checkFirst += increment
            matchLength = checkNextValue(checkFirst, 1)
        } while (matchLength <= ids.lastIndex)
        return checkFirst
    }

    private tailrec fun checkNextValue(check: Long, index: Int): Int {
        if (index > ids.lastIndex) { return index }
        val (id, offset) = ids[index]
        return if ((check + offset) % id == 0L) { checkNextValue(check, index + 1) } else { index - 1 }
    }
}

fun main() {
    val pt1SampleSchedule = SimpleSchedule(samplePt1)
    val pt1SampleAnswer = pt1SampleSchedule.nextBus().departingId
    assertEquals(295, pt1SampleAnswer)

    val pt1Schedule = SimpleSchedule(input)
    val pt1Answer = pt1Schedule.nextBus().departingId
    assertEquals(5946, pt1Answer)

    tests.forEachIndexed { test, value ->
        val test1 = StaggeredSchedule(value.first)
        assertEquals(value.second, test1.nextStaggeredArrivalStartTime(), "$test failed")
    }

    val pt2SampleTest = StaggeredSchedule(samplePt1.lineSequence().last()).nextStaggeredArrivalStartTime()
    assertEquals(1068781, pt2SampleTest)

    val pt2Output = StaggeredSchedule(input.lineSequence().last()).nextStaggeredArrivalStartTime()
    assertEquals(645338524823718, pt2Output)
}
