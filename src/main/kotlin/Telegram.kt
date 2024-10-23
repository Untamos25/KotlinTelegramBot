fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0

    val updateIdToRegex: Regex = "\"update_id\":(.+?),".toRegex()
    val messageToRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdToRegex: Regex = "(\\d+?),\"first_name\"".toRegex()
    val dataToRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    val telegramBotService = TelegramBotService(botToken)
    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(2000)

        val updates: String = telegramBotService.getUpdates(updateId)
        println(updates)

        updateId = updateIdToRegex.find(updates)?.groups?.get(1)?.value?.toInt()?.plus(1) ?: continue
        val text = messageToRegex.find(updates)?.groups?.get(1)?.value
        val chatId = chatIdToRegex.find(updates)?.groups?.get(1)?.value?.toLongOrNull() ?: continue
        val data = dataToRegex.find(updates)?.groups?.get(1)?.value

        if (text?.lowercase() == "/start") telegramBotService.sendMenu(chatId)
        if (data?.lowercase() == STATISTICS_CALLBACK) telegramBotService.sendMessage(
            chatId,
            "Выучено ${trainer.getStatistics().numberOfLearnedWords} из ${trainer.getStatistics().sizeOfDictionary} |" +
                    " ${trainer.getStatistics().percentOfLearnedWords}%"
        )
    }
}