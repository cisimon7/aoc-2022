fun main() {
    fun part1(pairs: List<Pair<IntRange, IntRange>>): Int {
        return pairs.count { (section1, section2) ->
            val inter = section1 intersect section2
            inter.containsAll(section1.toSet()) || inter.containsAll(section2.toSet())
        }
    }

    fun part2(pairs: List<Pair<IntRange, IntRange>>): Int {
        return pairs.count { (section1, section2) ->
            (section1 intersect section2).isNotEmpty()
        }
    }

    fun parse(stringList: List<String>): List<Pair<IntRange, IntRange>> {
        return stringList.map { pair ->
            pair.split(",")
                .map { section -> section.split("-") }
                .map { section -> section[0].toInt()..section[1].toInt() }
        }.map { pair -> Pair(pair[0], pair[1]) }
    }

    val testInput = readInput("Day04_test")
    check(part1(parse(testInput)) == 2)
    check(part2(parse(testInput)) == 4)

    val input = readInput("Day04_input")
    println(part1(parse(input)))
    println(part2(parse(input)))
}
