/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Определяет, как именно шанс (процент) влияет на генерацию числа из диапазона [min..max].
 */
public enum QuantityDistribution {
    /**
     * <b>Плавное притяжение к краю (по умолчанию).</b>
     * <p>
     * Шанс работает как «магнит»:
     * <ul>
     *   <li>Низкий шанс (1–15%) — числа почти всегда выпадают около <b>min</b>.</li>
     *   <li>Средний шанс (50%) — равномерный случайный разброс по всему диапазону.</li>
     *   <li>Высокий шанс (85–99%) — числа почти всегда выпадают около <b>max</b>.</li>
     * </ul>
     * <i>Пример для 10-100 при 5%:</i> броски будут давать 10, 11, 10, 13, 10, 15 (с мизерным шансом выбить 100).<br>
     * <b>Где использовать:</b> сила атаки, качество добытой руды, количество базовых ресурсов.
     */
    SMOOTH_BIAS {
        @Override
        public double roll(Random random, double low, double high, double biasPercent) {
            if (low == high) return low;
            double p = clampPercent(biasPercent);
            if (p <= 0.0) return low;
            if (p >= 1.0) return high;

            double u      = random.nextDouble();
            double factor = Math.pow(u, (1.0 - p) / p);
            return low + (high - low) * factor;
        }
    },

    /**
     * <b>Горка / Пик на проценте.</b>
     * <p>
     * Шанс задаёт вершину «горы». Чаще всего выпадает число ровно на отметке X% от диапазона,
     * а чем дальше число от этой отметки (хоть в большую, хоть в меньшую сторону), тем реже оно выпадает.
     * <p>
     * <i>Пример для 10-100 при 20%:</i> вершина приходится на ~28. Числа около 28 будут выпадать
     * чаще всего, а 10 и 100 — крайне редко.<br>
     * <b>Где использовать:</b> разброс температуры печи, время задержки механизмов,
     * когда результат должен колебаться около желаемого ориентира.
     */
    PEAK_AT_PERCENT {
        @Override
        public double roll(Random random, double low, double high, double biasPercent) {
            if (low == high) return low;
            double p = clampPercent(biasPercent);
            if (p <= 0.0) return low;
            if (p >= 1.0) return high;

            double u = random.nextDouble();
            double factor = (u < p)
                    ? Math.sqrt(u * p)
                    : 1.0 - Math.sqrt((1.0 - u) * (1.0 - p));
            return low + (high - low) * factor;
        }
    },

    /**
     * <b>Кучный разброс (Мишень).</b>
     * <p>
     * Моделирует попадание по мишени: значения ложатся очень плотным кучным облаком
     * вокруг точки процента. Выбросы на края диапазона практически исключены.
     * <p>
     * <i>Пример для 10-100 при 50%:</i> подавляющее большинство значений выпадет строго в коридоре 45–65.<br>
     * <b>Где использовать:</b> разброс пуль/стрел, параметры здоровья спавнящихся мобов,
     * скорость передвижения сущностей.
     */
    BELL_CLUSTER {
        @Override
        public double roll(Random random, double low, double high, double biasPercent) {
            if (low == high) return low;
            double p = clampPercent(biasPercent);
            if (p <= 0.0) return low;
            if (p >= 1.0) return high;

            double target = low + (high - low) * p;
            double spread = (high - low) / 6.0;
            double val    = target + random.nextGaussian() * spread;
            return Math.max(low, Math.min(high, val));
        }
    },

