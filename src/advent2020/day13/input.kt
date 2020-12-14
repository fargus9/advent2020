package advent2020.day13

const val samplePt1 = """939
7,13,x,x,59,x,31,19"""

const val input = """1008832
23,x,x,x,x,x,x,x,x,x,x,x,x,41,x,x,x,x,x,x,x,x,x,449,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,13,19,x,x,x,x,x,x,x,x,x,29,x,991,x,x,x,x,x,37,x,x,x,x,x,x,x,x,x,x,17"""

val tests = arrayOf("67,7,59,61" to 754018L,
    "67,x,7,59,61" to 779210L,
    "67,7,x,59,61" to 1261476L,
    "1789,37,47,1889" to 1202161486L)
