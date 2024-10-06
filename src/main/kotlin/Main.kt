import java.io.File

private const val NUMBER_OF_CORRECT_REPETITIONS_TO_LEARN_WORD = 3
private const val NUMBER_OF_ANSWERS = 4

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0
)

fun saveDictionary(dictionary: List<Word>, file: File) {
    val lines = dictionary.map { "${it.original}|${it.translate}|${it.correctAnswersCount}" }
    file.writeText(lines.joinToString("\n"))
}

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

                    if (unlearnedWords.isEmpty()) {
                        println("Вы выучили все слова.")
                        break
                    }

                    var wordsForQuestion = unlearnedWords.shuffled().take(NUMBER_OF_ANSWERS)
                    val rightAnswer = wordsForQuestion.random()

                    if (wordsForQuestion.size < NUMBER_OF_ANSWERS) {
                        val learnedWords = dictionary
                            .filter { it.correctAnswersCount >= NUMBER_OF_CORRECT_REPETITIONS_TO_LEARN_WORD }
                            .shuffled()
                        wordsForQuestion =
                            (wordsForQuestion + learnedWords.take(NUMBER_OF_ANSWERS - wordsForQuestion.size)).shuffled()
                    }

                    val rightAnswerIndex = wordsForQuestion.indexOfFirst { it.translate == rightAnswer.translate } + 1

                    println(rightAnswer.original)
                    wordsForQuestion.forEachIndexed { id, word ->
                        println("${id + 1} - ${word.translate}")
                    }
                    println("\n0 - Выход в меню")

                    val input = readln().toIntOrNull()
                    when (input) {
                        in 1..NUMBER_OF_ANSWERS -> {
                            if (input == rightAnswerIndex) {
                                println("Правильно!")
                                val wordIndexInDictionary = dictionary.indexOf(rightAnswer)
                                dictionary[wordIndexInDictionary].correctAnswersCount++
                                saveDictionary(dictionary, wordsFile)
                            }
                            else println("Неправильно - ${rightAnswer.original} [${rightAnswer.translate}]")
                        }

                        0 -> break
                        else -> println(
                            "Для выбора варианта ответа введите число от 1 до $NUMBER_OF_ANSWERS. " +
                                    "Или введите 0 для выхода в меню."
                        )
                    }
                    println()
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