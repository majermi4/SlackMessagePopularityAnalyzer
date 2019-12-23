package majermi4.MessagePopularityAnalyzer

fun main()
{
    val authToken = "XXX"
    val channel = SlackChannel.RANDOM

    val messageParser = MessageParser(authToken)
    val messages = messageParser.execute(channel)

    val popularMessages = MessageAnalyzer.findPopular(messages, 6)

    val channelName = SlackChannel.getChannelName(channel)
    println("Done. Analyzed ${messages.count()} messages in #$channelName channel. Found ${popularMessages.count()} popular messages.")

    popularMessages.forEachIndexed { idx, popularMessage ->
        println("Popular message #$idx text: ${popularMessage.text}")
    }
}