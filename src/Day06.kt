fun main() {
    fun posUniqueSequence(signal: String, count: Int): Int {
        val groups = signal.windowedSequence(count, 1)
        val idx = groups.indexOfFirst { it.toSet().size == count }
        val substring = signal.substring(0, idx)
        val overlap = signal
            .substring(idx, idx + count).scan("") { acc, ch -> acc + ch }
            .drop(1).indexOfFirst { seg ->
                signal.substring(0, idx).endsWith(seg)
            }.let { if (it == -1) 0 else it }
        return substring.length + count - overlap
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
