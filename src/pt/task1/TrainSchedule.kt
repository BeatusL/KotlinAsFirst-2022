package pt.task1

import java.lang.IllegalArgumentException

class TrainSchedule(trains: MutableList<Train>) {
    val trains = trains
}

class Destination(val time: Time, val station: String) {
}

class Time(private val time: String) {
    init {
        if (!time.matches(Regex("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]\$")))           // hh:mm
            throw IllegalArgumentException("Wrong time format")
    }

    private val timeL = time.split(":").map { it.toInt() }
    val minutes = timeL[0].toInt() * 60 + timeL[1]

    override fun toString() = time
}

class Train(val name: String, var destinations: MutableList<Destination>, val depTime: Time) {

    init {
        if (destinations.isEmpty()) throw IllegalArgumentException("Train without further destination")
    }

    val finalDestination = destinations.last()


}

