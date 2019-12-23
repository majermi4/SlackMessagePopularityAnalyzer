package majermi4.MessagePopularityAnalyzer.Model

import java.io.Serializable

data class Message(
    val text: String,
    val reactions: List<Reaction>,
    val authorId: String?,
    val replyCount : Int,
    val replyUsersCount: Int

) : Serializable
{
    fun totalReactionCount() : Int
    {
        var totalReactionCount = 0

        for (reaction in reactions) {
            totalReactionCount += reaction.count
        }

        return totalReactionCount
    }
}