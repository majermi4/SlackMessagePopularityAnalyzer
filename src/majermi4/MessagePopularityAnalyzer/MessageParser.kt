package majermi4.MessagePopularityAnalyzer

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import majermi4.MessagePopularityAnalyzer.Model.Message
import majermi4.MessagePopularityAnalyzer.Model.Reaction
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL

/**
 * Allows parsing of slack messages for a specified channel.
 */
class MessageParser(val authToken: String)
{
    fun parseMessagesFromAllChannels() : MutableList<Message>
    {
        // Asynchronously parse messages from all channels
        val deferred = SlackChannel.values().map {
            GlobalScope.async {
                parseMessagesFromChannel(it)
            }
        }

        val messages = mutableListOf<Message>()
        // Combine all messages to a single list
        runBlocking {
            deferred.forEach { messages.addAll(it.await()) }
        }

        // Synchronous version of the above ..
        // SlackChannel.values().forEach { messages.addAll(parseMessagesFromChannel(it)) }

        return messages
    }

    fun parseMessagesFromChannel(channel: SlackChannel) : MutableList<Message>
    {
        if (MessagePersister.hasMessages(channel)) {
            println("Using cached messages for channel #${channel.getName()} ...")

            return MessagePersister.getMessages(channel)
        }

        println("Parsing messages for channel #${channel.getName()}")

        val parsedMessages = parseMessagesFromUrl(channel) { getMessagesApiUrl(channel, it) }

        val parsedReplies = mutableListOf<Message>()
        parsedMessages
            .filter { it.replyCount > 0 }
            .forEach { parentMessage ->
                parsedReplies.addAll(
                    parseMessagesFromUrl(channel) { getRepliesApiUrl(parentMessage.timestamp, channel, it)}
                )
            }

        parsedMessages.addAll(parsedReplies)
        val distinctParsedMessages = parsedMessages.distinctBy { it.timestamp }.toMutableList()

        MessagePersister.storeMessages(channel, distinctParsedMessages)

        return distinctParsedMessages
    }

    private fun parseMessagesFromUrl(channel: SlackChannel, getApiUrl: (nextCursor: String?) -> String): MutableList<Message>
    {
        val parsedMessages = mutableListOf<Message>()
        var nextCursor : String? = null

        do {
            val slackMessagesApiUrl = getApiUrl(nextCursor)

            try {
                val channelMessagesRawJson = URL(slackMessagesApiUrl).readText()

                val messagesJsonObject = JSONObject(channelMessagesRawJson);
                val messagesJsonArray = messagesJsonObject.getJSONArray("messages")

                parsedMessages.addAll(parseMessagesFromJson(messagesJsonArray, channel))

                nextCursor = parseNextCursor(messagesJsonObject)
            } catch (e : IOException) {
                println("IO exception: ${e.message}")
                Util.wait(15)
            }
        } while(nextCursor != null)

        return parsedMessages
    }

    private fun parseNextCursor(messagesJsonObject: JSONObject): String?
    {
        return try {
            messagesJsonObject.getJSONObject("response_metadata").getString("next_cursor")
        } catch (e : JSONException) {
            null
        }
    }

    private fun parseMessagesFromJson(messagesJsonArray: JSONArray, channel: SlackChannel) : List<Message>
    {
        val parsedMessages = mutableListOf<Message>()

        for (i in 0 until messagesJsonArray.length()) {
            val messageJsonObject = messagesJsonArray.getJSONObject(i)
            val parsedMessage = parseMessageFromJson(messageJsonObject, channel)

            parsedMessages.add(parsedMessage)
        }

        return parsedMessages
    }

    private fun parseMessageFromJson(messageJsonObject: JSONObject, channel: SlackChannel) : Message
    {
        val parsedReactions = mutableListOf<Reaction>()

        if (messageJsonObject.has("reactions")) {
            val reactionsJsonArray = messageJsonObject.getJSONArray("reactions")

            for (i in 0 until reactionsJsonArray.length()) {
                val reactionJsonObject = reactionsJsonArray.getJSONObject(i)
                val reactionName = reactionJsonObject.getString("name")
                val reactionCount = reactionJsonObject.getInt("count")

                val reactionUserIdsJsonArray = reactionJsonObject.getJSONArray("users")
                val reactionUserIds = mutableListOf<String>()
                for (j in 0 until reactionUserIdsJsonArray.length()) {
                    reactionUserIds.add(reactionUserIdsJsonArray[j].toString())
                }

                parsedReactions.add(Reaction(reactionName, reactionCount, reactionUserIds))
            }
        }

        val messageTimestamp = messageJsonObject.getString("ts");
        val messageText = messageJsonObject.getString("text")
        val authorId = if (messageJsonObject.has("user")) messageJsonObject.getString("user") else null
        val replyCount = if (messageJsonObject.has("reply_count")) messageJsonObject.getInt("reply_count") else 0
        val replyUsersCount = if (messageJsonObject.has("reply_users_count")) messageJsonObject.getInt("reply_users_count") else 0

        return Message(messageTimestamp, messageText, parsedReactions, authorId, replyCount, replyUsersCount, channel)
    }

    private fun getRepliesApiUrl(parentMessageTimestamp: String, channel: SlackChannel, nextCursor: String?): String
    {
        var repliesApiUrl = "https://slack.com/api/conversations.replies?token=$authToken&channel=${channel.id}&ts=$parentMessageTimestamp"

        if (nextCursor != null) {
            repliesApiUrl += "&cursor=$nextCursor"
        }

        return repliesApiUrl
    }

    private fun getMessagesApiUrl(channel : SlackChannel, nextCursor: String?): String
    {
        var messagesApiUrl = "https://slack.com/api/conversations.history?token=$authToken&channel=${channel.id}"

        if (nextCursor != null) {
            messagesApiUrl += "&cursor=$nextCursor"
        }

        return messagesApiUrl
    }
}
