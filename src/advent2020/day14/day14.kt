package advent2020.day14

import kotlin.test.assertEquals

sealed class Program(input: String) {
    protected val program: Sequence<String> = input.lineSequence()
    protected var memory = mutableMapOf<Long, Long>().withDefault { 0 }

    fun String.toMask(): String? = "^mask = ([01X]+)$".toRegex().find(this)?.destructured?.let { (value) -> value }

    fun String.toMemAccess(): Pair<Long, Long>? = "^mem\\[([0-9]+)] = ([0-9]+)$".toRegex().find(this)?.destructured
        ?.let { (address, value) -> address.toLong() to value.toLong() }

    fun sumOfValuesInMemory() = memory.asSequence().sumOf { (_, value) -> value }
}

class DockingProgram(input: String): Program(input) {
    private var masks = LongArray(2)

    fun execute() = program.forEach {
        it.toMask()?.let { value ->
            masks = value.asSequence().fold(LongArray(2)) { masks, bit ->
                when (bit) {
                    'X' -> {
                        masks[0] = (masks[0] shl 1) or 0b1L
                        masks[1] = masks[1] shl 1
                    }
                    '0' -> {
                        masks[0] = masks[0] shl 1
                        masks[1] = masks[1] shl 1
                    }
                    '1' -> {
                        masks[0] = (masks[0] shl 1) or 0b1L
                        masks[1] = (masks[1] shl 1) or 0b1L
                    }
                }
                masks
            }
        }
        it.toMemAccess()?.let { (address, value) ->
            memory[address] = (value and masks[0]) or masks[1]
        }
    }
}

class DockingAddressProgram(input: String): Program(input) {
    private lateinit var mask: String

    fun execute() = program.forEach {
        it.toMask()?.let { value -> mask = value }
        it.toMemAccess()?.let { (address, value) -> storeValueUsingMask(0, address, value) }
    }

    private fun Long.flipBitToZero(shift: Int) = this and (0b1L shl shift).inv()

    private fun Long.flipBitToOne(shift: Int) = this or (0b1L shl shift)

    private fun storeValueUsingMask(mostSignificantBit: Int, address: Long, value: Long) {
        if (mostSignificantBit > mask.lastIndex) {
            memory[address] = value
            return
        }
        val nextSignificantBit = mostSignificantBit + 1
        val shift = mask.lastIndex - mostSignificantBit
        val newAddresses = when (mask[mostSignificantBit]) {
            'X' -> arrayOf(address.flipBitToOne(shift), address.flipBitToZero(shift))
            '0' -> arrayOf(address)
            '1' -> arrayOf(address.flipBitToOne(shift))
            else -> throw IllegalStateException("Expected valid mask character")
        }
        newAddresses.forEach { storeValueUsingMask(nextSignificantBit, it, value) }
    }
}

fun main() {
    val samplePt1 = DockingProgram(sample1)
    samplePt1.execute()
    assertEquals(165, samplePt1.sumOfValuesInMemory())

    val pt1Program = DockingProgram(input)
    pt1Program.execute()
    assertEquals(7477696999511, pt1Program.sumOfValuesInMemory())

    val samplePt2 = DockingAddressProgram(sample2)
    samplePt2.execute()
    assertEquals(208, samplePt2.sumOfValuesInMemory())

    val pt2Program = DockingAddressProgram(input)
    pt2Program.execute()
    assertEquals(3687727854171, pt2Program.sumOfValuesInMemory())
}
