package majermi4.MessagePopularityAnalyzer

import majermi4.MessagePopularityAnalyzer.Model.User
import org.json.JSONObject
import java.net.URL

class UserParser(val authToken: String)
{
    val cachedUsers = mutableMapOf<String,User>()

    fun parseUser(userId: String): User
    {
        if (cachedUsers.containsKey(userId)) {
            return cachedUsers.getValue(userId)
        }

        return parserUserFromApi(userId)
    }

    private fun parserUserFromApi(userId: String): User
    {
        val usersRawJson = URL(getUserApiUrl(userId)).readText()
        val usersJsonObject = JSONObject(usersRawJson)

        val userJsonObject = usersJsonObject.getJSONObject("user")

        val userName = userJsonObject.getString("name")

        return User(userId, userName)
    }

    private fun getUserApiUrl(userId: String): String
    {
        return "https://slack.com/api/users.info?token=$authToken&user=$userId"
    }
}