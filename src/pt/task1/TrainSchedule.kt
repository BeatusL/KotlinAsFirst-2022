package pt.task1


import java.lang.IllegalArgumentException

class TrainSchedule(private val trains: List<Train>) {

    class Destination(val time: Time, val name: String) {
        override fun toString() = "$name - $time"
    }

    class Train(val name: String, destinations: MutableList<Destination>, val depTime: Time) {
        val destinations: MutableList<Destination>
        fun size() = destinations.size


        init {
            if (destinations.isEmpty()) throw IllegalArgumentException("Train without further destination")
            this.destinations = destinations.sortedBy { it.time.minutes }.toMutableList()
            if (depTime.minutes > destinations[0].time.minutes)       //Train reaches another station before leaving current
                throw RuntimeException("Time paradox detected")
        }


        fun addStation(station: Destination) {
            destinations.add(station)
            destinations.sortBy { it.time.minutes }
            println("Added successfully")
        }

        fun deleteStation(station: String) {
            if (destinations.size != 1) {
                for (x in destinations) if (x.name == station) {
                    destinations.remove(x)
                    println("Deleted successfully")
                    return
                }
            } else {
                throw RuntimeException(
                    "This station is the only purpose in the life of this train,/n" +
                            "do not deprive his life of meaning plz"
                )
            }
            throw NoSuchElementException("No such station")
        }

        fun indexOf(station: String): Int? {
            for (x in destinations.indices) if (destinations[x].name == station) return x
            return null
        }

        override fun toString(): String = this.name
    }

    fun nearestTrain() =
        trains.minBy { it.destinations[0].time.minutes }            // Which one reaches another station sooner

    fun nearestByDestination(station: String) =                     // Which one reaches that particular station sooner
        trains.filter { it.indexOf(station) != null }.minBy { it.destinations[it.indexOf(station)!!].time.minutes }

    fun nearestByTime(time: Time) =                             // Which one leaves closer to the appointed time
        trains.filter { it.depTime.minutes >= time.minutes }.minBy { it.depTime.minutes }

}

class Time(private val time: String) {
    init {
        if (!time.matches(Regex("^([01][0-9]|2[0-3]):[0-5][0-9]\$")))           // hh:mm
            throw IllegalArgumentException("Wrong time format")
    }

    private val timeL = time.split(":").map { it.toInt() }
    val minutes = timeL[0] * 60 + timeL[1]

    override fun toString() = time
}

