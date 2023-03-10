@file:Suppress("UNUSED_PARAMETER")

package lesson12.task1

import ru.spbstu.wheels.asList
import java.lang.IllegalArgumentException

/**
 * Класс "расписание поездов".
 *
 * Общая сложность задания -- средняя, общая ценность в баллах -- 16.
 * Объект класса хранит расписание поездов для определённой станции отправления.
 * Для каждого поезда хранится конечная станция и список промежуточных.
 * Поддерживаемые методы:
 * добавить новый поезд, удалить поезд,
 * добавить / удалить промежуточную станцию существующему поезду,
 * поиск поездов по времени.
 *
 * В конструктор передаётся название станции отправления для данного расписания.
 */
class TrainTimeTable(private val baseStationName: String) {
    private val mapOfTrains = mutableMapOf<String, Train>()

    /**
     * Добавить новый поезд.
     *
     * Если поезд с таким именем уже есть, следует вернуть false и ничего не изменять в таблице
     *
     * @param train название поезда
     * @param depart время отправления с baseStationName
     * @param destination конечная станция
     * @return true, если поезд успешно добавлен, false, если такой поезд уже есть
     */
    fun addTrain(train: String, depart: Time, destination: Stop): Boolean {
        return if (mapOfTrains[train] != null) {
            false
        } else {
            mapOfTrains[train] = Train(train, Stop(baseStationName, depart), destination)
            true
        }
    }

    /**
     * Удалить существующий поезд.
     *
     * Если поезда с таким именем нет, следует вернуть false и ничего не изменять в таблице
     *
     * @param train название поезда
     * @return true, если поезд успешно удалён, false, если такой поезд не существует
     */
    fun removeTrain(train: String): Boolean = mapOfTrains.remove(train) != null

    /**
     * Добавить/изменить начальную, промежуточную или конечную остановку поезду.
     *
     * Если у поезда ещё нет остановки с названием stop, добавить её и вернуть true.
     * Если stop.name совпадает с baseStationName, изменить время отправления с этой станции и вернуть false.
     * Если stop совпадает с destination данного поезда, изменить время прибытия на неё и вернуть false.
     * Если stop совпадает с одной из промежуточных остановок, изменить время прибытия на неё и вернуть false.
     *
     * Функция должна сохранять инвариант: время прибытия на любую из промежуточных станций
     * должно находиться в интервале между временем отправления с baseStation и временем прибытия в destination,
     * иначе следует бросить исключение IllegalArgumentException.
     * Также, время прибытия на любую из промежуточных станций не должно совпадать с временем прибытия на другую
     * станцию или с временем отправления с baseStation, иначе бросить то же исключение.
     *
     * @param train название поезда
     * @param stop начальная, промежуточная или конечная станция
     * @return true, если поезду была добавлена новая остановка, false, если было изменено время остановки на старой
     */
    fun addStop(train: String, stop: Stop): Boolean {
        mapOfTrains[train]?.inChecker(stop) ?: return false
        return if (mapOfTrains[train]!!.hashMapOfStops[stop.name] == null) {
            mapOfTrains[train]!!.apply {
                hashMapOfStops[stop.name] = stop
                sortedMapOfStops[stop.time] = stop
                stops.toMutableList().add(stop)
            }
            true
        } else {
            mapOfTrains[train]!!.changeTime(stop)
            false
        }
    }

    /**
     * Удалить одну из промежуточных остановок.
     *
     * Если stopName совпадает с именем одной из промежуточных остановок, удалить её и вернуть true.
     * Если у поезда нет такой остановки, или stopName совпадает с начальной или конечной остановкой, вернуть false.
     *
     * @param train название поезда
     * @param stopName название промежуточной остановки
     * @return true, если удаление успешно
     */
    fun removeStop(train: String, stopName: String): Boolean {
        return if (mapOfTrains[train]!!.sortedMapOfStops.values.last().name != stopName
            && mapOfTrains[train]!!.sortedMapOfStops.values.first().name != stopName
            && mapOfTrains[train]!!.hashMapOfStops[stopName] != null
        ) {
            mapOfTrains[train]!!.apply {
                sortedMapOfStops.remove(hashMapOfStops[stopName]!!.time)
                hashMapOfStops.remove(stopName)
            }
            true
        } else false
    }

