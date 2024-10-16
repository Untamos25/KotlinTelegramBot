fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0

    val updateIdToRegex: Regex = "\"update_id\":(.+?),".toRegex()
    val messageToRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdToRegex: Regex = "(\\d+?),\"first_name\"".toRegex()

    while (true) {
        Thread.sleep(2000)

        val updates: String = TelegramBotService().getUpdates(botToken, updateId)
        println(updates)

        var matchResult: MatchResult? = updateIdToRegex.find(updates)
        updateId = matchResult?.groups?.get(1)?.value?.toInt()?.plus(1) ?: continue

        matchResult = messageToRegex.find(updates)
        val text = matchResult?.groups?.get(1)?.value

        matchResult = chatIdToRegex.find(updates)
        val chatId = matchResult?.groups?.get(1)?.value?.toInt()

        println(updateId)
        println(text)
        println(chatId)

        if (text == "Hello") TelegramBotService().sendMessage(botToken, chatId ?: return, "Hello")
    }
}