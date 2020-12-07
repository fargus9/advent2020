package advent2020.day7

import kotlin.test.assertEquals

const val sampleInput = """light red bags contain 1 bright white bag, 2 muted yellow bags.
dark orange bags contain 3 bright white bags, 4 muted yellow bags.
bright white bags contain 1 shiny gold bag.
muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
dark olive bags contain 3 faded blue bags, 4 dotted black bags.
vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
faded blue bags contain no other bags.
dotted black bags contain no other bags."""

const val sampleInput2 = """shiny gold bags contain 2 dark red bags.
dark red bags contain 2 dark orange bags.
dark orange bags contain 2 dark yellow bags.
dark yellow bags contain 2 dark green bags.
dark green bags contain 2 dark blue bags.
dark blue bags contain 2 dark violet bags.
dark violet bags contain no other bags."""

typealias NestingBagRules = Map<String, Array<String?>>
fun String.collectNestingBagRules(): NestingBagRules = splitToSequence(".\n").associate {
    val (outer, contents) = it.split(" bags contain ")
    val bagCounts = contents.replace(".", "").split(", ")
    val bagColors = bagCounts.map { count -> count.replace("(^[0-9]+ | bag[s]?$)".toRegex(), "") }
    outer to bagColors.map { color -> color.takeUnless { match -> match.startsWith("no other") } }.toTypedArray()
}

fun findBagsThatMayContain(rules: NestingBagRules, colors: Array<String>): Sequence<String> {
    if (rules.isEmpty() || colors.isEmpty()) return emptySequence()
    val (containedBy, notContainedBy) = rules.asSequence()
        .partition { (_, contents) -> contents.intersect(colors.asIterable()).isNotEmpty() }
    val containingColors = containedBy.asSequence().map { (key, _) -> key }
    return containingColors +
            findBagsThatMayContain(notContainedBy.associate { (key, value) -> key to value }, containingColors.toList().toTypedArray())
}

typealias BagCount = Map<String, Int>
typealias CountingBagRules = Map<String, BagCount>
fun String.collectCountingBagRules(): CountingBagRules = splitToSequence("\n").associate { line ->
    val (outer, contents) = line.split(" bags contain ")
    val bagCounts = contents.replace(".", "").split(", ")
        .map { count -> count.replace(" bag[s]?$".toRegex(), "") }
        .associate { carry ->
            val (count, color) = carry.split(" ", limit = 2)
            color to (count.takeUnless { it.startsWith("no") }?.toInt() ?: 0)
        }
    outer to bagCounts
}

fun findBagRequirementsFor(rules: CountingBagRules, color: String): Int {
    return rules[color]?.asSequence()?.sumBy { ( key, value) -> value + findBagRequirementsFor(rules, key) * value } ?: 0
}

fun main() {
    val sampleRules = sampleInput.collectNestingBagRules()
    val sampleOutput = findBagsThatMayContain(sampleRules, arrayOf("shiny gold")).count()
    assertEquals(4, sampleOutput)

    val pt1Rules = input.collectNestingBagRules()
    val pt1Output = findBagsThatMayContain(pt1Rules, arrayOf("shiny gold")).count()
    println(pt1Output)

    val sampleCountingRules = sampleInput.collectCountingBagRules()
    val sampleCountingOutput = findBagRequirementsFor(sampleCountingRules, "shiny gold")
    assertEquals(32, sampleCountingOutput)

    val sampleCountRules2 = sampleInput2.collectCountingBagRules()
    val sampleCountingOutput2 = findBagRequirementsFor(sampleCountRules2, "shiny gold")
    assertEquals(126, sampleCountingOutput2)

    val pt2Rules = input.collectCountingBagRules()
    val p2Count = findBagRequirementsFor(pt2Rules, "shiny gold")
    assertEquals(41559, p2Count)
}
