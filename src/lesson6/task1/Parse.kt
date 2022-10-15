@file:Suppress("UNUSED_PARAMETER", "ConvertCallChainIntoSequence")

package lesson6.task1

import lesson2.task2.daysInMonth
import java.lang.IllegalArgumentException
import java.lang.IndexOutOfBoundsException

// Урок 6: разбор строк, исключения
// Максимальное количество баллов = 13
// Рекомендуемое количество баллов = 11
// Вместе с предыдущими уроками (пять лучших, 2-6) = 40/54

/**
 * Пример
 *
 * Время представлено строкой вида "11:34:45", содержащей часы, минуты и секунды, разделённые двоеточием.
 * Разобрать эту строку и рассчитать количество секунд, прошедшее с начала дня.
 */
fun timeStrToSeconds(str: String): Int {
    val parts = str.split(":")
    var result = 0
    for (part in parts) {
        val number = part.toInt()
        result = result * 60 + number
    }
    return result
}

/**
 * Пример
 *
 * Дано число n от 0 до 99.
 * Вернуть его же в виде двухсимвольной строки, от "00" до "99"
 */
fun twoDigitStr(n: Int) = if (n in 0..9) "0$n" else "$n"

/**
 * Пример
 *
 * Дано seconds -- время в секундах, прошедшее с начала дня.
 * Вернуть текущее время в виде строки в формате "ЧЧ:ММ:СС".
 */
fun timeSecondsToStr(seconds: Int): String {
    val hour = seconds / 3600
    val minute = (seconds % 3600) / 60
    val second = seconds % 60
    return String.format("%02d:%02d:%02d", hour, minute, second)
}

/**
 * Пример: консольный ввод
 */
fun main() {
    println("Введите время в формате ЧЧ:ММ:СС")
    val line = readLine()
    if (line != null) {
        val seconds = timeStrToSeconds(line)
        if (seconds == -1) {
            println("Введённая строка $line не соответствует формату ЧЧ:ММ:СС")
        } else {
            println("Прошло секунд с начала суток: $seconds")
        }
    } else {
        println("Достигнут <конец файла> в процессе чтения строки. Программа прервана")
    }
}


/**
 * Средняя (4 балла)
 *
 * Дата представлена строкой вида "15 июля 2016".
 * Перевести её в цифровой формат "15.07.2016".
 * День и месяц всегда представлять двумя цифрами, например: 03.04.2011.
 * При неверном формате входной строки вернуть пустую строку.
 *
 * Обратите внимание: некорректная с точки зрения календаря дата (например, 30.02.2009) считается неверными
 * входными данными.
 */
fun dateStrToDigit(str: String): String {
    var res: String
    try {
        val months = mapOf(
            "января" to "01", "февраля" to "02", "марта" to "03",
            "апреля" to "04", "мая" to "05", "июня" to "06",
            "июля" to "07", "августа" to "08", "сентября" to "09",
            "октября" to "10", "ноября" to "11", "декабря" to "12"
        )
        val parts = str.split(" ")
        if (parts.size != 3 || check(parts[0].toInt(), months[parts[1]], parts[2].toInt()))
            throw NumberFormatException()
        res = String.format("%02d.${months[parts[1]]}.${parts[2].toInt()}", parts[0].toInt())
    } catch (e: NumberFormatException) {
        res = ""
    }
    return res
}

fun check(day: Int, month: String?, year: Int): Boolean =
    month == null || day > daysInMonth(month.toInt(), year)


/**
 * Средняя (4 балла)
 *
 * Дата представлена строкой вида "15.07.2016".
 * Перевести её в строковый формат вида "15 июля 2016".
 * При неверном формате входной строки вернуть пустую строку
 *
 * Обратите внимание: некорректная с точки зрения календаря дата (например, 30 февраля 2009) считается неверными
 * входными данными.
 */
