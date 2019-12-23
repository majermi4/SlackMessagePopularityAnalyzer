package majermi4.MessagePopularityAnalyzer.Model

import java.io.Serializable

data class Reaction(
    val name: String,
    val count: Int,
    val userIds: List<String>
) : Serializable