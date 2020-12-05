package advent2020.day4

import kotlin.test.assertEquals

const val exampleText = """ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
byr:1937 iyr:2017 cid:147 hgt:183cm

iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884
hcl:#cfa07d byr:1929

hcl:#ae17e1 iyr:2013
eyr:2024
ecl:brn pid:760753108 byr:1931
hgt:179cm

hcl:#cfa07d eyr:2025 pid:166559648
iyr:2011 ecl:brn hgt:59in"""

const val invalidInput = """eyr:1972 cid:100
hcl:#18171d ecl:amb hgt:170 pid:186cm iyr:2018 byr:1926

iyr:2019
hcl:#602927 eyr:1967 hgt:170cm
ecl:grn pid:012533040 byr:1946

hcl:dab227 iyr:2012
ecl:brn hgt:182cm pid:021572410 eyr:2020 byr:1992 cid:277

hgt:59cm ecl:zzz
eyr:2038 hcl:74454a iyr:2023
pid:3556412378 byr:2007"""

const val validInput = """pid:087499704 hgt:74in ecl:grn iyr:2012 eyr:2030 byr:1980
hcl:#623a2f

eyr:2029 ecl:blu cid:129 byr:1989
iyr:2014 pid:896056539 hcl:#a97842 hgt:165cm

hcl:#888785
hgt:164cm byr:2001 iyr:2015 cid:88
pid:545766238 ecl:hzl
eyr:2022

iyr:2010 hgt:158cm hcl:#b6652a ecl:blu byr:1944 eyr:2021 pid:093154719"""

val validFields = setOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid", "cid",)
val requiredFields = validFields.minus("cid")

typealias PassportData = Map<String, String>
fun String.countValidPassports(validationPredicate: (PassportData) -> Boolean) = splitToSequence("\n")
    .fold(mutableListOf(mutableMapOf<String, String>())) { list, input ->
        if (input.isEmpty()) {
            list.add(mutableMapOf())
        } else {
            input.splitToSequence(" ")
                .associateTo(list.last()) { val (key, value) = it.split(":") ; key to value }
        }
        list
    }.count(validationPredicate)

fun PassportData.validateKeys() = keys == validFields || keys == requiredFields

fun PassportData.validateFields(): Boolean = all { (field, data) ->
    when (field) {
        "byr" -> 1920.rangeTo(2002).contains(data.toInt())
        "iyr" -> 2010.rangeTo(2020).contains(data.toInt())
        "eyr" -> 2020.rangeTo(2030).contains(data.toInt())
        "hgt" -> {
            when (data.takeLast(2)) {
                "cm" -> 150.rangeTo(193).contains(data.dropLast(2).toInt())
                "in" -> 59.rangeTo(76).contains(data.dropLast(2).toInt())
                else -> false
            }
        }
        "hcl" -> data.matches("^#[0-9a-f]{6}+$".toRegex())
        "ecl" -> data.matches("^(amb|blu|brn|gry|grn|hzl|oth)$".toRegex())
        "pid" -> data.matches("^[0-9]{9}+$".toRegex())
        "cid" -> true
        else -> false
    }
}

fun main() {
    println("processing sample")
    val sampleOutput = exampleText.countValidPassports { it.validateKeys() }
    println(sampleOutput)

    println("processing pt1")
    val pt1Output = input.countValidPassports { it.validateKeys() }
    println(pt1Output)

    println("processing invalid input")
    val invalidOutput = invalidInput.countValidPassports { it.validateKeys() && it.validateFields() }
    assertEquals(0, invalidOutput)

    println("processing valid input")
    val validOutput = validInput.countValidPassports { it.validateKeys() && it.validateFields() }
    assertEquals(4, validOutput)

    println("processing pt2")
    val pt2Output = input.countValidPassports { it.validateKeys() && it.validateFields() }
    println(pt2Output)
}
