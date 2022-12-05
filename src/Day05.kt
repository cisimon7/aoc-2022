fun main() {
    fun part1(pair: Pair<MutableList<MutableList<Char>>, List<Triple<Int, Int, Int>>>): String {
        var (stacks, instructions) = pair
        instructions.forEach { (count, from, to) ->
            val items = stacks[from - 1].takeLast(count)
            stacks[to - 1].addAll(items.reversed())
            stacks[from - 1] = stacks[from - 1].dropLast(count).toMutableList()
        }
        return stacks.map { it.lastOrNull() ?: ' ' }.joinToString("")
    }

    fun part2(pair: Pair<MutableList<MutableList<Char>>, List<Triple<Int, Int, Int>>>): String {
        var (stacks, instructions) = pair
        instructions.forEach { (count, from, to) ->
            val items = stacks[from - 1].takeLast(count)
            stacks[to - 1].addAll(items)
            stacks[from - 1] = stacks[from - 1].dropLast(count).toMutableList()
        }
        return stacks.map { it.lastOrNull() ?: ' ' }.joinToString("")
    }

    fun parse(stringList: List<String>): Pair<MutableList<MutableList<Char>>, List<Triple<Int, Int, Int>>> {
        val height = stringList.takeWhile { it.isNotBlank() }.size
        val (order, instructions) = stringList.take(height) to stringList.drop(height + 1)
        var crates = order.dropLast(1).map { crate ->
            crate.windowed(3, 4).map { it[1] }
        }

        val width = crates.maxBy { it.size }.size
        crates = crates.map {
            if (it.size != width)
                it.also {
                    (0..it.size - width + 1).forEach { _ ->
                        (it as MutableList<Char>).add(' ')
                    }
                }
            else it
        }

        return crates.transpose().map { it.filter { ch -> ch.isLetter() }.reversed().toMutableList() }
            .toMutableList() to
                instructions.map { line ->
                    val instruction = line.split(" ")
                    Triple(instruction[1].toInt(), instruction[3].toInt(), instruction[5].toInt())
                }
    }

    val testInput = readInput("Day05_test")
    check(part1(parse(testInput)) == "CMZ")
    check(part2(parse(testInput)) == "MCD")

    val input = readInput("Day05_input")
    println(part1(parse(input)))
    println(part2(parse(input)))
}


