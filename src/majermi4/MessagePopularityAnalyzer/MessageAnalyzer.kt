package majermi4.MessagePopularityAnalyzer

import majermi4.MessagePopularityAnalyzer.Model.Message

object MessageAnalyzer
{
    fun findPopular(messages : List<Message>, popularityThreshold : Int) : List<Message>
    {
        val popularMessages = mutableListOf<Message>()

        for (message in messages) {
            if (message.totalReactionCount() >= popularityThreshold) {
                popularMessages.add(message)
            }
        }

        return popularMessages
    }
}