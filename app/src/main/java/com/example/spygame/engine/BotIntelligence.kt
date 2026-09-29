package com.example.spygame.engine

import com.example.spygame.model.LocationItem
import com.example.spygame.model.Player
import com.example.spygame.model.Role

object BotIntelligence {

    fun generateQuestion(asker: Player, target: Player, location: LocationItem?): String {
        return if (asker.role == Role.CIVILIAN && location != null) {
            generateCivilianQuestion(location)
        } else {
            generateSpyQuestion()
        }
    }

    fun generateAnswer(target: Player, question: String, location: LocationItem?): String {
        return if (target.role == Role.CIVILIAN && location != null) {
            generateCivilianAnswer(question, location)
        } else {
            generateSpyAnswer(question)
        }
    }

    private fun generateCivilianQuestion(location: LocationItem): String {
        val name = location.name.lowercase()

        // 1. Water / Beach / Marine
        if (name.contains("пляж") || name.contains("море") || name.contains("лайнер") ||
            name.contains("бассейн") || name.contains("порт") || name.contains("аквапарк")) {
            return listOf(
                "Нужно ли там надевать плавки или купальник?",
                "Там легко промокнуть или испачкаться в песке?",
                "Связано ли это место с водой и отдыхом?",
                "Зависит ли комфорт от хорошей солнечной погоды?",
                "Можно ли там услышать шум волн или чаек?"
            ).random()
        }

        // 2. Transport & Movement
        if (name.contains("самолет") || name.contains("самолёт") || name.contains("поезд") ||
            name.contains("метро") || name.contains("автобус") || name.contains("вокзал") ||
            name.contains("аэропорт") || name.contains("такси") || name.contains("машина")) {
            return listOf(
                "Это место движется или стоит неподвижно на земле?",
                "Нужен ли туда билет или посадочный талон?",
                "Берут ли туда с собой чемодан или дорожную сумку?",
                "Объявляют ли там остановки или отправления по громкой связи?",
                "Можно ли там укачаться во время движения?"
            ).random()
        }

        // 3. Cultural & Educational
        if (name.contains("библиотека") || name.contains("школа") || name.contains("университет") ||
            name.contains("музей") || name.contains("театр") || name.contains("церковь")) {
            return listOf(
                "Требуется ли там соблюдать строгую тишину?",
                "Туда приходят ради знаний, культуры или веры?",
                "Есть ли там гардероб или смотрители в залах?",
                "Можно ли там встретить людей с книгами или конспектами?",
                "Там обычно говорят шёпотом или вслух?"
            ).random()
        }

        // 4. Medical
        if (name.contains("больница") || name.contains("клиника") || name.contains("поликлиника") ||
            name.contains("аптека") || name.contains("врач")) {
            return listOf(
                "Туда приходят по радостному поводу или по необходимости?",
                "Носят ли люди там белые халаты или перчатки?",
                "Пахнет ли там специфическими лекарствами?",
                "Выдают ли там справки или рецепты?",
                "Есть ли там приёмные часы и очереди?"
            ).random()
        }

        // 5. Entertainment, Nightlife & Fun
        if (name.contains("кино") || name.contains("цирк") || name.contains("клуб") ||
            name.contains("казино") || name.contains("аттракцион") || name.contains("стадион")) {
            return listOf(
                "Там обычно темно и играет громкая музыка?",
                "Берут ли там попкорн или напитки во время сеанса?",
                "Часто ли там аплодируют артистам или игрокам?",
                "Туда чаще ходят по выходным ради развлечения?",
                "Там проверяют билеты на входе?"
            ).random()
        }

        // 6. Food & Drinks
        if (name.contains("ресторан") || name.contains("кафе") || name.contains("пицца") ||
            name.contains("супермаркет") || name.contains("рынок") || name.contains("столовая")) {
            return listOf(
                "Приносят ли там меню с ценами?",
                "Пахнет ли там вкусной горячей едой?",
                "Платят ли там после того, как всё съели?",
                "Есть ли там столики и официанты?",
                "Можно ли там взять что-то с собой на вынос?"
            ).random()
        }

        // 7. Nature & Mountains
        if (name.contains("гора") || name.contains("лес") || name.contains("кемпинг") ||
            name.contains("курорт") || name.contains("зоопарк") || name.contains("ферма")) {
            return listOf(
                "Нужна ли для этого места тёплая или спортивная одежда?",
                "Можно ли там встретить диких или домашних животных?",
                "Там есть асфальт или кругом природа?",
                "Опасно ли там заблудиться или травмироваться?",
                "Там дышится свежим воздухом?"
            ).random()
        }

        // 8. Cities
        if (name.contains("москва") || name.contains("париж") || name.contains("лондон") ||
            name.contains("рим") || name.contains("токио") || name.contains("петербург")) {
            return listOf(
                "Нужен ли загранпаспорт, чтобы туда попасть?",
                "Много ли там иностранных туристов с камерами?",
                "Говорят ли местные жители на русском языке?",
                "Славятся ли эти места древними достопримечательностями?",
                "Летают ли туда прямые авиарейсы?"
            ).random()
        }

        // General smart contextual fallback for any civilian location
        return listOf(
            "Бывает ли там шумно в разгар дня?",
            "Часто ли обычный человек посещает такое место?",
            "Оставляют ли там деньги или это бесплатно?",
            "Есть ли там дресс-код или пускают в чём угодно?",
            "Можно ли там провести целый день и не заметить времени?",
            "Приходят ли туда семьями с детьми?"
        ).random()
    }

