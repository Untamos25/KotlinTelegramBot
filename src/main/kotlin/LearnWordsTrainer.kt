import java.io.File

const val NUMBER_OF_ANSWERS = 4

data class Question(
    val wordsForQuestion: List<Word>,
    val rightAnswer: Word,
)

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0
)

data class Statistics(
    val numberOfLearnedWords: Int,
    val sizeOfDictionary: Int,
    val percentOfLearnedWords: Int
)

class LearnWordsTrainer(private val learnedAnswerCount: Int = 3) {
    var question: Question? = null
    private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val numberOfLearnedWords =
            dictionary.filter { it.correctAnswersCount >= learnedAnswerCount }.size
        val sizeOfDictionary = dictionary.size
        val percentOfLearnedWords = (numberOfLearnedWords.toDouble() / sizeOfDictionary * 100).toInt()
        return Statistics(numberOfLearnedWords, sizeOfDictionary, percentOfLearnedWords)
    }

    fun getNextQuestion(): Question? {
        val unlearnedWords = dictionary
            .filter { it.correctAnswersCount < learnedAnswerCount }

        if (unlearnedWords.isEmpty()) return null

        var wordsForQuestion = unlearnedWords.shuffled().take(NUMBER_OF_ANSWERS)
        val rightAnswer = wordsForQuestion.random()

        if (wordsForQuestion.size < NUMBER_OF_ANSWERS) {
            val learnedWords = dictionary
                .filter { it.correctAnswersCount >= learnedAnswerCount }
                .shuffled()
            wordsForQuestion =
                (wordsForQuestion + learnedWords.take(NUMBER_OF_ANSWERS - wordsForQuestion.size)).shuffled()
        }
        question = Question(wordsForQuestion, rightAnswer)
        return question
    }

    fun checkAnswer(input: Int?): Boolean {
        return question?.let { question ->
            val rightAnswerIndex =
                question.wordsForQuestion.indexOfFirst { it.translate == question.rightAnswer.translate } + 1
            if (input == rightAnswerIndex) {
                val wordIndexInDictionary = dictionary.indexOf(question.rightAnswer)
                dictionary[wordIndexInDictionary].correctAnswersCount++
                saveDictionary(dictionary)
                true
            } else {
                false
            }
        } == true
    }

    fun loadDictionary(): List<Word> {
        val dictionary = mutableListOf<Word>()
        val wordsFile = File("words.txt")

        wordsFile.readLines().forEach {
            val splitline = it.split("|")
            if (splitline.size == 3 && splitline.all { it.isNotBlank() }) {
                dictionary.add(
                    Word(
                        original = splitline[0],
                        translate = splitline[1],
                        correctAnswersCount = splitline[2].toIntOrNull() ?: 0
                    )
                )
            } else {
                println("Ошибка в строке \"$it\". Строка будет пропущена и удалена после сохранения словаря")
            }
        }
        return dictionary
    }

    fun saveDictionary(dictionary: List<Word>) {
        val wordsFile = File("words.txt")
        wordsFile.writeText("")
        for (word in dictionary) {
            wordsFile.appendText("${word.original}|${word.translate}|${word.correctAnswersCount}\n")
        }
    }
}
