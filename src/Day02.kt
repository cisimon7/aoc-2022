fun main() {
    fun abcMap(a: String) = when (a) {
        "A" -> HandShape.Rock
        "B" -> HandShape.Paper
        "C" -> HandShape.Scissors
        else -> throw Error("$a is not applicable")
    }

    fun xyzMap(a: String) = when (a) {
        "X" -> HandShape.Rock to Outcome.Loose
        "Y" -> HandShape.Paper to Outcome.Draw
        "Z" -> HandShape.Scissors to Outcome.Win
        else -> throw Error("$a is not applicable")
    }

    fun part1(input: List<String>): Int {
        return input
            .map { line -> line.split(" ") }
            .map { Pair(it[0], it[1]) }
            .map { (elf, me) -> abcMap(elf) to xyzMap(me).first }
            .sumOf { (elf, me) -> me.play(elf) }
    }

    fun part2(input: List<String>): Int {
        return input
            .map { line -> line.split(" ") }
            .map { Pair(it[0], it[1]) }
            .map { (elfa, mea) ->
                val elf = abcMap(elfa)
                when (xyzMap(mea).second) {
                    Outcome.Draw -> elf to elf
                    Outcome.Loose -> elf to elf.negative()
                    Outcome.Win -> elf to elf.positive()
                }
            }
            .sumOf { (elf, me) -> me.play(elf) }
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02_input")
    println(part1(input))
    println(part2(input))
}

sealed interface HandShape {

    val point: Int

    object Rock : HandShape {
        override val point = 1
    }

    object Paper : HandShape {
        override val point = 2
    }

    object Scissors : HandShape {
        override val point = 3
    }
}

fun HandShape.positive(): HandShape {
    return when (this) {
        HandShape.Paper -> HandShape.Scissors
        HandShape.Rock -> HandShape.Paper
        HandShape.Scissors -> HandShape.Rock
    }
}

fun HandShape.negative(): HandShape {
    return when (this) {
        HandShape.Paper -> HandShape.Rock
        HandShape.Rock -> HandShape.Scissors
        HandShape.Scissors -> HandShape.Paper
    }
}

sealed interface Outcome {
    object Win : Outcome
    object Draw : Outcome
    object Loose : Outcome
}

val winMatrix: List<List<Double>> = listOf(
    listOf(0.5, 0.0, 1.0),
    listOf(1.0, 0.5, 0.0),
    listOf(0.0, 1.0, 0.5)
)

fun HandShape.play(other: HandShape): Int {
    val (idx1, idx2) = listOf(this, other).map { shape ->
        listOf(HandShape.Rock, HandShape.Paper, HandShape.Scissors).indexOf(shape)
    }
    val win = winMatrix[idx1][idx2] * 6
    return if (win == 0.0) this.point else (win + this.point).toInt()
}