    /**
     * <b>Шанс или ноль (Классический дроп).</b>
     * <p>
     * Проверяет шанс как отдельное событие «всё или ничего»:
     * <ul>
     *   <li>Не повезло — возвращает <b>0</b>.</li>
     *   <li>Повезло — выдаёт обычное случайное число от <b>min</b> до <b>max</b>.</li>
     * </ul>
     * <i>Пример для 10-100 при 10%:</i> в 90% случаев выпадет 0. В оставшихся 10% выпадет случайное число от 10 до 100.<br>
     * <b>Где использовать:</b> лут с мобов (выпал предмет или нет), шанс спавна сундука.
     */
    CHANCE_OR_ZERO {
        @Override
        public double roll(Random random, double low, double high, double biasPercent) {
            if (low == high) return low;
            double p = clampPercent(biasPercent);
            if (p <= 0.0) return 0.0;
            if (p < 1.0 && random.nextDouble() > p) return 0.0;
            return low + random.nextDouble() * (high - low);
        }

        @Override
        public int rollInt(Random random, int low, int high, double biasPercent) {
            int min = Math.min(low, high);
            int max = Math.max(low, high);
            if (min == max) return min;

            double p = clampPercent(biasPercent);
            if (p <= 0.0) return 0;
            if (p < 1.0 && random.nextDouble() > p) return 0;
            return min + random.nextInt(max - min + 1);
        }

        @Override
        public long rollLong(Random random, long low, long high, double biasPercent) {
            long min = Math.min(low, high);
            long max = Math.max(low, high);
            if (min == max) return min;

            double p = clampPercent(biasPercent);
            if (p <= 0.0) return 0L;
            if (p < 1.0 && random.nextDouble() > p) return 0L;
            return min + (long) (random.nextDouble() * (max - min + 1));
        }
    },

    /**
     * <b>Чистый случайный выбор (Шанс игнорируется).</b>
     * <p>
     * Обычный честный рандом между min и max с одинаковой вероятностью для всех чисел.
     * На значение процента алгоритм не обращает внимания.
     * <p>
     * <b>Где использовать:</b> когда нужен просто случайный диапазон без всяких смещений и вероятностей.
     */
    FLAT_RANDOM {
        @Override
        public double roll(Random random, double low, double high, double biasPercent) {
            if (low == high) return low;
            return low + random.nextDouble() * (high - low);
        }

        @Override
        public int rollInt(Random random, int low, int high, double biasPercent) {
            int min = Math.min(low, high);
            int max = Math.max(low, high);
            if (min == max) return min;
            return min + random.nextInt(max - min + 1);
        }

        @Override
        public long rollLong(Random random, long low, long high, double biasPercent) {
            long min = Math.min(low, high);
            long max = Math.max(low, high);
            if (min == max) return min;
            return min + (long) (random.nextDouble() * (max - min + 1));
        }
    };

    public abstract double roll(Random random, double low, double high, double biasPercent);

    public double roll(Random random, double low, double high)      { return roll(random, low, high, 50.0); }
    public double roll(double low, double high, double biasPercent) { return roll(ThreadLocalRandom.current(), low, high, biasPercent); }
    public double roll(double low, double high)                     { return roll(ThreadLocalRandom.current(), low, high, 50.0); }

    public long rollLong(Random random, long low, long high, double biasPercent) {
        long min = Math.min(low, high);
        long max = Math.max(low, high);
        if (min == max) return min;
        return Math.round(roll(random, (double) min, (double) max, biasPercent));
    }

    public long rollLong(long low, long high, double biasPercent) { return rollLong(ThreadLocalRandom.current(), low, high, biasPercent); }
    public long rollLong(long low, long high)                     { return rollLong(ThreadLocalRandom.current(), low, high, 50.0); }

    public int rollInt(Random random, int low, int high, double biasPercent) {
        int min = Math.min(low, high);
        int max = Math.max(low, high);
        if (min == max) return min;
        return (int) Math.round(roll(random, min, max, biasPercent));
    }

    public int rollInt(int low, int high, double biasPercent) { return rollInt(ThreadLocalRandom.current(), low, high, biasPercent); }
    public int rollInt(int low, int high)                     { return rollInt(ThreadLocalRandom.current(), low, high, 50.0); }

    private static double clampPercent(double percent)        { return Math.max(0.0, Math.min(100.0, percent)) / 100.0; }
}