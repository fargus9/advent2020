package advent2020.day18

import kotlin.test.assertEquals

typealias Operator = (Long, Long) -> Long
val OPS = mapOf<String, Operator>("*" to { left, right -> left * right }, "+" to { left, right -> left + right })

fun String.eval(): Long = replace(" ", "").run {
    val valueStack = mutableListOf<Long>()
    val operatorStack = mutableListOf<Operator>()
    var offset = 0
    while (offset in indices || valueStack.size > 1) {
        if (valueStack.size == 2 && operatorStack.size > 0) {
            val right = valueStack.removeLast()
            val left = valueStack.removeLast()
            val op = operatorStack.removeLast()
            valueStack.add(op(left, right))
            continue
        }
        val operand = drop(offset).takeWhile { it in '0'..'9' }
        if (operand.isNotEmpty()) {
            valueStack.add(operand.toLong())
            offset += operand.length
            continue
        }

        val operator = drop(offset).takeWhile { it == '+' || it == '*' }
        if (operator.isNotEmpty()) {
            operatorStack.add(OPS[operator]!!)
            offset += 1
            continue
        }

        if (this[offset] == '(') {
            var ignoreParens = 0
            var lookAhead = offset + 1
            while (this[lookAhead] != ')' || ignoreParens != 0) {
                if (this[lookAhead] == '(') ignoreParens += 1
                if (this[lookAhead] == ')') ignoreParens -= 1
                lookAhead += 1
            }
            valueStack.add(substring(offset + 1, lookAhead).eval())
            offset = lookAhead + 1
        }
    }

    return valueStack.first()
}

fun String.eval2(): Long = replace(" ", "").run {
    val valueStack = mutableListOf<Long>()
    val operatorStack = mutableListOf<Operator>()
    var offset = 0
    while (offset in indices) {
        if (valueStack.size >= 2 && operatorStack.size > 0 && operatorStack.last() == OPS["+"]) {
            val right = valueStack.removeLast()
            val left = valueStack.removeLast()
            val op = operatorStack.removeLast()
            valueStack.add(op(left, right))
            continue
        }
        val operand = drop(offset).takeWhile { it in '0'..'9' }
        if (operand.isNotEmpty()) {
            valueStack.add(operand.toLong())
            offset += operand.length
            continue
        }

        val operator = drop(offset).takeWhile { it == '+' || it == '*' }
        if (operator == "*") {
            operatorStack.add(OPS[operator]!!)
            offset += 1
            continue
        } else if (operator.isNotEmpty()) {
            val right = drop(offset + 1).takeWhile { it in '0'..'9' }
            if (right.isNotEmpty()) {
                val left = valueStack.removeLast()
                valueStack.add(OPS["+"]!!(left, right.toLong()))
                offset += right.length + 1
                continue
            } else {
                operatorStack.add(OPS["+"]!!)
                offset += 1
            }
        }

        if (this[offset] == '(') {
            var ignoreParens = 0
            var lookAhead = offset + 1
            while (this[lookAhead] != ')' || ignoreParens != 0) {
                if (this[lookAhead] == '(') ignoreParens += 1
                if (this[lookAhead] == ')') ignoreParens -= 1
                lookAhead += 1
            }
            valueStack.add(substring(offset + 1, lookAhead).eval2())
            offset = lookAhead + 1
        }
    }
    while (valueStack.size > 1) {
        val right = valueStack.removeLast()
        val left = valueStack.removeLast()
        val op = operatorStack.removeLast()
        valueStack.add(op(left, right))
    }
    return valueStack.first()
}

fun main() {
    assertEquals(26, "2 * 3 + (4 * 5)".eval())
    assertEquals(437, "5 + (8 * 3 + 9 + 3 * 4 * 3)".eval())
    assertEquals(12240, "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))".eval())
    assertEquals(13632, "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2".eval())

    val pt1Output = input.lineSequence().sumOf { it.eval() }
    assertEquals(4297397455886L, pt1Output)

    assertEquals(51, "1 + (2 * 3) + (4 * (5 + 6))".eval2())
    assertEquals(46, "2 * 3 + (4 * 5)".eval2())
    assertEquals(1445, "5 + (8 * 3 + 9 + 3 * 4 * 3)".eval2())
    assertEquals(669060, "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))".eval2())
    assertEquals(23340, "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2".eval2())

    val pt2Output = input.lineSequence().sumOf { it.eval2() }
    println(pt2Output)
}
