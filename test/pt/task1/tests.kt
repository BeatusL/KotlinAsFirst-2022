package pt.task1


import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Tests {

    @Test
    fun testTime() {
        assertThrows(IllegalArgumentException::class.java) { Time.createTime("19:62") }
        assertThrows(IllegalArgumentException::class.java) { Time.createTime("19.30") }
        assertThrows(IllegalArgumentException::class.java) { Time.createTime("24:19") }
        assertEquals(Time.createTime("04:20").minutes, 260)
    }

}