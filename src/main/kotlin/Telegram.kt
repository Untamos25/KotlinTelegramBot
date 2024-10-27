import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

    val telegramBotService = TelegramBotService(botToken)
    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(2000)

        val responseString: String = telegramBotService.getUpdates(lastUpdateId)
        println(responseString)
        val response: Response = telegramBotService.decodeResponse(responseString)
        val updates = response.result
        val firstUpdate = updates.firstOrNull() ?: continue
        val updateId = firstUpdate.updateId
        lastUpdateId = updateId + 1

        val text = firstUpdate.message?.text
        val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id
        val data = firstUpdate.callbackQuery?.data

        if (text?.lowercase() == "/start") telegramBotService.sendMenu(chatId)

        if (data?.lowercase() == STATISTICS_CALLBACK) {
            val statistics = trainer.getStatistics()
            val progress = "Выучено ${statistics.numberOfLearnedWords} из ${statistics.sizeOfDictionary} | " +
                    "${statistics.percentOfLearnedWords}%"

            telegramBotService.sendMessage(chatId, progress)
        }

        if (data?.lowercase() == LEARN_WORDS_CALLBACK) {
            checkNextQuestionAndSend(trainer, telegramBotService, chatId)
        }

        if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
            val userAnswer = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
            if (trainer.checkAnswer(userAnswer)) {
                telegramBotService.sendMessage(chatId, "Правильно!")
            } else {
                val rightAnswer = trainer.question?.rightAnswer
                telegramBotService.sendMessage(
                    chatId, "Неправильно!\n${rightAnswer?.original} – это ${rightAnswer?.translation}"
                )
            }
            checkNextQuestionAndSend(trainer, telegramBotService, chatId)
        }

        if (data?.lowercase() == BACK_TO_MENU_CALLBACK) {
            telegramBotService.sendMenu(chatId)
        }
    }
}

fun checkNextQuestionAndSend(
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long?
) {
    val nextQuestion = trainer.getNextQuestion()
    if (nextQuestion == null) {
        telegramBotService.sendMessage(chatId, "Вы выучили все слова в базе")
        telegramBotService.sendMenu(chatId)
    } else {
        telegramBotService.sendQuestion(chatId, nextQuestion)
    }
}
