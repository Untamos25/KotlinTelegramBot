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
                    if (dictionary.any { it.correctAnswersCount < NUMBER_OF_CORRECT_REPETITIONS_TO_LEARN_WORD }) {
                        val allWordsToLearn =
                            dictionary.filter { it.correctAnswersCount < NUMBER_OF_CORRECT_REPETITIONS_TO_LEARN_WORD }

                        allWordsToLearn.forEach {
                            println(it.original)

                            val wordsInQuestion =
                                ((allWordsToLearn - it).shuffled().take(NUMBER_OF_ANSWERS - 1) + listOf(it)).shuffled()

                            wordsInQuestion.forEachIndexed {id, word ->
                                println("${id+1} - ${word.translate}")
                            }
                            readln()
                        }
                    } else {
                        println("Вы выучили все слова.")
                        break
                    }
                }

//                while (true) {
//                    if (dictionary.any { it.correctAnswersCount < NUMBER_OF_CORRECT_REPETITIONS_TO_LEARN_WORD }) {
//                        val wordsToLearn =
//                            dictionary.filter { it.correctAnswersCount < NUMBER_OF_CORRECT_REPETITIONS_TO_LEARN_WORD }
//
//                        wordsToLearn.forEach {
//                            println(it.original)
//
//                            val correctAnswer = listOf(it.translate)
//                            val wordsToLearnTranslations =
//                                (wordsToLearn.map { it.translate } - correctAnswer).shuffled()
//                            val otherTranslations = (dictionary - wordsToLearn).map { it.translate }.shuffled()
//                            val allAnswers = correctAnswer + wordsToLearnTranslations + otherTranslations
//
//                            allAnswers.take(NUMBER_OF_ANSWERS).shuffled().forEachIndexed { id, translation ->
//                                println("${id+1} - $translation")
//                            }
//
//                            println("\n0 - Вернуться в меню")
//                            print("Ответ: ")
//                            val input = readln().toIntOrNull()
//                            if (input == 0) return
//                        }
//                        println()
//
//                    } else {
//                        println("Вы выучили все слова.")
//                        break
//                    }
//                }
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