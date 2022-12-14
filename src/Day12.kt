fun main() {
    fun shortestPath(
        map: List<List<Elevation>>,
        startElevation: Elevation,
        endElevation: Elevation
    ): List<List<Elevation>> {
        var paths =
            listOf(listOf(map.flatten().firstOrNull { it == endElevation } ?: error("Can't find highest point")))
        while (paths.map { it.last() }.none { it == startElevation }) {
            paths = paths
                // Eliminating paths that have the same start and end points by taking the shortest path already
                .groupBy { it.last() }
                .map { (_, list) -> list.minBy { it.size } }
                // Extending paths
                .map { path ->
                    val (end, rest) = path.last() to path.dropLast(1)
                    map.possibleNext(end)
                        .filter { cell -> cell !in rest } // Prevent loop
                        .map { cell -> rest + listOf(end, cell) }
                        .ifEmpty { listOf(path) }
                }.flatten().also { if (it == paths) error("Can't find path to starting point") }
        }
        return paths
    }

    fun part1(map: List<List<Elevation>>): Int {
        val startElevation = map.flatten().first { it.h == -1 }
        val endElevation = map.flatten().first { it.h == 26 }
        val paths = shortestPath(map, startElevation, endElevation)
        return paths.filter { it.last().h == -1 }.minBy { it.size }.count() - 1
    }

    fun part2(map: List<List<Elevation>>): Int {
        // Takes forever for large maps, but works
        val endElevation = map.flatten().first { it.h == 26 }
        val startElevations = map.flatten().filter { it.h == 0 } + map.flatten().first { it.h == -1 }
        println(startElevations.size)
        return startElevations
            .map { start ->
                runCatching { shortestPath(map, start, endElevation) }
                    .getOrElse { emptyList() }
            }
            .flatten()
            .filter { it.last().h == -1 || it.last().h == 0 }
            .minBy { it.size }
            .count() - 1
    }

    fun parse(stringList: List<String>): List<List<Elevation>> {
        return stringList.mapIndexed { rIdx, line ->
            line.mapIndexed { cIdx, h ->
                when {
                    h.isLowerCase() -> Elevation(rIdx, cIdx, h - 'a', h)
                    h == 'S' -> Elevation(-1, -1, -1, h)
                    h == 'E' -> Elevation(rIdx, cIdx, 26, h)
                    else -> error("un-excepted character $h")
                }
            }
        }
    }

    val testInput = readInput("Day12_test")
    check(part1(parse(testInput)) == 31)
    check(part2(parse(testInput)) == 29)

    val input = readInput("Day12_input")
    println(part1(parse(input)))
    println(part2(parse(input)))
}

data class Elevation(val x: Int, val y: Int, val h: Int, val ch: Char)

fun List<List<Elevation>>.possibleNext(elevation: Elevation): List<Elevation> {
    val (x, y, h, _) = elevation
    return listOf(+1 to +0, +0 to +1, -1 to +0, +0 to -1)
        .map { (i, j) -> i + x to j + y }
        .filterNot { (i, j) -> i < 0 || i >= size || j < 0 || j >= this.first().size }
        .map { (i, j) -> this[i][j] }
        .filter { it.h + 1 == h || it.h >= h }
}

/**
 * Advent of Code[About][Events][Shop][Settings][Log Out]cisimon7 23*
 *        λy.2022[Calendar][AoC++][Sponsors][Leaderboard][Stats]
 * Our sponsors help make Advent of Code possible:
 * NUMERAI - join the hardest data science tournament in the world
 * --- Day 12: Hill Climbing Algorithm ---
 * You try contacting the Elves using your handheld device, but the river you're following must be too low to get a decent signal.
 *
 * You ask the device for a heightmap of the surrounding area (your puzzle input). The heightmap shows the local area from above broken into a grid; the elevation of each square of the grid is given by a single lowercase letter, where a is the lowest elevation, b is the next-lowest, and so on up to the highest elevation, z.
 *
 * Also included on the heightmap are marks for your current position (S) and the location that should get the best signal (E). Your current position (S) has elevation a, and the location that should get the best signal (E) has elevation z.
 *
 * You'd like to reach E, but to save energy, you should do it in as few steps as possible. During each step, you can move exactly one square up, down, left, or right. To avoid needing to get out your climbing gear, the elevation of the destination square can be at most one higher than the elevation of your current square; that is, if your current elevation is m, you could step to elevation n, but not to elevation o. (This also means that the elevation of the destination square can be much lower than the elevation of your current square.)
 *
 * For example:
 *
 * Sabqponm
 * abcryxxl
 * accszExk
 * acctuvwj
 * abdefghi
 * Here, you start in the top-left corner; your goal is near the middle. You could start by moving down or right, but eventually you'll need to head toward the e at the bottom. From there, you can spiral around to the goal:
 *
 * v..v<<<<
 * >v.vv<<^
 * .>vv>E^^
 * ..v>>>^^
 * ..>>>>>^
 * In the above diagram, the symbols indicate whether the path exits each square moving up (^), down (v), left (<), or right (>). The location that should get the best signal is still E, and . marks unvisited squares.
 *
 * This path reaches the goal in 31 steps, the fewest possible.
 *
 * What is the fewest steps required to move from your current position to the location that should get the best signal?
 *
 * Your puzzle answer was 394.
 *
 * The first half of this puzzle is complete! It provides one gold star: *
 *
 * --- Part Two ---
 * As you walk up the hill, you suspect that the Elves will want to turn this into a hiking trail. The beginning isn't very scenic, though; perhaps you can find a better starting point.
 *
 * To maximize exercise while hiking, the trail should start as low as possible: elevation a. The goal is still the square marked E. However, the trail should still be direct, taking the fewest steps to reach its goal. So, you'll need to find the shortest path from any square at elevation a to the square marked E.
 *
 * Again consider the example from above:
 *
 * Sabqponm
 * abcryxxl
 * accszExk
 * acctuvwj
 * abdefghi
 * Now, there are six choices for starting position (five marked a, plus the square marked S that counts as being at elevation a). If you start at the bottom-left square, you can reach the goal most quickly:
 *
 * ...v<<<<
 * ...vv<<^
 * ...v>E^^
 * .>v>>>^^
 * >^>>>>>^
 * This path reaches the goal in only 29 steps, the fewest possible.
 *
 * What is the fewest steps required to move starting from any square with elevation a to the location that should get the best signal?
 *
 * Answer:
 *
 *
 * Although it hasn't changed, you can still get your puzzle input.
 *
 * You can also [Share] this puzzle.*/