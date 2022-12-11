import kotlin.math.abs
import kotlin.math.sign

fun main() {
    fun part1(commands: List<Pair<Direction, Int>>): Int {
        val head = Knot.Head()
        val tail = Knot.Tail()
        commands.forEach { command ->
            val (direction, count) = command
            repeat(count) {
                head.step(direction)
                tail.step(head)
            }
        }
        return tail.visitedStates.toSet().count()
    }

    fun part2(commands: List<Pair<Direction, Int>>): Int {
        val head = Knot.Head()
        val tails = List(9) { Knot.Tail() }
        commands.forEach { command ->
            val (direction, count) = command
            repeat(count) {
                head.step(direction)
                tails.forEachIndexed { idx, tail ->
                    if (idx == 0) tail.step(head) else
                        tail.step(tails[idx - 1])
                }
            }
        }
        return tails.last().visitedStates.toSet().count()
    }

    fun parse(stringList: List<String>): List<Pair<Direction, Int>> {
        return stringList.map {
            val (dir, count) = it.split(" ").take(2)
            when (dir) {
                "R" -> Direction.RIGHT to count.toInt()
                "U" -> Direction.UP to count.toInt()
                "L" -> Direction.LEFT to count.toInt()
                "D" -> Direction.DOWN to count.toInt()
                else -> error("Unknown direction")
            }
        }
    }

    val testInput = readInput("Day09_test")
    val testInput2 = readInput("Day09_test2")
    check(part1(parse(testInput)) == 13)
    check(part2(parse(testInput2)) == 36)

    val input = readInput("Day09_input")
    println(part1(parse(input)))
    println(part2(parse(input)))
}

sealed class Knot {
    open var x: Int = 0
    open var y: Int = 0

    data class Head(override var x: Int = 0, override var y: Int = 0) : Knot() {

        fun step(direction: Direction) {
            when (direction) {
                Direction.UP -> this.y += 1
                Direction.RIGHT -> this.x += 1
                Direction.LEFT -> this.x -= 1
                Direction.DOWN -> this.y -= 1
            }
        }
    }

    data class Tail(override var x: Int = 0, override var y: Int = 0) : Knot() {
        val visitedStates = mutableListOf<Pair<Int, Int>>()
        fun step(head: Knot) {
            val (dx, dy) = (head.x - x) to (head.y - y)
            when (abs(dx) to abs(dy)) {
                (2 to 0) -> x += 1 * sign(dx.toDouble()).toInt()
                (0 to 2) -> y += 1 * sign(dy.toDouble()).toInt()
                (1 to 2) -> {
                    x += 1 * sign(dx.toDouble()).toInt()
                    y += 1 * sign(dy.toDouble()).toInt()
                }

                (2 to 1) -> {
                    x += 1 * sign(dx.toDouble()).toInt()
                    y += 1 * sign(dy.toDouble()).toInt()
                }

                (2 to 2) -> {
                    x += 1 * sign(dx.toDouble()).toInt()
                    y += 1 * sign(dy.toDouble()).toInt()
                }
            }
            visitedStates.add(x to y)
        }
    }
}

enum class Direction {
    UP, RIGHT, LEFT, DOWN
}
