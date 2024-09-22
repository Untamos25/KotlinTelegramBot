import java.io.File

private const val NUMBER_OF_CORRECT_REPETITIONS_TO_LEARN_WORD = 3

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int = 0
)

fun main() {

    val wordsFile: File = File("words.txt")
    val lines: List<String> = wordsFile.readLines()
    val dictionary: MutableList<Word> = mutableListOf()

    for (line in lines) {
        val line = line.split("|")
        val word = Word(original = line[0], translate = line[1], correctAnswersCount = line[2].toIntOrNull() ?: 0)
        dictionary.add(word)
    }

    while (true) {
        println(
            """Меню:
        |1 - Учить слова
        |2 - Статистика
        |0 - Выход
    """.trimMargin()
        )
        val input = readln().toIntOrNull()
        when (input) {
            1 -> println("Вы выбрали 1 - Учить слова")
            2 -> {
                val numberOfLearnedWords =
                    dictionary.filter { it.correctAnswersCount >= NUMBER_OF_CORRECT_REPETITIONS_TO_LEARN_WORD }
                val percentOfLearnedWords = (numberOfLearnedWords.size.toDouble() / dictionary.size * 100).toInt()
                println("Выучено ${numberOfLearnedWords.size} из ${dictionary.size} | $percentOfLearnedWords%")
            }

            0 -> break
            else -> println("Пожалуйста, укажите номер действия, которое вы хотите выполнить")
        }
    }
}