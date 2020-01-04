package majermi4.MessagePopularityAnalyzer.Model

import majermi4.MessagePopularityAnalyzer.SlackChannel
import java.io.Serializable

data class Message(
    val timestamp: String, // Slack message ID
    val text: String,
    val reactions: List<Reaction>,
    val authorId: String?,
    val replyCount : Int,
    val replyUsersCount: Int,
    val channel: SlackChannel

) : Serializable
{
    fun totalReactionCount() : Int
    {
        return reactions
            .fold(0, { totalEmojiCount, reaction -> totalEmojiCount + reaction.count })
    }

    fun emojiCount(emojiName: String) : Int
    {
        return reactions
            .filter { it.name.contains(emojiName) }
            .fold(0, { totalEmojiCount, reaction -> totalEmojiCount + reaction.count })
    }

    fun getUrlLink() : String
    {
        return "https://rohea.slack.com/archives/${channel.id}/p${timestamp.replace(".", "")}"
    }
}