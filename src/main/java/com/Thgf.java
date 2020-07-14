package com;

import javax.swing.*;

public class Thgf {
    static int d, h1, h2, l;
//        float maxTrowPoint = (float) (maxH - Math.sqrt((maxH * maxH + d * d) + l));
//        float secondHUp = maxH - (maxTrowPoint - l);
//        float m = maxH * maxH - d * d - maxH + l * l;
//        double sqrt = Math.sqrt(1 + 4 * m);
//        double result1 = (sqrt + 1) / 2;
//        double result2 = (-sqrt + 1) / 2;
//        double maxTrowPoint;
//        if (result1 < 0) maxTrowPoint = result2;
//        else maxTrowPoint = result1;
//        double finalResult = minH + maxH - (maxTrowPoint - l);

    public static void main(String[] args) {
        input();
        int maxH = Math.max(h1, h2);
        int minH = Math.min(h1, h2);
        float step = 0.00001F;
        float b = 0;
        while (b - (maxH - b) <= l) b += step;
        float maxHUp = (float) (maxH - (maxH - Math.sqrt(d * d + Math.pow(maxH - b, 2))));
        JOptionPane.showMessageDialog(null,
                "Минимальное расстояние, на которое придётся ползти вверх: " + (minH + maxHUp),
                "Результат", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void input() {
        String str = JOptionPane.showInputDialog(null,
                "Введите четыре числа не превышающие 1000000 разделённые пробелом:",
                "Запрос", JOptionPane.QUESTION_MESSAGE);
        if (str == null) System.exit(0);
        if (!str.matches("(\\d{1,6}|1000000)" +
                "\\s+(\\d{1,6}|1000000)" +
                "\\s+(\\d{1,6}|1000000)" +
                "\\s+(\\d{1,6}|1000000)")) {
            int result = JOptionPane.showConfirmDialog(null,
                    "Введены неверные данные. Повторить?",
                    "Ошибка", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) input();
            else System.exit(0);
            return;
        }
        String[] massive = str.split("\\s+");
        d = Integer.parseInt(massive[0]);
        h1 = Integer.parseInt(massive[1]);
        h2 = Integer.parseInt(massive[2]);
        l = Integer.parseInt(massive[3]);
    }
}