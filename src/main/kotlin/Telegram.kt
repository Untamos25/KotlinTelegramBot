import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long?,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>,
)

@Serializable
data class InlineKeyboard(
    @SerialName("callback_data")
    val callbackData: String,
    @SerialName("text")
    val text: String,
)

fun main(args: Array<String>) {
    val botToken = args[0]
    var lastUpdateId = 0L

    val json = Json {
        ignoreUnknownKeys = true
    }

    val telegramBotService = TelegramBotService(botToken)
    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(2000)

        val responseString: String = telegramBotService.getUpdates(lastUpdateId)
        println(responseString)
        val response: Response = json.decodeFromString(responseString)
        val updates = response.result
        val firstUpdate = updates.firstOrNull() ?: continue
        val updateId = firstUpdate.updateId
        lastUpdateId = updateId + 1

        val text = firstUpdate.message?.text
        val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id
        val data = firstUpdate.callbackQuery?.data

        if (text?.lowercase() == "/start") telegramBotService.sendMenu(json, chatId)

        if (data?.lowercase() == STATISTICS_CALLBACK) {
            val statistics = trainer.getStatistics()
            val progress = "Выучено ${statistics.numberOfLearnedWords} из ${statistics.sizeOfDictionary} | " +
                    "${statistics.percentOfLearnedWords}%"

            telegramBotService.sendMessage(json, chatId, progress)
        }

        if (data?.lowercase() == LEARN_WORDS_CALLBACK) {
            checkNextQuestionAndSend(json, trainer, telegramBotService, chatId)
        }

        if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
            val userAnswer = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
            if (trainer.checkAnswer(userAnswer)) {
                telegramBotService.sendMessage(json, chatId, "Правильно!")
            } else {
                val rightAnswer = trainer.question?.rightAnswer
                telegramBotService.sendMessage(
                    json, chatId, "Неправильно!\n" +
                            "${rightAnswer?.original} – это ${rightAnswer?.translation}"
                )
            }
            checkNextQuestionAndSend(json, trainer, telegramBotService, chatId)
        }
    }
}

fun checkNextQuestionAndSend(
    json: Json,
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long?
) {
    val nextQuestion = trainer.getNextQuestion()
    if (nextQuestion == null) {
        telegramBotService.sendMessage(json, chatId, "Вы выучили все слова в базе")
        telegramBotService.sendMenu(json, chatId)
    } else {
        telegramBotService.sendQuestion(json, chatId, nextQuestion)
    }
}
