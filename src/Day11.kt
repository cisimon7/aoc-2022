fun main() {
    fun part1(monkeys: List<Monkey>): Long {
        val rounds = 20
        repeat(rounds) { _ ->
            monkeys.sortedBy { it.id }.forEach { monkey ->
                while (monkey.worryLevels.isNotEmpty()) {
                    val (item, id) = monkey.inspectNext { it / 3 }
                    monkeys.firstOrNull { it.id == id }?.apply { receive(item) }
                        ?: error("Monkey with $id id not found")
                }
            }
        }
        return monkeys.map { it.inspectCount }.sortedDescending().take(2).reduce { acc, elem -> acc * elem }
    }

    fun part2(monkeys: List<Monkey>): Long {
        val rounds = 10_000
        val stress = monkeys.map { it.testNumber }.reduce(Long::times)
        repeat(rounds) { _ ->
            monkeys.sortedBy { it.id }.forEach { monkey ->
                while (monkey.worryLevels.isNotEmpty()) {
                    val (item, id) = monkey.inspectNext { it % stress }
                    monkeys.firstOrNull { it.id == id }?.apply { receive(item) }
                        ?: error("Monkey with $id id not found")
                }
            }
        }
        return monkeys.map { it.inspectCount }.sortedDescending().take(2).reduce(Long::times)
    }

    fun parse(stringList: List<String>): List<Monkey> {
        return stringList.windowed(6, 7).map { line -> line.map { it.trim() } }.map { monkeyLines ->
            val id = monkeyLines[0].removeSuffix(":").split(" ").last().toInt()
            val worryLevels = monkeyLines[1].substring(monkeyLines[1].indexOfFirst { it == ':' } + 1)
                .split(", ")
                .map { it.trim().toLong() }
            val (arg1, op, arg2) = monkeyLines[2].split(" ").takeLast(3)
            val levelChange = { level: Long ->
                val operand1 = if (arg1 == "old") level else arg1.toLong()
                val operand2 = if (arg2 == "old") level else arg2.toLong()
                when (op) {
                    "*" -> operand1 * operand2
                    "+" -> operand1 + operand2
                    else -> error("Operation for $op not defined")
                }
            }
            val testNum = monkeyLines[3].split(" ").last().toLong()
            val testTrue = monkeyLines[4].split(" ").last().toInt()
            val testFalse = monkeyLines[5].split(" ").last().toInt()
            val next = { level: Long -> if (level % testNum == 0L) testTrue else testFalse }
            Monkey(id, worryLevels.toMutableList(), levelChange, next, testNum)
        }
    }

    val testInput = readInput("Day11_test")
    check(part1(parse(testInput)) == 10_605L)
    check(part2(parse(testInput)) == 2_713_310_158)

    val input = readInput("Day11_input")
    println(part1(parse(input)))
    println(part2(parse(input)))
}

class Monkey(
    val id: Int,
    var worryLevels: MutableList<Long>,
    private val levelChange: (Long) -> Long,
    val next: (Long) -> Int,
    val testNumber: Long
) {
    var inspectCount = 0L
    fun inspectNext(reliefFunc: (Long) -> Long): Pair<Long, Int> {
        val item = reliefFunc(levelChange(worryLevels.removeFirst()))
        inspectCount += 1
        return item to next(item)
    }

    fun receive(item: Long) {
        worryLevels.add(item)
    }
}