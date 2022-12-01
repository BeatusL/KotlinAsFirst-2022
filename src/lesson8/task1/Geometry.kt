@file:Suppress("UNUSED_PARAMETER")

package lesson8.task1

import lesson1.task1.sqr
import kotlin.math.*

// Урок 8: простые классы
// Максимальное количество баллов = 40 (без очень трудных задач = 11)

/**
 * Точка на плоскости
 */
data class Point(val x: Double, val y: Double) {
    /**
     * Пример
     *
     * Рассчитать (по известной формуле) расстояние между двумя точками
     */
    fun distance(other: Point): Double = sqrt(sqr(x - other.x) + sqr(y - other.y))

    fun isLess(other: Point): Boolean = (this.y < other.y) || (this.y == other.y && this.x < other.x)
}

/**
 * Треугольник, заданный тремя точками (a, b, c, см. constructor ниже).
 * Эти три точки хранятся в множестве points, их порядок не имеет значения.
 */
@Suppress("MemberVisibilityCanBePrivate")
class Triangle private constructor(private val points: Set<Point>) {

    private val pointList = points.toList()

    val a: Point get() = pointList[0]

    val b: Point get() = pointList[1]

    val c: Point get() = pointList[2]

    constructor(a: Point, b: Point, c: Point) : this(linkedSetOf(a, b, c))

    /**
     * Пример: полупериметр
     */
    fun halfPerimeter() = (a.distance(b) + b.distance(c) + c.distance(a)) / 2.0

    /**
     * Пример: площадь
     */
    fun area(): Double {
        val p = halfPerimeter()
        return sqrt(p * (p - a.distance(b)) * (p - b.distance(c)) * (p - c.distance(a)))
    }

    /**
     * Пример: треугольник содержит точку
     */
    fun contains(p: Point): Boolean {
        val abp = Triangle(a, b, p)
        val bcp = Triangle(b, c, p)
        val cap = Triangle(c, a, p)
        return abp.area() + bcp.area() + cap.area() <= area()
    }

    override fun equals(other: Any?) = other is Triangle && points == other.points

    override fun hashCode() = points.hashCode()

    override fun toString() = "Triangle(a = $a, b = $b, c = $c)"
}

/**
 * Окружность с заданным центром и радиусом
 */
data class Circle(val center: Point, val radius: Double) {
    /**
     * Простая (2 балла)
     *
     * Рассчитать расстояние между двумя окружностями.
     * Расстояние между непересекающимися окружностями рассчитывается как
     * расстояние между их центрами минус сумма их радиусов.
     * Расстояние между пересекающимися окружностями считать равным 0.0.
     */
    fun distance(other: Circle): Double = max(center.distance(other.center) - radius - other.radius, 0.0)

    /**
     * Тривиальная (1 балл)
     *
     * Вернуть true, если и только если окружность содержит данную точку НА себе или ВНУТРИ себя
     */
    fun contains(p: Point): Boolean = (center.distance(p) <= radius)
}

/**
 * Отрезок между двумя точками
 */
data class Segment(val begin: Point, val end: Point) {
    override fun equals(other: Any?) =
        other is Segment && (begin == other.begin && end == other.end || end == other.begin && begin == other.end)

    override fun hashCode() =
        begin.hashCode() + end.hashCode()
}

/**
 * Средняя (3 балла)
 *
 * Дано множество точек. Вернуть отрезок, соединяющий две наиболее удалённые из них.
 * Если в множестве менее двух точек, бросить IllegalArgumentException
 */
fun diameter(vararg points: Point): Segment {
    TODO()
}

fun sqDist(point1: Point, point2: Point) = (point1.x - point2.x) * (point1.x - point2.x) +
        (point1.y - point2.y) * (point1.y - point2.y)

fun orientation(a: Point, b: Point, c: Point): Int {
    val area = ((b.y - a.y) * (c.x - b.x) - (b.x - a.x) * (c.y - b.y))
    return when {
        (area > 0) -> 1
        (area < 0) -> 2
        else -> 0
    }
}

fun compare(point1: Point, point2: Point): Int {
    val point0 = Point(0.0, 0.0)
    val orientation = orientation(point0, point1, point2)
    return if (orientation == 0) {
        if (sqDist(point0, point2) >= sqDist(point0, point1)) -1
        else 1
    } else {
        val i = if (orientation == 2) -1
        else 1
        i
    }
}

fun convexHull(points: MutableList<Point>): Set<Point> {
    val point0 = Point(0.0, 0.0)
    var mPoint = Point(Double.MAX_VALUE, Double.MAX_VALUE)
    var index = -1
    for (i in points.indices) if (points[i].isLess(mPoint)) {
        mPoint = points[i]
        index = i
    }
    points[index] = points[0]
    points[0] = mPoint
    points.sortWith { point, point2 -> compare(point, point2) }

    val list = mutableListOf(points[0])
    for (i in 1 until points.size) {
        if ((i < points.size - 1) && (orientation(point0, points[i], points[i + 1]) == 0)) continue
        list.add(points[i])
    }
    println(list)
    if (list.size < 3) throw Exception(NoSuchElementException())

    val res = mutableListOf(points[0], points[1], points[2])
    for (i in 3 until list.size) {
        val n = res.size
        while (n > 1 && orientation(res[n - 2], res[n - 1], list[i]) != 2) res.removeLast()
        res.add(0, list[i])
    }
    return res.toSet()
}


