const val NUMBER_OF_ANSWERS = 4

fun Question.questionToString(): String {
    val variants = this.wordsForQuestion
        .mapIndexed { id, word -> "${id + 1} - ${word.translate}" }
        .joinToString(separator = "\n")
    return this.rightAnswer.original + "\n" + variants + "\n\n" + "0 - Выход в меню"
}

fun main() {
    val trainer = LearnWordsTrainer()

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
                    val question = trainer.getNextQuestion()
                    if (question == null) {
                        println("Вы выучили все слова.")
                        break
                    } else {
                        println(question.questionToString())

                        val input = readln().toIntOrNull()
                        when (input) {
                            in 1..NUMBER_OF_ANSWERS -> {
                                if (trainer.checkAnswer(input)) println("\nПравильно!\n")
                                else println(
                                    "\nНеправильно! " +
                                            "${question.rightAnswer.original} = [${question.rightAnswer.translate}]\n"
                                )
                            }

                            0 -> break
                            else -> println(
                                "Для выбора варианта ответа введите число от 1 до $NUMBER_OF_ANSWERS. " +
                                        "Или введите 0 для выхода в меню."
                            )
                        }
                    }
                }
            }

            2 -> {
                val statistics = trainer.getStatistics()
                println(
                    "Выучено ${statistics.numberOfLearnedWords} из ${statistics.sizeOfDictionary} |" +
                            " ${statistics.percentOfLearnedWords}%"
                )
            }

            0 -> break
            else -> println("Пожалуйста, укажите номер действия, которое вы хотите выполнить")
        }
    }
}