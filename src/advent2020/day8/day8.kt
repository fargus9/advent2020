package advent2020.day8

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

const val sampleInputPt1 = """nop +0
acc +1
jmp +4
acc +3
jmp -3
acc -99
acc +1
jmp -4
acc +6"""

class Program(input: String) {
    private val backup = input.lineSequence().map { it.takeUnless { it == "jmp +0" } ?: "nop +0" }.toList()
    private var program = backup.toTypedArray()
    private val visited = Array(program.size) { 0 }
    private val rewritten = Array(program.size) { 0 }
    private var programCounter = 0
    private var lastJumpOrNop = -1
    var accumulator = 0

    fun executeUntilVisited(numberOfTimes: Int) {
        while (visited[programCounter] < numberOfTimes) {
            programCounter = executeLine()
        }
    }

    private fun String.rewriteInstruction(): String = with (split(" ")) {
        val (op, arg) = this
        return when (op) {
            "nop" -> if (arg.toInt() == 0) this@rewriteInstruction else "jmp $arg"
            else -> "nop $arg"
        }
    }

    fun executeAndCorrect() {
        while (programCounter <= program.lastIndex) {
            if (visited[programCounter] == 1) {
                program = backup.toTypedArray()
                program[lastJumpOrNop] = program[lastJumpOrNop].rewriteInstruction()
                rewritten[lastJumpOrNop] += 1
                for (i in visited.indices) { visited[i] = 0 }
                accumulator = 0
                programCounter = 0
            }
            programCounter = executeLine()
        }
    }

    private fun executeLine(): Int {
        var nextProgramCounter = programCounter + 1
        val (op, arg) = program[programCounter].split(" ")
        visited[programCounter] += 1
        when (op)  {
            "nop" -> if (rewritten[programCounter] == 0) lastJumpOrNop = programCounter
            "acc" -> accumulator += arg.toInt()
            "jmp" -> {
                nextProgramCounter = programCounter + arg.toInt()
                if (rewritten[programCounter] == 0) lastJumpOrNop = programCounter
            }
            else -> throw IllegalStateException("illegal op $op")
        }
        return nextProgramCounter
    }
}

fun main() {
    val sampleOutput = Program(sampleInputPt1).run { executeUntilVisited(1) ; accumulator }
    assertEquals(5, sampleOutput)

    val pt1Output = Program(input).run { executeUntilVisited(1) ; accumulator }
    assertEquals(1797, pt1Output)

    val sampleRewrite = Program(sampleInputPt1).run { executeAndCorrect() ; accumulator }
    assertEquals(8, sampleRewrite)

    val pt2Output = Program(input).run { executeAndCorrect() ; accumulator }
    assertEquals(1036, pt2Output)
}
