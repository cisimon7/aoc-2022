fun main() {
    fun part1(trees: List<List<Int>>): Int {
        val treesVisibility = with(trees) {
            mapIndexed { rIdx, row ->
                row.mapIndexed { cIdx, height ->
                    when {
                        rIdx == 0 || cIdx == size - 1 -> true
                        rIdx == row.size - 1 || cIdx == 0 -> true
                        else -> {
                            (0 until cIdx).all { i -> row[i] < height } ||
                                    (cIdx + 1 until row.size).all { i -> row[i] < height } ||
                                    (0 until rIdx).all { i -> this[i][cIdx] < height } ||
                                    (rIdx + 1 until size).all { i -> this[i][cIdx] < height }
                        }
                    }
                }
            }
        }
        return treesVisibility.flatten().count { it }
    }

    fun part2(trees: List<List<Int>>): Int {
        val scenicScores = with(trees) {
            mapIndexed { rIdx, row ->
                row.mapIndexed { cIdx, height ->
                    val left = (0 until cIdx).reversed().takeUntil { i -> height > row[i] }.count()
                    val right = (cIdx + 1 until row.size).takeUntil { i -> height > row[i] }.count()
                    val top = (0 until rIdx).reversed().takeUntil { i -> height > this[i][cIdx] }.count()
                    val bottom = (rIdx + 1 until size).takeUntil { i -> height > this[i][cIdx] }.count()
                    left * right * top * bottom
                }
            }
        }
        return scenicScores.flatten().maxOf { it }
    }

    fun parse(stringList: List<String>): List<List<Int>> {
        return stringList.map { line -> line.split("").drop(1).dropLast(1).map { it.toInt() } }
    }

    val testInput = readInput("Day08_test")
    check(part1(parse(testInput)) == 21)
    check(part2(parse(testInput)) == 8)

    val input = readInput("Day08_input")
    println(part1(parse(input)))
    println(part2(parse(input)))
}