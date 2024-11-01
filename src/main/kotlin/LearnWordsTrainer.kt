import kotlinx.serialization.Serializable
import java.io.File

const val NUMBER_OF_ANSWERS = 4

data class Question(
    val wordsForQuestion: List<Word>,
    val rightAnswer: Word,
)

@Serializable
data class Word(
    val original: String,
    val translation: String,
    var correctAnswersCount: Int = 0
)

data class Statistics(
    val numberOfLearnedWords: Int,
    val sizeOfDictionary: Int,
    val percentOfLearnedWords: Int
)

class LearnWordsTrainer(
    private val fileName: String = "words.txt",
    private val learnedAnswerCount: Int = 3
) {
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
                question.wordsForQuestion.indexOfFirst { it.translation == question.rightAnswer.translation } + 1
            if (input == rightAnswerIndex) {
                val wordIndexInDictionary = dictionary.indexOf(question.rightAnswer)
                dictionary[wordIndexInDictionary].correctAnswersCount++
                saveDictionary()
                true
            } else {
                false
            }
        } == true
    }

    fun loadDictionary(): List<Word> {
        val wordsFile = File(fileName)
        if (!wordsFile.exists()) {
            File("words.txt").copyTo(wordsFile)
        }

        val dictionary = mutableListOf<Word>()
        wordsFile.readLines().forEach {
            val splitline = it.split("|")
            if (splitline.size == 3 && splitline.all { it.isNotBlank() }) {
                dictionary.add(
                    Word(
                        original = splitline[0],
                        translation = splitline[1],
                        correctAnswersCount = splitline[2].toIntOrNull() ?: 0
                    )
                )
            } else {
                println("Ошибка в строке \"$it\". Строка будет пропущена и удалена после сохранения словаря")
            }
        }
        return dictionary
    }

    fun saveDictionary() {
        val wordsFile = File(fileName)
        wordsFile.writeText("")
        for (word in dictionary) {
            wordsFile.appendText("${word.original}|${word.translation}|${word.correctAnswersCount}\n")
        }
    }

    fun resetProgress() {
        dictionary.forEach { it.correctAnswersCount = 0 }
        saveDictionary()
    }
}
