fun main() {

    fun part1(pairs: List<Pair<Packet.Parent, Packet.Parent>>): Int {
        return pairs.map { (left, right) -> compare(left, right) }
            .mapIndexed { idx, case -> idx + 1 to case }
            .filter { (_, case) -> case == true }
            .sumOf { (idx, _) -> idx }
    }

    fun part2(pairs: List<Pair<Packet.Parent, Packet.Parent>>): Int {
        val divider1 = Packet.Parent(mutableListOf(Packet.Parent(mutableListOf(Packet.Child(2)))))
        val divider2 = Packet.Parent(mutableListOf(Packet.Parent(mutableListOf(Packet.Child(6)))))
        val packets = (pairs + (divider1 to divider2))
            .map { (left, right) -> listOf(left, right) }
            .flatten()
            .sortedWith { first, second ->
                when (compare(first, second)) {
                    true -> 1
                    false -> -1
                    null -> 0
                }
            }.reversed()
        val idx1 = packets.indexOf(divider1) + 1
        val idx2 = packets.indexOf(divider2) + 1
        return idx1 * idx2
    }

    fun parse(stringList: List<String>): List<Pair<Packet.Parent, Packet.Parent>> {
        return stringList.windowed(2, 3).map { line ->
            packetify(line[0].drop(1)) to packetify(line[1].drop(1))
        }
    }

    val testInput = readInput("Day13_test")
    check(part1(parse(testInput)) == 13)
    check(part2(parse(testInput)) == 140)

    val input = readInput("Day13_input")
    println(part1(parse(input)))
    println(part2(parse(input)))
}

tailrec fun compare(left: Packet.Parent, right: Packet.Parent): Boolean? {
    val leftNext = left.children.firstOrNull()
    val rightNext = right.children.firstOrNull()
    return when {
        leftNext is Packet.Parent && rightNext is Packet.Parent -> compare(leftNext, rightNext) ?: compare(
            Packet.Parent(left.children.drop(1).toMutableList()),
            Packet.Parent(right.children.drop(1).toMutableList())
        )

        leftNext is Packet.Child && rightNext is Packet.Child -> if (leftNext.value == rightNext.value) {
            compare(
                Packet.Parent(left.children.drop(1).toMutableList()),
                Packet.Parent(right.children.drop(1).toMutableList())
            )
        } else leftNext.value < rightNext.value

        leftNext is Packet.Child && rightNext is Packet.Parent -> compare(
            Packet.Parent(mutableListOf(Packet.Parent(mutableListOf(leftNext)))),
            right
        )

        leftNext is Packet.Parent && rightNext is Packet.Child -> compare(
            left,
            Packet.Parent(mutableListOf(Packet.Parent(mutableListOf(rightNext))))
        )

        leftNext == null && rightNext == null -> null
        leftNext == null && rightNext != null -> true
        leftNext != null && rightNext == null -> false
        else -> error("unknown pair")
    }
}

tailrec fun packetify(
    line: String,
    parent: Packet.Parent = Packet.Parent(),
    lineage: MutableList<Packet.Parent> = mutableListOf()
): Packet.Parent {
    return when {
        line.isEmpty() -> parent
        line.first() == ',' -> packetify(line.drop(1), parent, lineage)
        line.first() == '[' -> packetify(line.drop(1), Packet.Parent(), (lineage + parent).toMutableList())
        line.first() == ']' -> if (lineage.isEmpty()) parent else {
            val grandParent = lineage.last()
            grandParent.children.add(parent)
            packetify(line.drop(1), grandParent, lineage.dropLast(1).toMutableList())
        }

        else -> {
            val childIdx = line.indexOfFirst { it == ',' || it == ']' }
            parent.children.add(Packet.Child(line.take(childIdx).toInt()))
            packetify(line.drop(childIdx), parent, lineage)
        }
    }
}

sealed interface Packet {
    class Child(val value: Int) : Packet

    class Parent(val children: MutableList<Packet> = mutableListOf()) : Packet
}


