import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private const val URL_TELEGRAM_BOT = "https://api.telegram.org/bot"

class TelegramBotService(private val botToken: String) {

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "$URL_TELEGRAM_BOT$botToken/getUpdates?offset=$updateId"
        return sendRequest(urlGetUpdates)
    }

    fun sendMessage(chatId: Long, botAnswer: String) {
        val urlSendMessage = "$URL_TELEGRAM_BOT$botToken/sendMessage?chat_id=$chatId&text=$botAnswer"
        println(sendRequest(urlSendMessage))
    }

    private fun sendRequest(url: String): String {
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(url)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }
}