fun dateDigitToStr(digital: String): String {
    try {
        val months = mapOf(
            "01" to "января", "02" to "февраля", "03" to "марта",
            "04" to "апреля", "05" to "мая", "06" to "июня",
            "07" to "июля", "08" to "августа", "09" to "сентября",
            "10" to "октября", "11" to "ноября", "12" to "декабря"
        )
        val parts = digital.split(".")
        if (parts.size != 3 || months[parts[1]] == null || check(parts[0].toInt(), parts[1], parts[2].toInt()))
            throw NumberFormatException()
        return String.format("%d ${months[parts[1]]} %d", parts[0].toInt(), parts[2].toInt())
    } catch (e: NumberFormatException) {
        return ""
    }
}

/**
 * Средняя (4 балла)
 *
 * Номер телефона задан строкой вида "+7 (921) 123-45-67".
 * Префикс (+7) может отсутствовать, код города (в скобках) также может отсутствовать.
 * Может присутствовать неограниченное количество пробелов и чёрточек,
 * например, номер 12 --  34- 5 -- 67 -89 тоже следует считать легальным.
 * Перевести номер в формат без скобок, пробелов и чёрточек (но с +), например,
 * "+79211234567" или "123456789" для приведённых примеров.
 * Все символы в номере, кроме цифр, пробелов и +-(), считать недопустимыми.
 * При неверном формате вернуть пустую строку.
 *
 * PS: Дополнительные примеры работы функции можно посмотреть в соответствующих тестах.
 */
fun flattenPhoneNumber(phone: String): String {
    var ans = ""
    val symbols = setOf('(', ' ', ')', '-')
    val numsAndPlus = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+')
    for (i in phone.indices) {
        val n = phone[i]
        when {
            n in numsAndPlus -> ans += n
            n == '(' && chFlPhNum(phone, i) -> return ""
            (n !in symbols) -> return ""
        }
    }
    return ans
}

fun chFlPhNum(phone: String, c: Int): Boolean {
    var n = c
    val nums = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    while (phone[n] != ')' && n + 1 < phone.length) {
        n++
        if (phone[n] in nums) return false
    }
    return true
}

/**
 * Средняя (5 баллов)
 *
 * Результаты спортсмена на соревнованиях в прыжках в длину представлены строкой вида
 * "706 - % 717 % 703".
 * В строке могут присутствовать числа, черточки - и знаки процента %, разделённые пробелами;
 * число соответствует удачному прыжку, - пропущенной попытке, % заступу.
 * Прочитать строку и вернуть максимальное присутствующее в ней число (717 в примере).
 * При нарушении формата входной строки или при отсутствии в ней чисел, вернуть -1.
 */
fun bestLongJump(jumps: String): Int {
    if (!(jumps.matches(Regex("([0-9]+|[%-] )*[0-9]+|[%-]")))) return -1
    val results = jumps.split(' ').filter { it != "%" && it != "-" }.map { it.toInt() }
    return results.max()

}

/**
 * Сложная (6 баллов)
 *
 * Результаты спортсмена на соревнованиях в прыжках в высоту представлены строкой вида
 * "220 + 224 %+ 228 %- 230 + 232 %%- 234 %".
 * Здесь + соответствует удачной попытке, % неудачной, - пропущенной.
 * Высота и соответствующие ей попытки разделяются пробелом.
 * Прочитать строку и вернуть максимальную взятую высоту (230 в примере).
 * При нарушении формата входной строки, а также в случае отсутствия удачных попыток,
 * вернуть -1.
 */
fun bestHighJump(jumps: String): Int {
    if (!(jumps.matches(Regex("([0-9]+ [+%-]+ )*[0-9]+ [+%-]+")))) return -1
    val h = mutableListOf<Int>()
    val results = jumps.split(' ')
    for (i in results.indices)
        if (i % 2 == 0 && "+" in results[i + 1]) h.add(results[i].toInt())
    if (h.isEmpty()) return -1
    return h.max()
}


/**
 * Сложная (6 баллов)
 *
 * В строке представлено выражение вида "2 + 31 - 40 + 13",
 * использующее целые положительные числа, плюсы и минусы, разделённые пробелами.
 * Наличие двух знаков подряд "13 + + 10" или двух чисел подряд "1 2" не допускается.
 * Вернуть значение выражения (6 для примера).
 * Про нарушении формата входной строки бросить исключение IllegalArgumentException
 */
