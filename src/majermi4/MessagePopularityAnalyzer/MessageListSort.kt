package majermi4.MessagePopularityAnalyzer

import majermi4.MessagePopularityAnalyzer.Model.Message

fun MutableList<Message>.sortByPopularity() {
    this.sortByDescending { it.totalReactionCount() }
}

fun MutableList<Message>.sortByGreatestUseOfEmoji(emojiName: String) {
    this.sortByDescending { it.emojiCount(emojiName) }
}

fun MutableList<Message>.sortByReplyCount() {
    this.sortByDescending { it.replyCount }
}

fun MutableList<Message>.sortByUniqueUsersReplyCount() {
    this.sortByDescending { it.replyUsersCount }
}

fun MutableList<Message>.sortByTextLength() {
    this.sortByDescending { it.text.length }
}

//fun MutableList<Message>.sortByFastestReplyToParent() {
//    this.sortByDescending { it.length }
//}