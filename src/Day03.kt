fun main() {

    fun Char.priority(): Int {
        return if (this.isLowerCase()) this.code - 96 else this.code - 64 + 26
    }

    fun part1(stringList: List<String>): Int {
        return stringList
            .map { rucksack -> rucksack.chunked(rucksack.length / 2) }
            .flatMap { (compartment1, compartment2) -> compartment1.toSet() intersect compartment2.toSet() }
            .sumOf { item -> item.priority() }
    }

    fun part2(stringList: List<String>): Int {
        return stringList.chunked(3)
            .map { Triple(it[0], it[1], it[2]) }
            .flatMap { (first, second, third) -> first.toSet() intersect second.toSet() intersect third.toSet() }
            .sumOf { item -> item.priority() }
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03_input")
    println(part1(input))
    println(part2(input))
}