fun plusMinus(expression: String): Int {
    if (!(expression.matches(Regex("([0-9]+ [+-] )*[0-9]+")))) throw IllegalArgumentException()
    val exp = expression.split(" ")
    var res = exp[0].toInt()
    for (i in 1 until exp.size step 2) {
        val num = exp[i + 1]
        when {
            exp[i] == "+" -> res += num.toInt()
            exp[i] == "-" -> res -= num.toInt()
        }
    }
    return res
}

/**
 * Сложная (6 баллов)
 *
 * Строка состоит из набора слов, отделённых друг от друга одним пробелом.
 * Определить, имеются ли в строке повторяющиеся слова, идущие друг за другом.
 * Слова, отличающиеся только регистром, считать совпадающими.
 * Вернуть индекс начала первого повторяющегося слова, или -1, если повторов нет.
 * Пример: "Он пошёл в в школу" => результат 9 (индекс первого 'в')
 */
fun firstDuplicateIndex(str: String): Int {
    val list = str.split(" ").map { it.uppercase() }
    var flag = -1
    var res = -1
    for (i in 0 until list.size - 1) {
        if (list[i] == list[i + 1]) flag = i
    }
    if (flag > -1) {
        res = 0
        for (i in 0 until flag) res += list[i].length + 1
    }
    return res
}

/**
 * Сложная (6 баллов)
 *
 * Строка содержит названия товаров и цены на них в формате вида
 * "Хлеб 39.9; Молоко 62; Курица 184.0; Конфеты 89.9".
 * То есть, название товара отделено от цены пробелом,
 * а цена отделена от названия следующего товара точкой с запятой и пробелом.
 * Вернуть название самого дорогого товара в списке (в примере это Курица),
 * или пустую строку при нарушении формата строки.
 * Все цены должны быть больше нуля либо равны нулю.
 */
fun mostExpensive(description: String): String {
    if (description.isEmpty()) return ""
    val list = description.split("; ")
    var res = ""
    var maxPrice = -1.0
    for (element in list) {
        val price = element.split(" ").last().toDouble()
        when {
            price < 0 -> return ""
            price > maxPrice -> {
                res = element.split(" ").first()
                maxPrice = price
            }
        }
    }
    return res
}

/**
 * Сложная (6 баллов)
 *
 * Перевести число roman, заданное в римской системе счисления,
 * в десятичную систему и вернуть как результат.
 * Римские цифры: 1 = I, 4 = IV, 5 = V, 9 = IX, 10 = X, 40 = XL, 50 = L,
 * 90 = XC, 100 = C, 400 = CD, 500 = D, 900 = CM, 1000 = M.
 * Например: XXIII = 23, XLIV = 44, C = 100
 *
 * Вернуть -1, если roman не является корректным римским числом
 */
fun fromRoman(roman: String): Int {
    if (roman.isEmpty()) return -1
    val tsd = listOf("C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM")
    val tfd = listOf("X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")
    val tzd = listOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")
    var res = 0
    var fl = 0
    var i = 0
    while (i < roman.length) {
        when {

            roman[i] == 'M' && fl == 0 -> {
                res += 1000
                i++
            }

            (roman[i] == 'C' || roman[i] == 'D') && fl == 0 -> {
                var j = if (roman.length > i + 4) i + 4 else roman.length
                while (roman.substring(i, j) !in tsd && j != i) {
                    j--
                }
                if (i != j) {
                    res += (tsd.indexOf(roman.substring(i, j)) + 1) * 100
                    fl = 1
                    i = j
                } else return -1
            }

            (roman[i] == 'X' || roman[i] == 'L') && fl < 2 -> {
                var j = if (roman.length > i + 4) i + 4 else roman.length
                while (roman.substring(i, j) !in tfd && j != i) {
                    j--
                }
                if (i != j) {
                    res += (tfd.indexOf(roman.substring(i, j)) + 1) * 10
                    fl = 2
                    i = j
                } else return -1
            }

            (roman[i] == 'I' || roman[i] == 'V') && fl < 3 -> {
                var j = if (roman.length > i + 4) i + 4 else roman.length
                while (roman.substring(i, j) !in tzd && j != i) {
                    j--
                }
                if (i != j) {
                    res += tzd.indexOf(roman.substring(i, j)) + 1
                    fl = 3
                    i = j
                } else return -1
            }

            else -> return -1
        }
    }
    return res
}

