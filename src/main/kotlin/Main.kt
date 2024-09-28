import java.io.File

private const val NUMBER_OF_CORRECT_REPETITIONS_TO_LEARN_WORD = 3
private const val NUMBER_OF_ANSWERS = 4

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
            1 -> {
                while (true) {
                    val unlearnedWords =
                        dictionary.filter { it.correctAnswersCount < NUMBER_OF_CORRECT_REPETITIONS_TO_LEARN_WORD }

                    if (unlearnedWords.isNotEmpty()) {
                        if (unlearnedWords.size >= NUMBER_OF_ANSWERS) {
                            val wordsForQuestion = unlearnedWords.shuffled().take(NUMBER_OF_ANSWERS)

                            println(wordsForQuestion.random().original)

                            wordsForQuestion.forEachIndexed { id, word ->
                                println("${id + 1} - ${word.translate}")
                            }
                        } else {
                            println(unlearnedWords.random().original)

                            val wordsForQuestion = (unlearnedWords + dictionary
                                .filter { it.correctAnswersCount >= NUMBER_OF_CORRECT_REPETITIONS_TO_LEARN_WORD }
                                .shuffled()
                                .take(NUMBER_OF_ANSWERS - unlearnedWords.size)
                                    ).shuffled()

                            wordsForQuestion.forEachIndexed { id, word ->
                                println("${id + 1} - ${word.translate}")
                            }
                        }

                        println("\n0 - Выход в меню")
                        val input = readln().toIntOrNull()
                        if (input == 0) break

                    } else {
                        println("Вы выучили все слова.")
                        break
                    }
                }
            }

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