/**
 * --- Day 13: Distress Signal ---
 * You climb the hill and again try contacting the Elves. However, you instead receive a signal you weren't expecting: a distress signal.
 *
 * Your handheld device must still not be working properly; the packets from the distress signal got decoded out of order. You'll need to re-order the list of received packets (your puzzle input) to decode the message.
 *
 * Your list consists of pairs of packets; pairs are separated by a blank line. You need to identify how many pairs of packets are in the right order.
 *
 * For example:
 *
 * [1,1,3,1,1]
 * [1,1,5,1,1]
 *
 * [[1],[2,3,4]]
 * [[1],4]
 *
 * [9]
 * [[8,7,6]]
 *
 * [[4,4],4,4]
 * [[4,4],4,4,4]
 *
 * [7,7,7,7]
 * [7,7,7]
 *
 * []
 * [3]
 *
 * [[[]]]
 * [[]]
 *
 * [1,[2,[3,[4,[5,6,7]]]],8,9]
 * [1,[2,[3,[4,[5,6,0]]]],8,9]
 * Packet data consists of lists and integers. Each list starts with [, ends with ], and contains zero or more comma-separated values (either integers or other lists). Each packet is always a list and appears on its own line.
 *
 * When comparing two values, the first value is called left and the second value is called right. Then:
 *
 * If both values are integers, the lower integer should come first. If the left integer is lower than the right integer, the inputs are in the right order. If the left integer is higher than the right integer, the inputs are not in the right order. Otherwise, the inputs are the same integer; continue checking the next part of the input.
 * If both values are lists, compare the first value of each list, then the second value, and so on. If the left list runs out of items first, the inputs are in the right order. If the right list runs out of items first, the inputs are not in the right order. If the lists are the same length and no comparison makes a decision about the order, continue checking the next part of the input.
 * If exactly one value is an integer, convert the integer to a list which contains that integer as its only value, then retry the comparison. For example, if comparing [0,0,0] and 2, convert the right value to [2] (a list containing 2); the result is then found by instead comparing [0,0,0] and [2].
 * Using these rules, you can determine which of the pairs in the example are in the right order:
 *
 * == Pair 1 ==
 * - Compare [1,1,3,1,1] vs [1,1,5,1,1]
 *   - Compare 1 vs 1
 *   - Compare 1 vs 1
 *   - Compare 3 vs 5
 *     - Left side is smaller, so inputs are in the right order
 *
 * == Pair 2 ==
 * - Compare [[1],[2,3,4]] vs [[1],4]
 *   - Compare [1] vs [1]
 *     - Compare 1 vs 1
 *   - Compare [2,3,4] vs 4
 *     - Mixed types; convert right to [4] and retry comparison
 *     - Compare [2,3,4] vs [4]
 *       - Compare 2 vs 4
 *         - Left side is smaller, so inputs are in the right order
 *
 * == Pair 3 ==
 * - Compare [9] vs [[8,7,6]]
 *   - Compare 9 vs [8,7,6]
 *     - Mixed types; convert left to [9] and retry comparison
 *     - Compare [9] vs [8,7,6]
 *       - Compare 9 vs 8
 *         - Right side is smaller, so inputs are not in the right order
 *
 * == Pair 4 ==
 * - Compare [[4,4],4,4] vs [[4,4],4,4,4]
 *   - Compare [4,4] vs [4,4]
 *     - Compare 4 vs 4
 *     - Compare 4 vs 4
 *   - Compare 4 vs 4
 *   - Compare 4 vs 4
 *   - Left side ran out of items, so inputs are in the right order
 *
 * == Pair 5 ==
 * - Compare [7,7,7,7] vs [7,7,7]
 *   - Compare 7 vs 7
 *   - Compare 7 vs 7
 *   - Compare 7 vs 7
 *   - Right side ran out of items, so inputs are not in the right order
 *
 * == Pair 6 ==
 * - Compare [] vs [3]
 *   - Left side ran out of items, so inputs are in the right order
 *
 * == Pair 7 ==
 * - Compare [[[]]] vs [[]]
 *   - Compare [[]] vs []
 *     - Right side ran out of items, so inputs are not in the right order
 *
 * == Pair 8 ==
 * - Compare [1,[2,[3,[4,[5,6,7]]]],8,9] vs [1,[2,[3,[4,[5,6,0]]]],8,9]
 *   - Compare 1 vs 1
 *   - Compare [2,[3,[4,[5,6,7]]]] vs [2,[3,[4,[5,6,0]]]]
 *     - Compare 2 vs 2
 *     - Compare [3,[4,[5,6,7]]] vs [3,[4,[5,6,0]]]
 *       - Compare 3 vs 3
 *       - Compare [4,[5,6,7]] vs [4,[5,6,0]]
 *         - Compare 4 vs 4
 *         - Compare [5,6,7] vs [5,6,0]
 *           - Compare 5 vs 5
 *           - Compare 6 vs 6
 *           - Compare 7 vs 0
 *             - Right side is smaller, so inputs are not in the right order
 * What are the indices of the pairs that are already in the right order? (The first pair has index 1, the second pair has index 2, and so on.) In the above example, the pairs in the right order are 1, 2, 4, and 6; the sum of these indices is 13.
 *
 * Determine which pairs of packets are already in the right order. What is the sum of the indices of those pairs?
 *
 * Your puzzle answer was 5555.
 *
 * --- Part Two ---
 * Now, you just need to put all of the packets in the right order. Disregard the blank lines in your list of received packets.
 *
 * The distress signal protocol also requires that you include two additional divider packets:
 *
 * [[2]]
 * [[6]]
 * Using the same rules as before, organize all packets - the ones in your list of received packets as well as the two divider packets - into the correct order.
 *
 * For the example above, the result of putting the packets in the correct order is:
 *
 * []
 * [[]]
 * [[[]]]
 * [1,1,3,1,1]
 * [1,1,5,1,1]
 * [[1],[2,3,4]]
 * [1,[2,[3,[4,[5,6,0]]]],8,9]
 * [1,[2,[3,[4,[5,6,7]]]],8,9]
 * [[1],4]
 * [[2]]
 * [3]
 * [[4,4],4,4]
 * [[4,4],4,4,4]
 * [[6]]
 * [7,7,7]
 * [7,7,7,7]
 * [[8,7,6]]
 * [9]
 * Afterward, locate the divider packets. To find the decoder key for this distress signal, you need to determine the indices of the two divider packets and multiply them together. (The first packet is at index 1, the second packet is at index 2, and so on.) In this example, the divider packets are 10th and 14th, and so the decoder key is 140.
 *
 * Organize all of the packets into the correct order. What is the decoder key for the distress signal?
 *
 * Your puzzle answer was 22852.
 *
 * Both parts of this puzzle are complete! They provide two gold stars: **
 *
 * At this point, you should return to your Advent calendar and try another puzzle.*/
