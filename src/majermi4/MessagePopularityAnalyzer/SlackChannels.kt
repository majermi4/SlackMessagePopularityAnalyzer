package majermi4.MessagePopularityAnalyzer

object SlackChannels
{
    const val RANDOM = "random"
    const val LINKROLL = "linkroll"
    const val CONFERENCE = "conference"
    const val BACKEND = "backend"
    const val PACE = "pace"
    const val BUSINESS = "business"
    const val SUURLAHETYSTO = "suurlahetysto"
    const val HELOFFICE = "heloffice"

    private val CHANNEL_IDS = mapOf(
        RANDOM to "C03V4DD2L",
        LINKROLL to "C04A5EQM8",
        CONFERENCE to "CBBHM360M",
        BACKEND to "C6AQBS2FL",
        PACE to "C04A5ED0N",
        BUSINESS to "C1KGU8X6C",
        SUURLAHETYSTO to "C0NDEA4NB",
        HELOFFICE to "C75SL59EC"
    )

    fun getChannelId(channelName : String) : String
    {
        return CHANNEL_IDS[channelName] ?: error("Unknown channel name '$channelName'.")
    }

    fun getChannelName(channelId : String) : String
    {
        return CHANNEL_IDS.filterValues { value ->
            value == channelId
        }.keys.first()
    }
}