fun main() {
    fun part1(elves: List<List<Int>>): Pair<Int, Long> {
        val maxElf = elves.maxBy { it.sum() }
        val idx = elves.indexOf(maxElf) + 1
        return idx to maxElf.sum().toLong()
    }

    fun part2(elves: List<List<Int>>): Int {
        return elves.map { it.sum() }.sortedDescending().take(3).sum()
    }

    fun parse(stringList: List<String>): List<List<Int>> {
        var input = stringList
        return generateSequence {
            val items = input.takeWhile { it.isNotBlank() }.map { it.toInt() }
            input = input.drop(items.size + 1)
            items.ifEmpty { null }
        }.toList()
    }

    val testInput = readInput("Day01_test")
    check(part1(parse(testInput)) == (4 to 24_000L))
    check(part2(parse(testInput)) == 45_000)

    val input = readInput("Day01_input")
    println(part1(parse(input)))
    println(part2(parse(input)))
}
