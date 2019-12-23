package majermi4.MessagePopularityAnalyzer

enum class SlackChannel
{
    RANDOM,
    LINKROLL,
    CONFERENCE,
    BACKEND,
    PACE,
    BUSINESS,
    SUURLAHETYSTO,
    HELOFFICE;

    companion object {
        fun getChannelId(channel: SlackChannel): String {
            return when (channel) {
                RANDOM -> "C03V4DD2L"
                LINKROLL -> "C04A5EQM8"
                CONFERENCE -> "CBBHM360M"
                BACKEND -> "C6AQBS2FL"
                PACE -> "C04A5ED0N"
                BUSINESS -> "C1KGU8X6C"
                SUURLAHETYSTO -> "C0NDEA4NB"
                HELOFFICE -> "C75SL59EC"
            }
        }

        fun getChannelName(channel: SlackChannel) = channel.toString().toLowerCase()
    }
}