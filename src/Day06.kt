fun main() {
    fun posUniqueSequence(signal: String, count: Int): Int {
        val idx = signal.windowedSequence(count, 1).indexOfFirst { it.toSet().size == count }
        return idx + count
    }

    fun part1(signals: List<String>): List<Int> {
        return signals.map { signal -> posUniqueSequence(signal, 4) }
    }

    fun part2(signals: List<String>): List<Int> {
        return signals.map { signal -> posUniqueSequence(signal, 14) }
    }

    fun parse(stringList: List<String>): List<String> {
        return stringList
    }

    val testInput = readInput("Day06_test")
    check(part1(parse(testInput)) == listOf(5, 6, 10, 11))
    check(part2(parse(testInput)) == listOf(23, 23, 29, 26))

    val input = readInput("Day06_input")
    println(part1(parse(input)))
    println(part2(parse(input)))
}
