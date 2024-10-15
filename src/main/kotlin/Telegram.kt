import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
        println(updates)

        if (updates.contains("\"result\":[]")) continue

        val updateIdToRegex: Regex = "\"update_id\":(.+?),".toRegex()
        var matchResult: MatchResult? = updateIdToRegex.find(updates)
        val updateIdString = matchResult?.groups?.get(1)?.value
        if (updateIdString != null) updateId = updateIdString.toInt() + 1

        val messageToRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        matchResult = messageToRegex.find(updates)
        val text = matchResult?.groups?.get(1)?.value

        println(updateId)
        println(text)
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}
