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

typealias OccupancyRules = Seats.(Char, Int) -> Char?
class Seats(input: String) {
    private var nextState: CharArray
    private var seats: CharArray
    private var rowSize: Int = input.indexOfFirst { it == '\n' } + 2
    private val surrounding = arrayOf(-1, 1, -(rowSize + 1), -rowSize, -(rowSize -1), rowSize + 1, rowSize, rowSize - 1)
    private val occupiedSeatsInSight: IntArray

    init {
        val padded = emptyRow(rowSize) + input.lineSequence().map { ".$it." }.toList() + emptyRow(rowSize)
        seats = padded.joinToString("").toCharArray()
        nextState = seats.copyOf()
        occupiedSeatsInSight = IntArray(seats.size)
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
            '#' -> if (occupiedLineOfSightTo(considering) > 4) 'L' else null
            else -> null
        }
    }

    private fun occupiedLineOfSightTo(seat: Int) = occupiedSeatsInSight[seat]

    fun applyLineOfSightRules(): Boolean {
        var considering = seats.indexOfFirst { it != '.' }
        val seatRange = seats.indices
        while (considering in seatRange) {
            var sighted = 0
            // could potentially short-circuit each search by looking for || (occupiedSeatsInSight[<value>] == 8
            // && seats[considering] == 'L') - not a big win I think
            for (i in (considering - rowSize).downTo(considering % rowSize) step rowSize) {
                if (seats[i] == '#') {
                    sighted += 1
                    break
                }
            }
            for (i in (considering + rowSize).until(seats.size - considering % rowSize) step rowSize) {
                if (seats[i] == '#') {
                    sighted += 1
                    break
                }
            }
            for (i in 1..(considering % rowSize)) {
                if (seats[considering - i] == '#') {
                    sighted += 1
                    break
                }
            }
            for (i in 1.until(rowSize - considering % rowSize)) {
                if (seats[considering + i] == '#') {
                    sighted += 1
                    break
                }
            }
            for (i in (considering - rowSize - 1).downTo(0) step rowSize + 1) {
                if (seats[i] == '#') {
                    sighted += 1
                    break
                }
            }
            for (i in (considering + rowSize + 1).until(seats.lastIndex) step rowSize + 1) {
                if (seats[i] == '#') {
                    sighted += 1
                    break
                }
            }
            // bounds are WRONG
            for (i in (rowSize - 1)..( rowSize - (considering / rowSize)) step rowSize - 1) {
                if (seats[considering + i] == '#') {
                    sighted += 1
                    break
                }
            }
            for (i in (considering - rowSize + 1).downTo(rowSize - 1) step rowSize + 1) {
                if (seats[i] == '#') {
                    sighted += 1
                    break
                }
            }
            occupiedSeatsInSight[considering] = sighted
            considering = seats.indexOfFirstFrom(considering + 1) { it != '.' }
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
