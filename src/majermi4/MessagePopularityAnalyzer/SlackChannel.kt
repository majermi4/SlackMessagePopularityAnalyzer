package majermi4.MessagePopularityAnalyzer

enum class SlackChannel(val id: String)
{
    RANDOM("C03V4DD2L"),
    LINKROLL("C04A5EQM8"),
    CONFERENCE("CBBHM360M"),
    BACKEND("C6AQBS2FL"),
    PACE("C04A5ED0N"),
    BUSINESS("C1KGU8X6C"),
    SUURLAHETYSTO("C0NDEA4NB"),
    HELOFFICE("C75SL59EC");

    fun getName() = this.toString().toLowerCase()
}