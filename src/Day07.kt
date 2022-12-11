fun main() {
    fun getNestedDirectories(folder: FileSystem.Storage): List<FileSystem.Storage> {
        val directories = mutableListOf<FileSystem.Storage>()
        val stack = mutableListOf(folder to false)
        while (stack.isNotEmpty()) {
            val (storage, checked) = stack.last()
            if (checked) stack.removeLast() else {
                val fs = storage.fileSystem.filterIsInstance<FileSystem.Storage>()
                when (fs.isEmpty()) {
                    true -> stack.removeLast()
                    false -> {
                        directories.addAll(fs)
                        stack.removeLast()
                        stack += fs.map { it to false }
                    }
                }
            }
        }
        return directories + folder
    }

    fun part1(root: FileSystem.Storage): Int {
        val directories = getNestedDirectories(root)
        return directories.map { it.size }.filter { it <= 100_000 }.sum()
    }

    fun part2(root: FileSystem.Storage): Int {
        val directories = getNestedDirectories(root)
        return directories.filter { 30_000_000 <= (70_000_000 - root.size + it.size) }.minOf { it.size }
    }

    fun parse(stringList: List<String>): FileSystem.Storage {
        val inputOutput = stringList.joinToString("\n").split("$").drop(1).map { it.trim() }
        var stack: FileSystem.Storage = FileSystem.Storage.Root()
        val root = stack
        for (io in inputOutput.drop(1)) {
            when (io.substring(0, 2)) {
                "ls" -> {
                    val fs = io.lines().drop(1).asFileSystem(stack)
                    stack.fileSystem += fs
                }

                "cd" -> {
                    val path = io.removePrefix("cd ").trim()
                    stack = if (path.contains("..")) {
                        stack.parentDir ?: error("illegal move")
                    } else if (path.contains("/")) {
                        root
                    } else {
                        stack.fileSystem.filterIsInstance<FileSystem.Storage>().firstOrNull { it.name == path }
                            ?: error("tried to cd into a file or none existent directory")
                    }
                }
            }
        }
        return root
    }

    val testInput = readInput("Day07_test")
    check(part1(parse(testInput)) == 95_437)
    check(part2(parse(testInput)) == 24_933_642)

    val input = readInput("Day07_input")
    println(part1(parse(input)))
    println(part2(parse(input)))
}

/* Code copied from Jetbrains Sebastian Aigner */
private fun List<String>.asFileSystem(parent: FileSystem.Storage): List<FileSystem> {
    return map { elem ->
        when {
            elem.startsWith("dir") -> {
                FileSystem.Storage.Directory(name = elem.removePrefix("dir "), parent)
            }

            elem.first().isDigit() -> {
                val (size, name) = elem.split(" ")
                FileSystem.File(name.trim(), size.toInt(), parent)
            }

            else -> error("Malformed input")
        }
    }
}

sealed interface FileSystem {
    var name: String
    var size: Int
    val parentDir: Storage?

    class File(override var name: String, val fSize: Int, override val parentDir: Storage?) : FileSystem {
        override var size = fSize
    }

    sealed interface Storage : FileSystem {
        val fileSystem: MutableList<FileSystem>
        override var size: Int
            get() = fileSystem.sumOf { it.size }
            set(value) {}

        class Directory(
            override var name: String,
            override val parentDir: Storage?,
            override val fileSystem: MutableList<FileSystem> = mutableListOf()
        ) : Storage

        class Root(
            override var name: String = "/",
            override val parentDir: Storage? = null,
            override val fileSystem: MutableList<FileSystem> = mutableListOf()
        ) : Storage
    }
}
