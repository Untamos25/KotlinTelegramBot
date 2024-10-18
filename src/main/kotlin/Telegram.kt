fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0

    val updateIdToRegex: Regex = "\"update_id\":(.+?),".toRegex()
    val messageToRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdToRegex: Regex = "(\\d+?),\"first_name\"".toRegex()

    val telegramBotService = TelegramBotService(botToken)

    while (true) {
        Thread.sleep(2000)

        val updates: String = telegramBotService.getUpdates(updateId)
        println(updates)

        var matchResult: MatchResult? = updateIdToRegex.find(updates)
        updateId = matchResult?.groups?.get(1)?.value?.toInt()?.plus(1) ?: continue

        matchResult = messageToRegex.find(updates)
        val text = matchResult?.groups?.get(1)?.value

        matchResult = chatIdToRegex.find(updates)
        val chatId = matchResult?.groups?.get(1)?.value?.toLong()

        println(updateId)
        println(text)
        println(chatId)

        if (chatId != null && text == "Hello") telegramBotService.sendMessage(chatId, "Hello")
    }
}