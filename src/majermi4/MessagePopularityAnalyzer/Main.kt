package majermi4.MessagePopularityAnalyzer

fun main()
{
    val authToken = "XXX"
    val channelName = SlackChannels.RANDOM
    val channelId = SlackChannels.getChannelId(channelName)

    val messageParser = MessageParser(authToken)
    val messages = messageParser.execute(channelId)

    val popularMessages = MessageAnalyzer.findPopular(messages, 6)

    println("Done. Analyzed ${messages.count()} messages in #$channelName channel. Found ${popularMessages.count()} popular messages.")

    popularMessages.forEachIndexed { idx, popularMessage ->
        println("Popular message #$idx text: ${popularMessage.text}")
    }
}