/**
 * Очень сложная (7 баллов)
 *
 * Имеется специальное устройство, представляющее собой
 * конвейер из cells ячеек (нумеруются от 0 до cells - 1 слева направо) и датчик, двигающийся над этим конвейером.
 * Строка commands содержит последовательность команд, выполняемых данным устройством, например +>+>+>+>+
 * Каждая команда кодируется одним специальным символом:
 *	> - сдвиг датчика вправо на 1 ячейку;
 *  < - сдвиг датчика влево на 1 ячейку;
 *	+ - увеличение значения в ячейке под датчиком на 1 ед.;
 *	- - уменьшение значения в ячейке под датчиком на 1 ед.;
 *	[ - если значение под датчиком равно 0, в качестве следующей команды следует воспринимать
 *  	не следующую по порядку, а идущую за соответствующей следующей командой ']' (с учётом вложенности);
 *	] - если значение под датчиком не равно 0, в качестве следующей команды следует воспринимать
 *  	не следующую по порядку, а идущую за соответствующей предыдущей командой '[' (с учётом вложенности);
 *      (комбинация [] имитирует цикл)
 *  пробел - пустая команда
 *
 * Изначально все ячейки заполнены значением 0 и датчик стоит на ячейке с номером N/2 (округлять вниз)
 *
 * После выполнения limit команд или всех команд из commands следует прекратить выполнение последовательности команд.
 * Учитываются все команды, в том числе несостоявшиеся переходы ("[" при значении под датчиком не равном 0 и "]" при
 * значении под датчиком равном 0) и пробелы.
 *
 * Вернуть список размера cells, содержащий элементы ячеек устройства после завершения выполнения последовательности.
 * Например, для 10 ячеек и командной строки +>+>+>+>+ результат должен быть 0,0,0,0,0,1,1,1,1,1
 *
 * Все прочие символы следует считать ошибочными и формировать исключение IllegalArgumentException.
 * То же исключение формируется, если у символов [ ] не оказывается пары.
 * Выход за границу конвейера также следует считать ошибкой и формировать исключение IllegalStateException.
 * Считать, что ошибочные символы и непарные скобки являются более приоритетной ошибкой чем выход за границу ленты,
 * то есть если в программе присутствует некорректный символ или непарная скобка, то должно быть выброшено
 * IllegalArgumentException.
 * IllegalArgumentException должен бросаться даже если ошибочная команда не была достигнута в ходе выполнения.
 *
 */
fun computeDeviceCells(cells: Int, commands: String, limit: Int): List<Int> {
    val c = commands.toMutableList()
    val res = MutableList(cells) { 0 }
    var i = cells / 2
    var j = 0
    var left = limit
    if (!commandsCheck(c)) throw IllegalArgumentException()
    while (left > 0 && j < c.size) {
        when {
            i < 0 || i + 1 > res.size -> throw IllegalStateException()
            c[j] == '>' -> i++
            c[j] == '<' -> i--
            c[j] == '+' -> res[i]++
            c[j] == '-' -> res[i]--
            c[j] == '[' -> {
                if (res[i] == 0) {
                    var k = j
                    while (c[k] != ']') k++
                    j = k
                }
            }
            c[j] == ']' -> {
                if (res[i] != 0) {
                    var k = j
                    var counter = 0
                    while (c[k] != '[' || counter != 1) {
                        when {
                            c[k] == ']' -> counter++
                            c[k] == '[' -> counter--
                        }
                        k--
                    }
                    j = k
                }
            }
        }
        j++
        left--
    }
    return res
}

fun commandsCheck(commands: List<Char>): Boolean {
    val legalSymbols = listOf('>', '<', '+', '-', '[', ']', ' ')
    var c = 0
    for (element in commands) {
        when {
            element == '[' -> c++
            element == ']' -> c--
            c < 0 -> return false
        }
    }
    return c == 0 && commands.filter { it in legalSymbols }.size == commands.size
}
