package pt.task1

import java.lang.IllegalArgumentException

class TrainSchedule {
}

class Destination private constructor(val time: Time, val station: String) {
    companion object {
        fun createDest(time: Time, station: String) = Destination(time, station)
    }
}

class Time private constructor(private val time: String) {

    init {
        if (!time.matches(Regex("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]\$")))           // hh:mm
            throw IllegalArgumentException("Wrong time format")
    }

    private val timeL = time.split(":").map { it.toInt() }
    val minutes = timeL[0].toInt() * 60 + timeL[1]

    override fun toString() = time

    companion object {
        fun createTime(str: String) = Time(str)
    }
}


