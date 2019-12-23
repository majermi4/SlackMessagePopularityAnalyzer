package majermi4.MessagePopularityAnalyzer

import majermi4.MessagePopularityAnalyzer.Model.Message
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.Path

/**
 * Stores previously parsed messages to avoid excessive API calls to slack API.
 */
object MessagePersister
{
    fun storeMessages(channel : SlackChannel, messages : List<Message>)
    {
        // Serialization
        val fileOutputStream = FileOutputStream(fileNameForChannel(channel.id))
        val objectOutputStream = ObjectOutputStream(fileOutputStream)

        // Method for serialization of object
        objectOutputStream.writeObject(messages)

        objectOutputStream.close()
        fileOutputStream.close()
    }

    fun hasMessages(channel : SlackChannel) : Boolean
    {
        return Files.exists(Path.of(fileNameForChannel(channel.id)));
    }

    fun getMessages(channel : SlackChannel) : List<Message>
    {
        // Reading the object from a file
        val fileInputStream = FileInputStream(fileNameForChannel(channel.id))
        val objectInputStream = ObjectInputStream(fileInputStream)

        // Method for deserialization of object
        @Suppress("UNCHECKED_CAST")
        val messages  = objectInputStream.readObject() as List<Message>

        fileInputStream.close()
        objectInputStream.close()

        return messages
    }

    private fun fileNameForChannel(channelId : String) : String
    {
        return "#$channelId-messages";
    }
}