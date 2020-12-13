package advent2020.day12

import kotlin.math.abs
import kotlin.test.assertEquals

typealias Heading = Pair<Int, Int>

sealed class Program(input: String) {
    private val program = input.lineSequence()
    protected var position = IntArray(2)

    fun calculateManhattanDistance(): Int = position.sumOf { abs(it) }

    private fun followInstruction(op: Char, arg: Int) {
        when (op) {
            'N' -> north(arg)
            'S' -> south(arg)
            'W' -> west(arg)
            'E' -> east(arg)
            'L' -> left(arg)
            'R' -> right(arg)
            'F' -> forward(arg)
        }
    }

    abstract fun north(arg: Int)
    abstract fun south(arg: Int)
    abstract fun west(arg: Int)
    abstract fun east(arg: Int)
    abstract fun left(arg: Int)
    abstract fun right(arg: Int)
    abstract fun forward(arg: Int)

    private val String.arg: Int
        get() = drop(1).toInt()

    fun navigate() = program.forEach { followInstruction(it[0], it.arg) }

    companion object {
        const val X_AXIS = 0
        const val Y_AXIS = 1
    }
}

class HeadingProgram(input: String): Program(input) {
    private var heading = X_AXIS to 1

    private fun Heading.turnLeft(): Heading {
        val axis = first xor Y_AXIS
        val dir = if (axis == Y_AXIS && first == X_AXIS) second * -1 else second
        return axis to dir
    }

    private fun Heading.turnRight(): Heading {
        val axis = first xor Y_AXIS
        val dir = if (axis == X_AXIS && first == Y_AXIS) second * -1 else second
        return axis to dir
    }

    override fun north(arg: Int) { position[Y_AXIS] -= arg }
    override fun south(arg: Int) { position[Y_AXIS] += arg }
    override fun west(arg: Int) { position[X_AXIS] -= arg }
    override fun east(arg: Int) { position[X_AXIS] += arg }
    override fun left(arg: Int) = repeat(arg / 90) { heading = heading.turnLeft() }
    override fun right(arg: Int) = repeat(arg / 90) { heading = heading.turnRight() }
    override fun forward(arg: Int) { position[heading.first] += heading.second * arg }
}

typealias WayPoint = IntArray
class WaypointProgram(input: String): Program(input) {
    private val wayPoint = WayPoint(2)

    init {
        east(10)
        north(1)
    }

    override fun north(arg: Int) { wayPoint[Y_AXIS] -= arg }

    override fun south(arg: Int) { wayPoint[Y_AXIS] += arg }

    override fun west(arg: Int) { wayPoint[X_AXIS] -= arg }

    override fun east(arg: Int) { wayPoint[X_AXIS] += arg }

    private fun WayPoint.turnLeft() {
        val tmp = this[X_AXIS]
        this[X_AXIS] = this[Y_AXIS]
        this[Y_AXIS] = tmp * -1
    }

    private fun WayPoint.turnRight() {
        val tmp = this[X_AXIS]
        this[X_AXIS] = this[Y_AXIS] * -1
        this[Y_AXIS] = tmp
    }

    override fun left(arg: Int) = repeat(arg / 90) { wayPoint.turnLeft() }

    override fun right(arg: Int) = repeat(arg / 90) { wayPoint.turnRight() }

    override fun forward(arg: Int) {
        position[X_AXIS] += arg * wayPoint[X_AXIS]
        position[Y_AXIS] += arg * wayPoint[Y_AXIS]
    }
}

fun main() {
    val pt1SampleProgram = HeadingProgram(pt1Sample)
    pt1SampleProgram.navigate()
    assertEquals(25, pt1SampleProgram.calculateManhattanDistance())

    val pt1Program = HeadingProgram(input)
    pt1Program.navigate()
    assertEquals(2270, pt1Program.calculateManhattanDistance())

    val pt2SampleProgram = WaypointProgram(pt1Sample)
    pt2SampleProgram.navigate()
    assertEquals(286, pt2SampleProgram.calculateManhattanDistance())

    val pt2Program = WaypointProgram(input)
    pt2Program.navigate()
    assertEquals(138669, pt2Program.calculateManhattanDistance())
}