/**
 * Простая (2 балла)
 *
 * Построить окружность по её диаметру, заданному двумя точками
 * Центр её должен находиться посередине между точками, а радиус составлять половину расстояния между ними
 */
fun circleByDiameter(diameter: Segment): Circle = Circle(
    Point(
        (diameter.begin.x + diameter.end.x) / 2.0,
        (diameter.begin.y + diameter.end.y) / 2.0
    ),
    sqrt((diameter.begin.x - diameter.end.x).pow(2) + (diameter.begin.y - diameter.end.y).pow(2)) / 2.0
)

/**
 * Прямая, заданная точкой point и углом наклона angle (в радианах) по отношению к оси X.
 * Уравнение прямой: (y - point.y) * cos(angle) = (x - point.x) * sin(angle)
 * или: y * cos(angle) = x * sin(angle) + b, где b = point.y * cos(angle) - point.x * sin(angle).
 * Угол наклона обязан находиться в диапазоне от 0 (включительно) до PI (исключительно).
 */
class Line private constructor(val b: Double, val angle: Double) {
    init {
        require(angle >= 0 && angle < PI) { "Incorrect line angle: $angle" }
    }

    constructor(point: Point, angle: Double) : this(point.y * cos(angle) - point.x * sin(angle), angle)

    /**
     * Средняя (3 балла)
     *
     * Найти точку пересечения с другой линией.
     * Для этого необходимо составить и решить систему из двух уравнений (каждое для своей прямой)
     */
    fun crossPoint(other: Line): Point = TODO()

    override fun equals(other: Any?) = other is Line && angle == other.angle && b == other.b

    override fun hashCode(): Int {
        var result = b.hashCode()
        result = 31 * result + angle.hashCode()
        return result
    }

    override fun toString() = "Line(${cos(angle)} * y = ${sin(angle)} * x + $b)"
}

/**
 * Средняя (3 балла)
 *
 * Построить прямую по отрезку
 */
fun lineBySegment(s: Segment): Line {
    val dx = s.end.x - s.begin.x
    val dy = s.end.y - s.begin.y
    val k = dy / dx
    return when {
        (dx == 0.0) -> Line(Point(s.begin.x, s.begin.y), PI / 2.0)
        (atan(k) < 0) -> Line(Point(s.begin.x, s.begin.y), atan(k) + PI)
        (atan(k) >= PI) -> Line(Point(s.begin.x, s.begin.y), atan(k) - PI)
        else -> Line(Point(s.begin.x, s.begin.y), atan(k))
    }
}

/**
 * Средняя (3 балла)
 *
 * Построить прямую по двум точкам
 */
fun lineByPoints(a: Point, b: Point): Line = lineBySegment(Segment(a, b))

/**
 * Сложная (5 баллов)
 *
 * Построить серединный перпендикуляр по отрезку или по двум точкам
 */
fun bisectorByPoints(a: Point, b: Point): Line {
    val dx = b.x - a.x
    val dy = b.y - a.y
    val angle = atan(-1 / (dy / dx))
    return when {
        (dx == 0.0) -> Line(Point(a.x + dx / 2.0, a.y + dy / 2.0), 0.0)
        (angle < 0) -> Line(Point(a.x + dx / 2.0, a.y + dy / 2.0), angle + PI)
        (angle >= PI) -> Line(Point(a.x + dx / 2.0, a.y + dy / 2.0), angle - PI)
        else -> Line(Point(a.x + dx / 2.0, a.y + dy / 2.0), angle)
    }
}

/**
 * Средняя (3 балла)
 *
 * Задан список из n окружностей на плоскости.
 * Найти пару наименее удалённых из них; расстояние между окружностями
 * рассчитывать так, как указано в Circle.distance.
 *
 * При наличии нескольких наименее удалённых пар,
 * вернуть первую из них по порядку в списке circles.
 *
 * Если в списке менее двух окружностей, бросить IllegalArgumentException
 */
fun findNearestCirclePair(vararg circles: Circle): Pair<Circle, Circle> = TODO()

/**
 * Сложная (5 баллов)
 *
 * Дано три различные точки. Построить окружность, проходящую через них
 * (все три точки должны лежать НА, а не ВНУТРИ, окружности).
 * Описание алгоритмов см. в Интернете
 * (построить окружность по трём точкам, или
 * построить окружность, описанную вокруг треугольника - эквивалентная задача).
 */
fun circleByThreePoints(a: Point, b: Point, c: Point): Circle {
    val ab = a.distance(b)
    val bc = b.distance(c)
    val ca = c.distance(a)
    val r = ab * bc * ca / sqrt((ab + bc + ca) * (-ab + bc + ca) * (ab - bc + ca) * (ab + bc - ca))
    return Circle(Point(0.0, 0.0), 0.0)
}

/**
 * Очень сложная (10 баллов)
 *
 * Дано множество точек на плоскости. Найти круг минимального радиуса,
 * содержащий все эти точки. Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит одну точку, вернуть круг нулевого радиуса с центром в данной точке.
 *
 * Примечание: в зависимости от ситуации, такая окружность может либо проходить через какие-либо
 * три точки данного множества, либо иметь своим диаметром отрезок,
 * соединяющий две самые удалённые точки в данном множестве.
 */
fun minContainingCircle(vararg points: Point): Circle = TODO()

