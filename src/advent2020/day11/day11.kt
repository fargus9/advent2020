package advent2020.day11

import java.lang.Integer.max
import java.lang.Math.min
import kotlin.test.assertEquals

fun CharArray.indexOfFirstFrom(from: Int, predicate: (Char) -> Boolean): Int {
    for (index in from..lastIndex) {
        if (predicate(this[index])) {
            return index
        }
    }
    return -1
}

typealias OccupancyRules = Seats.(Char, Int) -> Char?
class Seats(input: String) {
    private var nextState: CharArray
    private var seats: CharArray
    private var rowSize: Int = input.indexOfFirst { it == '\n' } + 2
    private val surrounding = arrayOf(-1, 1, -(rowSize + 1), -rowSize, -(rowSize -1), rowSize + 1, rowSize, rowSize - 1)
    private val vertical: IntArray
    private val horizontal: IntArray
    private val increasing: IntArray
    private val decreasing: IntArray

    init {
        val padded = emptyRow(rowSize) + input.lineSequence().map { ".$it." }.toList() + emptyRow(rowSize)
        seats = padded.joinToString("").toCharArray()
        nextState = seats.copyOf()
        vertical = IntArray(rowSize)
        horizontal = IntArray(rowSize)
        increasing = IntArray(rowSize * 2)
        decreasing = IntArray( rowSize * 2)
    }

    private fun applyRules(flip: OccupancyRules): Boolean {
        var considering = seats.indexOfFirst { it != '.' }
        val range = seats.indices
        seats.copyInto(nextState)
        var anyChanged = false
        while (considering in range) {
            this.flip(seats[considering], considering)?.let { anyChanged = true ; nextState[considering] = it }
            considering = seats.indexOfFirstFrom(considering + 1) { it != '.'}
        }
        if (anyChanged) {
            nextState.copyInto(seats)
        }
        return anyChanged
    }

    private fun occupiedAdjacentTo(seat: Int) = surrounding.count { seats[seat + it] == '#' }

    private val adjacencyRules: OccupancyRules = { value, considering ->
        when (value) {
            'L' -> if (occupiedAdjacentTo(considering) == 0) '#' else null
            '#' -> if (occupiedAdjacentTo(considering) > 3) 'L' else null
            else -> null
        }
    }
    fun applyAdjacencyRules() = applyRules(adjacencyRules)

    fun settle() {
        @Suppress("ControlFlowWithEmptyBody")
        while (applyAdjacencyRules()) {}
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

    fun settleLineOfSight() {
        @Suppress("ControlFlowWithEmptyBody")
        while (applyLineOfSightRules()) {}
    }

    private val lineOfSightRules: OccupancyRules = { value, considering ->
        when (value) {
            'L' -> if (occupiedLineOfSightTo(considering) == 0) '#' else null
            // this is because a filled seat itself accounts for 4 using this algorithm
            '#' -> if (occupiedLineOfSightTo(considering) > 8) 'L' else null
            else -> null
        }
    }

    private fun occupiedLineOfSightTo(seat: Int) = seat.seatSlopeIndices.let{ (columnIndex, rowIndex, diagonalIndex) ->
        vertical[columnIndex].coerceAtMost(3) +
                horizontal[rowIndex].coerceAtMost(3) +
                increasing[diagonalIndex].coerceAtMost(3) +
                decreasing[diagonalIndex].coerceAtMost(3)
    }

    private val Int.seatSlopeIndices: Array<Int>
        get() {
            val columnIndex = this % rowSize
            val rowIndex = this / rowSize
            val decreasingIndex  = rowIndex - columnIndex + rowSize
            val increasingIndex  = columnIndex - rowIndex + rowSize
            return arrayOf(columnIndex, rowIndex, decreasingIndex, increasingIndex)
        }
    
    fun applyLineOfSightRules(): Boolean {
        sequenceOf(vertical, horizontal, increasing, decreasing).forEach { it.reset() }
        var considering = seats.indexOfFirst { it == '#' }
        val seatRange = seats.indices
        while (considering in seatRange) {
            val (columnIndex, rowIndex, decreasingIndex, increasingIndex) = considering.seatSlopeIndices
            vertical[columnIndex] += 1
            horizontal[rowIndex] += 1
            decreasing[decreasingIndex] += 1
            increasing[increasingIndex] += 1

            considering = seats.indexOfFirstFrom(considering + 1) { it == '#'}
        }
        return applyRules(lineOfSightRules)
    }

    companion object {
        fun emptyRow(rowSize: Int) = listOf(CharArray(rowSize) { '.' }.joinToString(""))
    }
}

private fun IntArray.reset() {
    for (i in indices) {
        this[i] = 0
    }
}

fun main() {
    val testSeats = Seats(sample)
    testPasses.forEachIndexed { index, it ->
        testSeats.applyAdjacencyRules()
        assertEquals(it, testSeats.toString(), "comparing rules applied to test pass $index")
    }
    val sampleSeats = Seats(sample)
    sampleSeats.settle()
    assertEquals(37, sampleSeats.occupiedCount)

    val inputSeats = Seats(input)
    inputSeats.settle()
    assertEquals(2204, inputSeats.occupiedCount)

    val largeTestSeats = Seats(sample)
    largeTestPasses.forEachIndexed { index, it ->
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
