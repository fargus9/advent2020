package advent2020.day14

import advent2020.day6.sample
import kotlin.test.assertEquals

// this isn't valid for the problem set in a variety of ways
fun String.toMask() = map { it.toInt() }.fold(0) { total, value -> (total shl 1) or (value and 0b1) }

class DockingProgram(input: String) {
    private val program: Sequence<String> = input.lineSequence()
    var masks = LongArray(2)
    var memory = mutableMapOf<Int, Long>().withDefault { 0 }

    fun execute() = program.forEach {
        "^mask = ([01X]+)$".toRegex().find(it)?.destructured?.let { (value) ->
            masks = value.asSequence().fold(LongArray(2)) { masks, bit ->
                when (bit) {
                    'X' -> {
                        masks[0] = (masks[0] shl 1) or 0b1
                        masks[1] = masks[1] shl 1
                    }
                    '0' -> {
                        masks[0] = masks[0] shl 1
                        masks[1] = masks[1] shl 1
                    }
                    '1' -> {
                        masks[0] = (masks[0] shl 1) or 0b1
                        masks[1] = (masks[1] shl 1) or 0b1
                    }
                }
                masks
            }
        }
        "^mem\\[([0-9]+)] = ([0-9]+)$".toRegex().find(it)?.destructured?.let { (idx, value) ->
            memory[idx.toInt()] = (value.toLong() and masks[0]) or masks[1]
        }
    }

    fun sumOfValuesInMemory() = memory.asSequence().sumOf { (_, value) -> value }
}

fun main() {
    val samplePt1 = DockingProgram(sample1)
    samplePt1.execute()
    assertEquals(165, samplePt1.sumOfValuesInMemory())

    val pt1Program = DockingProgram(input)
    pt1Program.execute()
    println(pt1Program.sumOfValuesInMemory())
}
