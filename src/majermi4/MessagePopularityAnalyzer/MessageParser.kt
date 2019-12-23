package majermi4.MessagePopularityAnalyzer

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
    fun execute(channel: SlackChannel) : List<Message>
    {
        if (MessagePersister.hasMessages(channel)) {
            println("Using cached messages for channel #${channel.id} ...")

            return MessagePersister.getMessages(channel)
        }

        val parsedMessages = mutableListOf<Message>()
        var nextCursor : String? = null

        do {
            val slackMessagesApiUrl = getMessagesApiUrl(channel, nextCursor)

            try {
                val channelMessagesRawJson = URL(slackMessagesApiUrl).readText()

                val messagesJsonObject = JSONObject(channelMessagesRawJson);
                val messagesJsonArray = messagesJsonObject.getJSONArray("messages")

                parsedMessages.addAll(parseMessages(messagesJsonArray))

                nextCursor = parseNextCursor(messagesJsonObject)
            } catch (e : IOException) {
                println("IO exception: ${e.message}")
                Util.wait(15)
            }
        } while(nextCursor != null)

        MessagePersister.storeMessages(channel, parsedMessages)

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

    private fun parseMessages(messagesJsonArray : JSONArray) : List<Message>
    {
        val parsedMessages = mutableListOf<Message>()

        for (i in 0 until messagesJsonArray.length()) {
            val messageJsonObject = messagesJsonArray.getJSONObject(i)
            val parsedMessage = parseMessage(messageJsonObject)

            parsedMessages.add(parsedMessage)
        }

        return parsedMessages
    }

    private fun parseMessage(messageJsonObject: JSONObject) : Message
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

        val messageText = messageJsonObject.getString("text")
        val authorId = if (messageJsonObject.has("user")) messageJsonObject.getString("user") else null
        val replyCount = if (messageJsonObject.has("reply_count")) messageJsonObject.getInt("reply_count") else 0
        val replyUsersCount = if (messageJsonObject.has("reply_users_count")) messageJsonObject.getInt("reply_users_count") else 0

        return Message(messageText, parsedReactions, authorId, replyCount, replyUsersCount)
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
