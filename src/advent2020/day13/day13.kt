package advent2020.day13

import com.sun.org.apache.xpath.internal.operations.Bool
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
    private val ids: List<DepartingBus>
    init {
        ids = input.splitToSequence(",").foldIndexed(mutableListOf()) { index, combined, value ->
            if (value != "x") { combined.add(value.toInt() to index) }
            combined
        }
    }

    fun nextStaggeredArrivalStartTime(): Long {
        val firstId: Long = ids.first().first.toLong()
        var checkFirst = firstId
        do {
            checkFirst += firstId
        } while (!checkNextValue(checkFirst, 1))
        return checkFirst
    }

    private tailrec fun checkNextValue(check: Long, index: Int): Boolean {
        if (index > ids.lastIndex) { return true }
        val (id, offset) = ids[index]
        val nextStep = (check / id + 1) * id
        return if (check + offset == nextStep) { checkNextValue(check, index + 1) } else { false }
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

    val pt2Test = StaggeredSchedule(input.lineSequence().last()).nextStaggeredArrivalStartTime()
    println(pt2Test)
}
