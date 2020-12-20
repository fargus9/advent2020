package advent2020.day17

import kotlin.test.assertEquals

typealias Plane = Array<CharArray>
typealias State = Map<Int, Array<CharArray>>

class Pocket(input: String) {
    private var state: State

    init {
        val initialState = input.lineSequence()
            .map { it.toCharArray() }
            .toList()
            .toTypedArray()
        state = mutableMapOf(0 to initialState)
    }

    private fun emptyState(width: Int, height: Int) = Plane(height) { CharArray(width) { '.' } }

    fun applyBootStep() {
        val width = state[0]!!.first().size + 2
        val height = state[0]!!.size + 2
        val lowest = state.asSequence().minOf { (key, _) -> key }
        val highest = state.asSequence().maxOf { (key, _) -> key }
        state = state.asSequence().associate { (z, plane)  ->
            z to plane.map { it.joinToString("", ".", ".").toCharArray() }
                .toMutableList()
                .apply { add(0, CharArray(width) { '.' }) ; add(CharArray(width) { '.' }) }
                .toTypedArray()
        }.toMutableMap().also {
            it[lowest - 1] = emptyState(width, height)
            it[highest + 1] = emptyState(width, height)
        }
        val nextState = state.asSequence().associate { (z, plane) ->
            z to plane.asSequence().map { it.copyOf() }.toList().toTypedArray()
        }
        nextState.asSequence().forEach { (z1, plane) ->
            for (y1 in plane.indices) {
                for (x1 in plane[y1].indices) {
                    var activeNeighbors = 0
                    for (z2 in z1 - 1..z1 + 1) {
                        if (z2 !in state.keys) continue
                        for (y2 in y1 - 1..y1 + 1) {
                            if (y2 !in state[z2]!!.indices) continue
                            for (x2 in x1 - 1..x1 + 1) {
                                if (x2 !in state[z2]!![y2].indices) continue
                                if (z2 != z1 || y2 != y1 || x2 != x1) {
                                    activeNeighbors += if (state[z2]?.let { it[y2][x2] } == '#') 1 else 0
                                }
                            }
                        }
                    }
                    when {
                        plane[y1][x1] == '#' && activeNeighbors !in 2..3 -> '.'
                        plane[y1][x1] == '.' && activeNeighbors == 3 -> '#'
                        else -> null
                    }?.let { plane[y1][x1] = it }
                }
            }
        }
        state = nextState
    }

    val activeCubes: Int
        get() = state.asSequence().sumOf { (_, state) -> state.sumOf { row -> row.count { it == '#' } } }
}

class FourDPocket(input: String) {
    private var state: Array<Array<Array<CharArray>>>

    init {
        val initialState = input.lineSequence()
            .map { it.toCharArray() }
            .toList()
            .toTypedArray()
        state = arrayOf(arrayOf(initialState))
    }

    fun applyBootStep() {
        val size = state[0][0][0].size + 2
        state = state.map { pocket ->
            pocket.map { plane ->
                plane.map {
                    it.joinToString("", ".", ".").toCharArray()
                }.toMutableList().also {
                    it.add(0, CharArray(size) { '.' })
                    it.add(CharArray(size) { '.' })
                }.toTypedArray()
            }.toMutableList().also {
                it.add(0, Array(size) { CharArray(size) { '.' } })
                it.add(Array(size) { CharArray(size) { '.' } })
            }.toTypedArray()
        }.toMutableList().also {
            it.add(0, Array(size) { Array(size) { CharArray(size) { '.' } } })
            it.add(Array(size) { Array(size) { CharArray(size) { '.' } } })
        }.toTypedArray()
        val nextState = state.asSequence()
            .map { w ->
                w.map { z -> z.map { y -> y.copyOf() }.toTypedArray() }.toTypedArray()
            }.toList()
            .toTypedArray()
        nextState.asSequence().forEachIndexed { w1, pocket ->
            for (z1 in pocket.indices) {
                val plane = pocket[z1]
                for (y1 in plane.indices) {
                    for (x1 in plane[y1].indices) {
                        var activeNeighbors = 0
                        for (w2 in w1 - 1..w1 + 1) {
                            if (w2 !in state.indices) continue
                            for (z2 in z1 - 1..z1 + 1) {
                                if (z2 !in state[w2].indices) continue
                                for (y2 in y1 - 1..y1 + 1) {
                                    if (y2 !in state[w2][z2].indices) continue
                                    for (x2 in x1 - 1..x1 + 1) {
                                        if (x2 !in state[w2][z2][y2].indices) continue
                                        if (w2 != w1 || z2 != z1 || y2 != y1 || x2 != x1) {
                                            activeNeighbors += if (state[w2][z2][y2][x2] == '#') 1 else 0
                                        }
                                    }
                                }
                            }
                        }
                        when {
                            plane[y1][x1] == '#' && activeNeighbors !in 2..3 -> '.'
                            plane[y1][x1] == '.' && activeNeighbors == 3 -> '#'
                            else -> null
                        }?.let { plane[y1][x1] = it }
                    }
                }
            }        }
        state = nextState
    }

    val activeCubes: Int
        get() = state.asSequence().sumOf { pocket -> pocket.sumOf { state -> state.sumOf { row -> row.count { it == '#' } } } }
}

fun main() {
    val samplePocket = Pocket(sampleInitial)
    repeat(6) {
        samplePocket.applyBootStep()
    }
    assertEquals(112, samplePocket.activeCubes)

    val pt1Pocket = Pocket(input)
    repeat(6) {
        pt1Pocket.applyBootStep()
    }
    assertEquals(223, pt1Pocket.activeCubes)

    val fourDSample = FourDPocket(sampleInitial)
    repeat(6) {
        fourDSample.applyBootStep()
    }
    assertEquals(848, fourDSample.activeCubes)

    val pt2Pocket = FourDPocket(input)
    repeat(6) {
        pt2Pocket.applyBootStep()
    }
    assertEquals(1884, pt2Pocket.activeCubes)
}
