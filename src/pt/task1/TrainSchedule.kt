package pt.task1


import java.lang.IllegalArgumentException

class TrainSchedule(private val trains: MutableList<Train>) {

    class Destination(val time: Time, val name: String) {
        override fun toString() = "$name - $time"
    }

    class Time(private val time: String) {
        init {
            if (!time.matches(Regex("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]\$")))           // hh:mm
                throw IllegalArgumentException("Wrong time format")
        }

        private val timeL = time.split(":").map { it.toInt() }
        val minutes = timeL[0] * 60 + timeL[1]

        override fun toString() = time
    }

    class Train(val name: String, destinations: MutableList<Destination>, val depTime: Time) {
        val destinations: MutableList<Destination>

        init {
            if (destinations.isEmpty()) throw IllegalArgumentException("Train without further destination")
            this.destinations = destinations.sortedBy { it.time.minutes }.toMutableList()
            if (depTime.minutes > destinations[0].time.minutes)       //Train reaches another station before leaving current
                throw RuntimeException("Time paradox detected")
        }

        //val finalDestination = destinations.last()

        fun addStation(station: Destination) {
            destinations.add(station)
            destinations.sortBy { it.time.minutes }
            println("Station added successfully")
        }

        fun deleteStation(station: String) {
            if (destinations.size != 1) {
                for (x in destinations) if (x.name == station) {
                    destinations.remove(x)
                    println("Deleted successfully")
                    break
                }
            } else {
                throw RuntimeException(
                    "This station is the only purpose in the life of this train,/n" +
                            "do not deprive his life of meaning plz"
                )
            }
            throw NoSuchElementException("No such station")
        }

        fun contains(station: String): Boolean {
            for (x in destinations) if (x.name == station) return true
            return false
        }

        override fun toString(): String = this.name
    }

    fun nearestTrain() = trains.minBy { it.destinations[0].time.minutes }

    fun nearestByDestination(station: String) =
        trains.filter { it.contains(station) }.minBy { it.destinations[0].time.minutes }

    fun nearestByTime(time: Time) {
        trains.filter { it.depTime.minutes > time.minutes }.minBy { it.destinations[0].time.minutes }
    }

}

