package advent2020.day5

import kotlin.test.assertEquals

const val testPass1 = "BFFFBBFRRR"
const val testPass2 = "FFFBBBFRRR"
const val testPass3 = "BBFFBBFRLL"

val IntRange.half
    get() = count() / 2
val IntRange.lowerHalf
    get() = first until first + half
val IntRange.upperHalf
    get() = (first + half).rangeTo(last)

typealias Directions = Sequence<Char>
tailrec fun findIndex(range: IntRange, directions: Directions, predicate: (Char) -> Boolean): Int {
    val newRange = if (predicate(directions.first())) range.lowerHalf else range.upperHalf
    return when (newRange.count()) {
        2 -> if (predicate(directions.last())) newRange.first else newRange.last
        1 -> range.first
        else -> findIndex(newRange, directions.drop(1), predicate)
    }
}

val rowsOfPlane = 0.rangeTo(127)
val seatsOfPlane = 0.rangeTo(7)
typealias Seat = Triple<Int, Int, Int>
fun Directions.findSeat(): Seat {
    val rowGroup = take(7)
    val row = findIndex(rowsOfPlane, rowGroup) { it == 'F' }
    val seatGroup = drop(7)
    val seat = findIndex(seatsOfPlane, seatGroup) { it == 'L' }
    return Seat(row, seat, row * 8 + seat);
}
fun Directions.findSeatId(): Int = findSeat().third

fun main() {
    assertEquals(0.rangeTo(63), 0.rangeTo(127).lowerHalf)
    assertEquals(32.rangeTo(63), 0.rangeTo(63).upperHalf)
    assertEquals(32.rangeTo(47), 32.rangeTo(63).lowerHalf)
    assertEquals(40.rangeTo(47), 32.rangeTo(47).upperHalf)
    assertEquals(44.rangeTo(47), 40.rangeTo(47).upperHalf)
    assertEquals(44.rangeTo(45), 44.rangeTo(47).lowerHalf)

    assertEquals(4.rangeTo(7), 0.rangeTo(7).upperHalf)
    assertEquals(4.rangeTo(5), 4.rangeTo(7).lowerHalf)

    with (testPass1.asSequence()) {
        val rowGroup = take(7)
        val row = findIndex(rowsOfPlane, rowGroup) { it == 'F' }
        assertEquals(70, row)
        val seatGroup = drop(7)
        val seat = findIndex(seatsOfPlane, seatGroup) { it == 'L' }
        assertEquals(7, seat)
        assertEquals(567, findSeatId())
    }

    with (testPass2.asSequence()) {
        val rowGroup = take(7)
        val row = findIndex(rowsOfPlane, rowGroup) { it == 'F' }
        assertEquals(14, row)
        val seatGroup = drop(7)
        val seat = findIndex(seatsOfPlane, seatGroup) { it == 'L' }
        assertEquals(7, seat)
        assertEquals(119, findSeatId())
    }

    with (testPass3.asSequence()) {
        val rowGroup = take(7)
        val row = findIndex(rowsOfPlane, rowGroup) { it == 'F' }
        assertEquals(102, row)
        val seatGroup = drop(7)
        val seat = findIndex(seatsOfPlane, seatGroup) { it == 'L' }
        assertEquals(4, seat)
        assertEquals(820, findSeatId())
    }

    val testOutput = sequenceOf(testPass1, testPass2, testPass3).map { it.asSequence().findSeatId() }.max()
    assertEquals(820, testOutput)

    val pt1 = input.splitToSequence("\n").map { it.asSequence().findSeatId() }.max()
    println(pt1)

    val seatIds = Array(128) { arrayOfNulls<Int>(8) }
    input.splitToSequence("\n").map { it.asSequence().findSeat() }.forEach { (row, seat, id) ->
        seatIds[row][seat] = id;
    }

    val projectedRow = seatIds.indexOfFirst { seats -> seats.count { it == null } == 1 }
    val projectedSeat = seatIds[projectedRow].indexOf(null)
    val projectedSeatId = projectedRow * 8 + projectedSeat
    println("row: $projectedRow seat: $projectedSeat id: $projectedSeatId")
}
