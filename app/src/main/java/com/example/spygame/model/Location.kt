package com.example.spygame.model

data class LocationItem(
    val name: String,
    val synonyms: List<String> = emptyList()
) {
    fun matches(input: String): Boolean {
        val clean = normalize(input)
        if (clean.isBlank()) return false
        if (normalize(name) == clean) return true
        return synonyms.any { normalize(it) == clean }
    }

    companion object {
        fun normalize(value: String): String {
            return value.lowercase()
                .replace('ё', 'е')
                .replace(Regex("[^\\p{L}\\p{N}\\s]"), " ")
                .replace(Regex("\\s+"), " ")
                .trim()
        }
    }
}

data class LocationCategory(
    val id: String,
    val name: String,
    val icon: String,
    val items: List<LocationItem>
)

object DefaultLocationsPack {
    val categories: List<LocationCategory> = listOf(
        LocationCategory(
            id = "places",
            name = "Места",
            icon = "🏖️",
            items = listOf(
                LocationItem("Пляж", listOf("море", "побережье")),
                LocationItem("Космос", listOf("космическая станция", "мкс")),
                LocationItem("Цирк", listOf("цирковое представление")),
                LocationItem("Кинотеатр", listOf("кино")),
                LocationItem("Зоопарк"),
                LocationItem("Казино"),
                LocationItem("Парк аттракционов", listOf("луна-парк", "аттракционы")),
                LocationItem("Рынок", listOf("базар")),
                LocationItem("Стадион"),
                LocationItem("Порт"),
                LocationItem("Ферма"),
                LocationItem("Кемпинг", listOf("палаточный лагерь")),
                LocationItem("Горнолыжный курорт", listOf("горнолыжка")),
                LocationItem("Ночной клуб", listOf("клуб", "дискотека")),
                LocationItem("Спа-салон", listOf("спа", "баня", "сауна")),
                LocationItem("Круизный лайнер", listOf("круиз"))
            )
        ),
        LocationCategory(
            id = "buildings",
            name = "Здания",
            icon = "🏛️",
            items = listOf(
                LocationItem("Больница", listOf("госпиталь", "клиника", "поликлиника")),
                LocationItem("Школа"),
                LocationItem("Библиотека"),
                LocationItem("Банк"),
                LocationItem("Музей"),
                LocationItem("Театр"),
                LocationItem("Университет", listOf("институт")),
                LocationItem("Церковь", listOf("храм", "собор")),
                LocationItem("Тюрьма", listOf("колония")),
                LocationItem("Вокзал", listOf("железнодорожный вокзал")),
                LocationItem("Небоскрёб", listOf("небоскреб")),
                LocationItem("Замок"),
                LocationItem("Маяк"),
                LocationItem("Пожарная станция", listOf("пожарная часть")),
                LocationItem("Отель", listOf("гостиница")),
                LocationItem("Аэропорт"),
                LocationItem("Ресторан"),
                LocationItem("Супермаркет", listOf("магазин"))
            )
        ),
        LocationCategory(
            id = "cities",
            name = "Города",
            icon = "🌆",
            items = listOf(
                LocationItem("Москва"),
                LocationItem("Париж"),
                LocationItem("Лондон"),
                LocationItem("Нью-Йорк"),
                LocationItem("Токио"),
                LocationItem("Венеция"),
                LocationItem("Рим"),
                LocationItem("Дубай"),
                LocationItem("Санкт-Петербург", listOf("питер")),
                LocationItem("Стамбул"),
                LocationItem("Барселона"),
                LocationItem("Пекин"),
                LocationItem("Каир"),
                LocationItem("Рио-де-Жанейро", listOf("рио")),
                LocationItem("Сингапур")
            )
        ),
        LocationCategory(
            id = "professions",
            name = "Профессии",
            icon = "🧑‍🍳",
            items = listOf(
                LocationItem("Повар", listOf("шеф-повар")),
                LocationItem("Врач", listOf("доктор")),
                LocationItem("Полицейский", listOf("полисмен")),
                LocationItem("Пилот", listOf("лётчик", "летчик")),
                LocationItem("Учитель", listOf("преподаватель")),
                LocationItem("Пожарный"),
                LocationItem("Космонавт", listOf("астронавт")),
                LocationItem("Программист", listOf("разработчик")),
                LocationItem("Официант"),
                LocationItem("Парикмахер", listOf("стилист")),
                LocationItem("Таксист", listOf("водитель такси")),
                LocationItem("Архитектор"),
                LocationItem("Журналист"),
                LocationItem("Юрист", listOf("адвокат")),
                LocationItem("Ветеринар"),
                LocationItem("Строитель")
            )
        ),
        LocationCategory(
            id = "items",
            name = "Предметы",
            icon = "🎒",
            items = listOf(
                LocationItem("Зонт", listOf("зонтик")),
                LocationItem("Часы"),
                LocationItem("Зеркало"),
                LocationItem("Ключ"),
                LocationItem("Чемодан"),
                LocationItem("Рюкзак"),
                LocationItem("Очки"),
                LocationItem("Гитара"),
                LocationItem("Книга"),
                LocationItem("Свеча"),
                LocationItem("Телескоп"),
                LocationItem("Швейная машинка"),
                LocationItem("Компас"),
                LocationItem("Молоток"),
                LocationItem("Утюг")
            )
        ),
        LocationCategory(
            id = "food",
            name = "Еда",
            icon = "🍕",
            items = listOf(
                LocationItem("Пицца"),
                LocationItem("Суши", listOf("роллы")),
                LocationItem("Борщ"),
                LocationItem("Мороженое", listOf("пломбир")),
                LocationItem("Шашлык"),
                LocationItem("Пельмени"),
                LocationItem("Бургер", listOf("гамбургер")),
                LocationItem("Торт"),
                LocationItem("Оливье", listOf("салат оливье")),
                LocationItem("Блины"),
                LocationItem("Паста", listOf("спагетти")),
                LocationItem("Шоколад"),
                LocationItem("Арбуз"),
                LocationItem("Кофе"),
                LocationItem("Попкорн")
            )
        ),
        LocationCategory(
            id = "transport",
            name = "Транспорт",
            icon = "🚆",
            items = listOf(
                LocationItem("Автомобиль", listOf("машина", "авто")),
                LocationItem("Самолёт", listOf("самолет")),
                LocationItem("Поезд"),
                LocationItem("Метро", listOf("метрополитен")),
                LocationItem("Велосипед"),
                LocationItem("Такси"),
                LocationItem("Вертолёт", listOf("вертолет")),
                LocationItem("Корабль", listOf("пароход")),
                LocationItem("Автобус"),
                LocationItem("Трамвай"),
                LocationItem("Самокат"),
                LocationItem("Воздушный шар", listOf("аэростат")),
                LocationItem("Подводная лодка", listOf("субмарина")),
                LocationItem("Мотоцикл"),
                LocationItem("Канатная дорога", listOf("фуникулёр"))
            )
        ),
        LocationCategory(
            id = "events",
            name = "Мероприятия",
            icon = "🎉",
            items = listOf(
                LocationItem("Свадьба"),
                LocationItem("День рождения"),
                LocationItem("Концерт"),
                LocationItem("Футбольный матч", listOf("футбол")),
                LocationItem("Выпускной"),
                LocationItem("Новый год"),
                LocationItem("Экзамен"),
                LocationItem("Ярмарка"),
                LocationItem("Фестиваль"),
                LocationItem("Собеседование"),
                LocationItem("Пикник"),
                LocationItem("Олимпиада"),
                LocationItem("Карнавал"),
                LocationItem("Вечеринка"),
                LocationItem("Свидание")
            )
        ),
        LocationCategory(
            id = "animals",
            name = "Животные",
            icon = "🦒",
            items = listOf(
                LocationItem("Слон"),
                LocationItem("Пингвин"),
                LocationItem("Кошка", listOf("кот")),
                LocationItem("Собака", listOf("пёс", "пес")),
                LocationItem("Дельфин"),
                LocationItem("Обезьяна"),
                LocationItem("Лев"),
                LocationItem("Акула"),
                LocationItem("Попугай"),
                LocationItem("Медведь"),
                LocationItem("Жираф"),
                LocationItem("Лошадь", listOf("конь")),
                LocationItem("Сова"),
                LocationItem("Кенгуру"),
                LocationItem("Черепаха")
            )
        ),
        LocationCategory(
            id = "tech",
            name = "Техника",
            icon = "📱",
            items = listOf(
                LocationItem("Смартфон", listOf("телефон")),
                LocationItem("Ноутбук"),
                LocationItem("Телевизор"),
                LocationItem("Холодильник"),
                LocationItem("Микроволновка", listOf("микроволновая печь")),
                LocationItem("Дрон", listOf("квадрокоптер")),
                LocationItem("Робот-пылесос", listOf("пылесос")),
                LocationItem("Стиральная машина", listOf("стиралка")),
                LocationItem("Фотоаппарат"),
                LocationItem("Наушники"),
                LocationItem("Принтер"),
                LocationItem("Игровая приставка", listOf("приставка")),
                LocationItem("Умные часы", listOf("смарт-часы")),
                LocationItem("Кондиционер"),
                LocationItem("3D-принтер")
            )
        ),
        LocationCategory(
            id = "nature",
            name = "Природа",
            icon = "🌋",
            items = listOf(
                LocationItem("Вулкан"),
                LocationItem("Водопад"),
                LocationItem("Пустыня"),
                LocationItem("Джунгли"),
                LocationItem("Океан"),
                LocationItem("Горы"),
                LocationItem("Пещера"),
                LocationItem("Северный полюс"),
                LocationItem("Остров"),
                LocationItem("Лес"),
                LocationItem("Болото"),
                LocationItem("Саванна"),
                LocationItem("Айсберг"),
                LocationItem("Коралловый риф")
            )
        )
    )
}
