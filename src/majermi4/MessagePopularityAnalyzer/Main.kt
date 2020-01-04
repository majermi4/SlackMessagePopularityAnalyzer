package majermi4.MessagePopularityAnalyzer

import java.io.File

fun main()
{
    val authToken = File("authToken").readText()

    val startTime: Long = System.currentTimeMillis()

    val messageParser = MessageParser(authToken)
    val userParser = UserParser(authToken)

    val messages = messageParser.parseMessagesFromAllChannels()

    messages.sortByGreatestUseOfEmoji("+1")
    //messages.sortByGreatestUseOfEmoji("-1")
    //messages.sortByTextLength();
    // TODO: Sort by fastest replies

    val endTime: Long = System.currentTimeMillis()

    val timeElapesed = (endTime - startTime)

    println("Done. Analyzed ${messages.count()} messages in $timeElapesed ms.")

    messages.subList(0, 5).forEach { popularMessage ->
        var authorId = "Unknown"
        var authorName = "Unknown"

        if (popularMessage.authorId != null) {
            val author = userParser.parseUser(popularMessage.authorId)
            authorId = author.userId
            authorName = author.userName
        }

        println("-----------------------------------------------------------")
        println("Message id: ${popularMessage.timestamp}")
        println("Message text: ${popularMessage.text}")
        println("User: $authorName (Id: $authorId)")
        println("Channel: ${popularMessage.channel.getName()}")
        println("Link: ${popularMessage.getUrlLink()}")
    }
}