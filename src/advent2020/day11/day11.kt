package advent2020.day11

import kotlin.test.assertEquals

fun CharArray.indexOfFirstFrom(from: Int, predicate: (Char) -> Boolean): Int {
    for (index in from..lastIndex) {
        if (predicate(this[index])) {
            return index
        }
    }
    return -1
}

class Seats(input: String) {
    private var nextState: CharArray
    var seats: CharArray
    private var rowSize: Int = input.indexOfFirst { it == '\n' } + 2
    private val surrounding = arrayOf(-1, 1, -(rowSize + 1), -rowSize, -(rowSize -1), rowSize + 1, rowSize, rowSize - 1)

    init {
        val padded = emptyRow(rowSize) + input.lineSequence().map { ".$it." }.toList() + emptyRow(rowSize)
        seats = padded.joinToString("").toCharArray()
        nextState = seats.copyOf()
    }

    private fun occupiedAdjacentTo(seat: Int) = surrounding.count { seats[seat + it] == '#' }

    fun applyRules(): Boolean {
        var considering = seats.indexOfFirst { it == 'L' || it == '#' }
        val range = seats.indices
        seats.copyInto(nextState)
        var anyChanged = false
        while (considering in range) {
            when (seats[considering]) {
                'L' -> if (occupiedAdjacentTo(considering) == 0) '#' else null
                '#' -> if (occupiedAdjacentTo(considering) > 3) 'L' else null
                else -> null
            }?.let { anyChanged = true ; nextState[considering] = it }
            considering = seats.indexOfFirstFrom(considering + 1) { it == '#' || it == 'L' }
        }
        if (anyChanged) {
            nextState.copyInto(seats)
        }
        return anyChanged
    }

    fun settle() {
        @Suppress("ControlFlowWithEmptyBody")
        while (applyRules()) {}
    }

    val occupiedCount: Int
        get() = seats.count { it == '#' }

    override fun toString(): String {
        return seats.asList()
            .chunked(rowSize)
            .drop(1)
            .dropLast(1)
            .joinToString("\n") { it.drop(1).dropLast(1).joinToString("") }
    }

    fun applyLineOfSightRules(): Boolean {
        TODO("Not yet implemented")
    }

    fun settleLineOfSight() {
        @Suppress("ControlFlowWithEmptyBody")
        while (applyLineOfSightRules()) {}
    }

    companion object {
        fun emptyRow(rowSize: Int) = listOf(CharArray(rowSize) { '.' }.joinToString(""))
    }
}

fun main() {
    val testSeats = Seats(sample)
    testPasses.forEachIndexed { index, it ->
        testSeats.applyRules()
        assertEquals(it, testSeats.toString(), "comparing rules applied to test pass $index")
    }
    val sampleSeats = Seats(sample)
    sampleSeats.settle()
    assertEquals(37, sampleSeats.occupiedCount)

    val inputSeats = Seats(input)
    inputSeats.settle()
    assertEquals(2204, inputSeats.occupiedCount)

    val largeTestSeats = Seats(sample)
    testPasses.forEachIndexed { index, it ->
        largeTestSeats.applyLineOfSightRules()
        assertEquals(it, largeTestSeats.toString(), "comparing rules applied to test pass $index")
    }
    val lineOfSightSample = Seats(sample)
    lineOfSightSample.settleLineOfSight()
    assertEquals(26, lineOfSightSample.occupiedCount)

    val lineOfSightSeats = Seats(input)
    lineOfSightSeats.settleLineOfSight()
    println(lineOfSightSeats.occupiedCount)
}
