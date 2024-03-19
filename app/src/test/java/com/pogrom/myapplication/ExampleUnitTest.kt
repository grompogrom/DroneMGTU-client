package com.pogrom.myapplication

import com.pogrom.myapplication.data.NetworkService
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*
import java.util.Timer
import java.util.TimerTask

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun api_test(){
        runBlocking {
            val res = NetworkService.serverDroneApi.connectDrones()
            println("$res")
        }
        assertEquals(1, 1)
    }

}
