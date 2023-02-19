package pt.task1


import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Tests {

    @Test
    fun testTime() {
        assertThrows(IllegalArgumentException::class.java) { Time("19:62") }
        assertThrows(IllegalArgumentException::class.java) { Time("19.30") }
        assertThrows(IllegalArgumentException::class.java) { Time("24:19") }
        assertEquals(Time("04:20").minutes, 260)
        assertEquals(Time("00:00").minutes, 0)
    }

    @Test
    fun testTrain() {
        val stationsPool = List(10) { index -> "Station-${index + 1}" }
        val timePool = List(10) { index -> Time("00:${10 + index * 2}") }
        val thomas = TrainSchedule.Train(
            "Thomas",
            MutableList(10) { index -> TrainSchedule.Destination(timePool[index], stationsPool[index]) },
            Time("00:00")
        )

        assertThrows(NoSuchElementException()::class.java) { thomas.deleteStation("Non-existent station") }
        assertThrows(RuntimeException()::class.java) {
            TrainSchedule.Train(
                "Gordon",
                mutableListOf(TrainSchedule.Destination(Time("10:00"), "Only station")),
                Time("00:00")
            ).deleteStation("Only station")
        }
        thomas.addStation(TrainSchedule.Destination(Time("13:30"), "N"))
        assertEquals(11, thomas.size())
        thomas.deleteStation("N")
        assertEquals(10, thomas.size())
    }

    @Test
    fun testSchedule() {
        val stationsPool1 = List(10) { index -> "Station-${index + 1}" }
        val timePool1 = List(10) { index -> Time("00:${10 + index * 2}") }
        val stationsPool2 = List(10) { index -> "Station-${index * 2}" }
        val timePool2 = List(10) { index -> Time("06:${10 + index * 3}") }

        val thomas = TrainSchedule.Train(
            "Thomas",
            MutableList(10) { index -> TrainSchedule.Destination(timePool1[index], stationsPool1[index]) },
            Time("00:00")
        )

        val emily = TrainSchedule.Train(
            "Emily",
            MutableList(10) { index -> TrainSchedule.Destination(timePool2[index], stationsPool2[index]) },
            Time("06:00")
        )

        val gordon = TrainSchedule.Train(
            "Gordon",
            mutableListOf(TrainSchedule.Destination(Time("00:08"), "Station-1")),
            Time("00:05")
        )
        val stationSchedule = TrainSchedule(listOf(thomas, emily, gordon))

        assertEquals(                   //Thomas - 00:10; Emily - 06:10; Gordon - 00:08
            gordon,
            stationSchedule.nearestTrain()
        )
        assertEquals(                   //Thomas - 00:10; Emily - N/A; Gordon - 00:08
            gordon,
            stationSchedule.nearestByDestination("Station-1")
        )
        assertEquals(                   //Thomas - N/A; Emily - 06:37; Gordon - N/A
            emily,
            stationSchedule.nearestByDestination("Station-18")
        )
        assertEquals(emily, stationSchedule.nearestByTime(Time("05:00")))
        assertEquals(gordon, stationSchedule.nearestByTime(Time("00:04")))
    }

}