import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val LEARN_WORDS_CALLBACK = "learn_words_clicked"
const val STATISTICS_CALLBACK = "statistics_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
private const val URL_TELEGRAM_BOT = "https://api.telegram.org/bot"

class TelegramBotService(private val botToken: String) {
    fun getUpdates(updateId: Long): String {
        val urlGetUpdates = "$URL_TELEGRAM_BOT$botToken/getUpdates?offset=$updateId"
        return sendRequest(urlGetUpdates)
    }

    fun sendMessage(json: Json, chatId: Long?, botAnswer: String): String {
        val urlSendMessage = "$URL_TELEGRAM_BOT$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = botAnswer,
        )
        val requestBodyString = json.encodeToString(requestBody)

        return sendRequest(urlSendMessage, requestBodyString)
    }

    fun sendMenu(json: Json, chatId: Long?): String {
        val urlSendMenu = "$URL_TELEGRAM_BOT$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "Главное меню",
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyboard(text = "Изучать слова", callbackData = LEARN_WORDS_CALLBACK),
                    ),
                    listOf(
                        InlineKeyboard(text = "Статистика", callbackData = STATISTICS_CALLBACK),
                    )
                )
            )
        )

        val requestBodyString = json.encodeToString(requestBody)

        return sendRequest(urlSendMenu, requestBodyString)
    }

    fun sendQuestion(json: Json, chatId: Long?, question: Question): String {
        val urlSendMessage = "$URL_TELEGRAM_BOT$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = question.rightAnswer.original,
            replyMarkup = ReplyMarkup(
                listOf(question.wordsForQuestion.mapIndexed { index, word ->
                    InlineKeyboard(
                        text = word.translation, callbackData = "$CALLBACK_DATA_ANSWER_PREFIX${index + 1}"
                    )
                })
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        return sendRequest(urlSendMessage, requestBodyString)
    }

    private fun sendRequest(url: String, requestBodyString: String? = null): String {
        val client: HttpClient = HttpClient.newBuilder().build()
        val requestBuilder = HttpRequest.newBuilder().uri(URI.create(url))

        if (requestBodyString != null) {
            requestBuilder.header("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
        }
        val request = requestBuilder.build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }
}
