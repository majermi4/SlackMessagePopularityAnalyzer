package majermi4.MessagePopularityAnalyzer

import java.util.concurrent.TimeUnit

object Util
{
    fun wait(seconds: Int)
    {
        println("Waiting for 15s before retrying ...")

        for (i in 1..seconds) {
            print(".")
            TimeUnit.SECONDS.sleep(1)
        }

        println("Done waiting.")
    }
}