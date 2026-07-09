package ru.basted.bspbuitesting.dynamic;

import java.util.regex.Pattern;

/**
 * Stateless-маскировка предсказуемо динамического контента (время, дата,
 * относительное время вида "5 минут назад"). Никакого состояния, никакого
 * xpath, никакой персистентности между прогонами — маска считается заново
 * на каждом чтении текста, поэтому одинаково применяется и при создании
 * baseline, и при каждой последующей сверке.
 * <p>
 * ВАЖНО: правила намеренно узкие (даты/время), а не "любые цифры" — иначе
 * легитимный статический текст вроде "Работаем с 9:00 до 18:00" тоже
 * замаскируется и перестанет проверяться.
 */
public final class DynamicContentMasker {
    private static final Pattern[] DYNAMIC_PATTERNS = {
            // 15:42 / 15:42:07
            Pattern.compile("\\d{1,2}:\\d{2}(:\\d{2})?"),
            // 08.07.2026 / 08/07/2026 / 2026-07-08
            Pattern.compile("\\d{1,2}[./]\\d{1,2}[./]\\d{2,4}|\\d{4}-\\d{2}-\\d{2}"),
            // "5 минут назад", "2 часа назад", "3 дня назад"
            Pattern.compile("\\d+\\s*(секунд\\w*|минут\\w*|час\\w*|день|дня|дней)\\s*назад"),
            // "8 июля 2026"
            Pattern.compile(
                    "(?i)\\b\\d{1,2}\\s+(январ\\w*|феврал\\w*|март\\w*|апрел\\w*|ма[йя]\\w*|июн\\w*|" +
                            "июл\\w*|август\\w*|сентябр\\w*|октябр\\w*|ноябр\\w*|декабр\\w*)\\s+\\d{4}\\b"
            ),
    };

    private DynamicContentMasker() {
    }

    public static String mask(final String rawText) {
        if (rawText == null || rawText.isEmpty()) {
            return rawText;
        }
        String result = rawText;
        for (final Pattern p : DYNAMIC_PATTERNS) {
            result = p.matcher(result).replaceAll("*");
        }
        // схлопываем "**" в "*", если несколько паттернов совпали рядом
        return result.replaceAll("\\*{2,}", "*");
    }
}