    /**
     * Вернуть список всех поездов, упорядоченный по времени отправления с baseStationName
     */
    fun trains(): List<Train> = mapOfTrains.values.sortedBy { it.sortedMapOfStops.values.first().time }

    /**
     * Вернуть список всех поездов, отправляющихся не ранее currentTime
     * и имеющих остановку (начальную, промежуточную или конечную) на станции destinationName.
     * Список должен быть упорядочен по времени прибытия на станцию destinationName
     */
    fun trains(currentTime: Time, destinationName: String): List<Train> =
        mapOfTrains.values.filter { it.hashMapOfStops[destinationName] != null && it.sortedMapOfStops.values.first().time > currentTime }
            .sortedBy { _train -> _train.hashMapOfStops[destinationName]!!.time.minute }

    /**
     * Сравнение на равенство.
     * Расписания считаются одинаковыми, если содержат одинаковый набор поездов,
     * и поезда с тем же именем останавливаются на одинаковых станциях в одинаковое время.
     */
    override fun equals(other: Any?): Boolean {
        if (other !is TrainTimeTable || this.trains().size != other.trains().size) return false
        for (i in this.trains().indices) {
            if (this.trains()[i].name != other.trains()[i].name ||
                this.trains()[i].sortedStops() != other.trains()[i].sortedStops()
            )
                return false
        }
        return true
    }

    override fun hashCode(): Int = baseStationName.hashCode()

}

/**
 * Время (часы, минуты)
 */
data class Time(val hour: Int, val minute: Int) : Comparable<Time> {
    init {
        if (hour >= 24 || hour < 0 || minute >= 60 || minute < 0) throw IllegalArgumentException()
    }

    fun toMinutes() = hour * 60 + minute
    override fun toString() = String.format("%02d:%02d", hour, minute)

    /**
     * Сравнение времён на больше/меньше (согласно контракту compareTo)
     */
    override fun compareTo(other: Time): Int = this.toMinutes() - other.toMinutes()
}

/**
 * Остановка (название, время прибытия)
 */
data class Stop(val name: String, val time: Time) {
    override fun equals(other: Any?): Boolean = other is Stop && this.name == other.name
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + time.hashCode()
        return result
    }

}

/**
 * Поезд (имя, список остановок, упорядоченный по времени).
 * Первой идёт начальная остановка, последней конечная.
 */
data class Train(val name: String, var stops: List<Stop>) {
    constructor(name: String, vararg stops: Stop) : this(name, stops.asList())

    val hashMapOfStops = HashMap(stops.associateBy { it.name })
    val sortedMapOfStops = stops.associateBy { it.time }.toSortedMap(compareBy { it.toMinutes() })

    fun changeTime(stop: Stop) = apply {
        sortedMapOfStops.remove(hashMapOfStops[stop.name]!!.time)
        hashMapOfStops[stop.name] = stop
        sortedMapOfStops[stop.time] = stop
    }

    fun inChecker(stop: Stop) {
        val list = sortedMapOfStops.values.toList()
        if ((stop.name != list[0].name && stop.name != list.last().name &&
                    (stop.time <= list[0].time || stop.time >= list.last().time)) ||
            (stop.name == list[0].name && stop.time >= list[1].time) ||
            (stop.name == list.last().name && stop.time <= list[list.size - 2].time)
        )
            throw IllegalArgumentException()
        val s = sortedMapOfStops[stop.time]
        if (s != null && s != stop && s.time == stop.time
        ) throw IllegalArgumentException()
    }

    fun sortedStops() = Train(name, stops.sortedBy { it.time.toMinutes() })

    override fun equals(other: Any?): Boolean = (other is Train && this.name == other.name) ||
            (other is String && this.name == other)

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + stops.hashCode()
        return result
    }

}