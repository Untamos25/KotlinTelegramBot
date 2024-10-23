import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

const val LEARN_WORDS_CALLBACK = "learn_words_clicked"
const val STATISTICS_CALLBACK = "statistics_clicked"
private const val URL_TELEGRAM_BOT = "https://api.telegram.org/bot"

class TelegramBotService(private val botToken: String) {

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "$URL_TELEGRAM_BOT$botToken/getUpdates?offset=$updateId"
        return sendRequest(urlGetUpdates)
    }

    fun sendMessage(chatId: Long, botAnswer: String): String {
        val encodedBotAnswer = URLEncoder.encode(botAnswer, StandardCharsets.UTF_8)
        val urlSendMessage = "$URL_TELEGRAM_BOT$botToken/sendMessage?chat_id=$chatId&text=$encodedBotAnswer"
        return sendRequest(urlSendMessage)
    }

    fun sendMenu(chatId: Long): String {
        val urlSendMenu = "$URL_TELEGRAM_BOT$botToken/sendMessage"
        val sendMenuBody = """
             {
                "chat_id": $chatId,
                "text": "Главное меню",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "Изучать слова",
                                "callback_data": "$LEARN_WORDS_CALLBACK"
                            },
                            {
                                "text": "Статистика",
                                "callback_data": "$STATISTICS_CALLBACK"
                            }
                        ]
                    ]
                }
             }
        """.trimIndent()

        return sendRequest(urlSendMenu, sendMenuBody)
    }

    private fun sendRequest(url: String, body: String? = null): String {
        val client: HttpClient = HttpClient.newBuilder().build()
        val requestBuilder = HttpRequest.newBuilder().uri(URI.create(url))

        if (body != null) {
            requestBuilder.header("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
        }
        val request = requestBuilder.build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }
}