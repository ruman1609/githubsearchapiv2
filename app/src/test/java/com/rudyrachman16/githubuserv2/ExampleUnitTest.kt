package com.rudyrachman16.githubuserv2

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    enum class TEST {
        A, B
    }

    @Test
    fun addition_isCorrect() {
        val time = "16:20".split(":").toTypedArray()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time[0].toInt())
            set(Calendar.MINUTE, time[1].toInt())
        }
        val timee = "1/4/2021 16:20"
        val formatter = SimpleDateFormat("d/M/yyyy HH:mm")
        val date = formatter.parse(timee)
        assertEquals(calendar.timeInMillis, calendar.timeInMillis)
    }
}