    private fun generateSpyQuestion(): String {
        // Subtle probing questions from a spy who doesn't know the location
        return listOf(
            "Там чаще находятся на открытом воздухе или внутри помещения?",
            "Бывают ли там дети и пожилые люди?",
            "Много ли денег обычно люди тратят в таком месте?",
            "Нужно ли туда надевать специальную или нарядную одежду?",
            "Там шумно и оживлённо или скорее спокойно?",
            "Это место предназначено для развлечений или для серьёзных дел?",
            "Ты лично часто там бываешь в течение года?",
            "Туда обычно приходят в одиночку или в компании друзей?",
            "Зависит ли посещение этого места от времени года?",
            "Там проводят больше двух часов подряд?"
        ).random()
    }

    private fun generateCivilianAnswer(question: String, location: LocationItem): String {
        val q = question.lowercase()
        val loc = location.name.lowercase()

        // Noise / Music / Sound
        if (q.contains("шум") || q.contains("тиш") || q.contains("звук") || q.contains("музык")) {
            return if (loc.contains("библиотек") || loc.contains("музей") || loc.contains("храм") || loc.contains("больниц")) {
                "Там стараются соблюдать тишину и не шуметь."
            } else if (loc.contains("клуб") || loc.contains("стадион") || loc.contains("цирк") || loc.contains("концерт")) {
                "О да, там бывает невероятно громко и шумно!"
            } else {
                "Уровень шума вполне умеренный, как обычно."
            }
        }

        // Money / Price / Cost
        if (q.contains("деньг") || q.contains("плат") || q.contains("стои") || q.contains("билет") || q.contains("бесплатн")) {
            return if (loc.contains("лес") || loc.contains("парк") || loc.contains("пляж") || loc.contains("улиц")) {
                "Обычно это бесплатно или стоит совсем символически."
            } else if (loc.contains("казино") || loc.contains("ресторан") || loc.contains("курорт") || loc.contains("лайнер")) {
                "Да, там можно оставить весьма приличную сумму."
            } else {
                "Там требуются билеты или стандартная плата за вход."
            }
        }

        // Weather / Temperature / Water
        if (q.contains("погод") || q.contains("тепл") || q.contains("холод") || q.contains("вод") || q.contains("промок")) {
            return if (loc.contains("пляж") || loc.contains("бассейн") || loc.contains("лайнер") || loc.contains("море")) {
                "Да, вода и тепло имеют ключевое значение!"
            } else if (loc.contains("горнолыж") || loc.contains("каток")) {
                "Там довольно морозно, нужно одеваться тепло."
            } else {
                "Там внутри комфортная температура круглый год."
            }
        }

        // Clothes / Dress code
        if (q.contains("одежд") || q.contains("надев") || q.contains("костюм") || q.contains("обув") || q.contains("дресс")) {
            return if (loc.contains("театр") || loc.contains("ресторан") || loc.contains("казино")) {
                "Лучше одеться поприличнее и нарядно."
            } else if (loc.contains("пляж") || loc.contains("бассейн") || loc.contains("спорт")) {
                "Одежда максимально лёгкая или спортивная."
            } else {
                "Обычная повседневная одежда, без строгих требований."
            }
        }

        // Frequency / Personal visit
        if (q.contains("част") || q.contains("быва") || q.contains("ходил")) {
            return listOf(
                "Бываю там по необходимости или по настроению.",
                "Не скажу, что каждый день, но место мне хорошо знакомо.",
                "Иногда заглядываю, вполне привычная обстановка."
            ).random()
        }

        // Kids / Families
        if (q.contains("дет") || q.contains("семь") || q.contains("ребен")) {
            return if (loc.contains("клуб") || loc.contains("казино") || loc.contains("тюрьм")) {
                "Нет, детям там точно делать нечего."
            } else if (loc.contains("цирк") || loc.contains("зоопарк") || loc.contains("парк") || loc.contains("кино")) {
                "Да, там всегда полно детей и семей!"
            } else {
                "Бывают, но это не главное предназначение места."
            }
        }

        // Smart general civilian reply
        return listOf(
            "Да, в этом месте именно так всё и устроено.",
            "Вполне типично для такой обстановки.",
            "Смотря с какой стороны посмотреть, но в целом соглашусь.",
            "Для тех, кто там бывал, это очевидная вещь."
        ).random()
    }

    private fun generateSpyAnswer(question: String): String {
        // Evasive, plausible spy answers that don't reveal ignorance
        return listOf(
            "Смотря в какой день и в какой компании туда приходить.",
            "Всё очень индивидуально, бывает по-разному.",
            "Я думаю, у каждого тут свой личный опыт.",
            "Вполне обычно, ничего сверхъестественного.",
            "Как и в большинстве подобных мест, без сюрпризов.",
            "Зависит от времени года и настроения."
        ).random()
    }
}
