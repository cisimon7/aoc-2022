import kotlin.properties.Delegates

fun main() {
    fun part1(instructions: List<Instruction>): Int {
        val register = Register()
        instructions.forEach { instruction ->
            register.execute(instruction)
        }
        return register.signalStrength
    }

    fun part2(instructions: List<Instruction>): String {
        val register = Register()
        instructions.forEach { instruction ->
            register.execute(instruction)
        }
        return register.image
    }

    fun parse(stringList: List<String>): List<Instruction> {
        return stringList.map { line ->
            when {
                line.contains("addx") -> {
                    Instruction.Addx(line.split(" ").last().toInt())
                }

                line.contains("noop") -> {
                    Instruction.Noop
                }

                else -> error("unknown instruction")
            }
        }
    }

    val testInput = readInput("Day10_test")
    check(part1(parse(testInput)) == 13_140)
    println(part2(parse(testInput)))
    println()

    val input = readInput("Day10_input")
    println(part1(parse(input)))
    println(part2(parse(input)))
}

class Register {
    var signalStrength: Int = 0
        private set

    var image: String = "🀫"
        private set

    private var value: Int = 1

    private var cycle: Int by Delegates.observable(0) { _, _, newCycle ->
        if (newCycle == 20 || (newCycle - 20) % 40 == 0 && newCycle <= 220) {
            signalStrength += (newCycle * value)
        }
        if (newCycle % 40 == 0) image += "\n"
        image += if (((newCycle - 1) % 40) in (value - 1..value + 1)) "🀫" else "🀆"
    }

    fun execute(instruction: Instruction) {
        when (instruction) {
            is Instruction.Addx -> {
                repeat(2) { cycle += 1 }
                value += instruction.x
            }

            is Instruction.Noop -> {
                repeat(1) { cycle += 1 }
            }
        }
    }
}

sealed interface Instruction {
    class Addx(val x: Int) : Instruction
    object Noop : Instruction
}
