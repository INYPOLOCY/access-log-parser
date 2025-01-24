import java.io.*;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.println("Введите путь к файлу:");
                String path = consoleReader.readLine();
                File file = new File(path);

                if (!file.exists() || file.isDirectory()) {
                    System.out.println("Указанный путь неверен: файл не существует или это директория.");
                    continue;
                }

                processFile(file);
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка ввода-вывода: " + e.getMessage());
        }
    }

    private static void processFile(File file) {
        Statistics statistics = new Statistics();
        int totalLines = 0;
        int googlebotCount = 0;
        int yandexBotCount = 0;

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                totalLines++;
                try {
                    LogEntry logEntry = new LogEntry(line);
                    statistics.addEntry(logEntry);

                    String userAgent = logEntry.getUserAgent().getFullUserAgent();
                    if (userAgent.contains("Googlebot")) {
                        googlebotCount++;
                    } else if (userAgent.contains("YandexBot")) {
                        yandexBotCount++;
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка разбора строки: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }

        // Вывод статистики
        System.out.println("Общее количество строк: " + totalLines);
        System.out.println("Количество запросов от Googlebot: " + googlebotCount);
        System.out.println("Количество запросов от YandexBot: " + yandexBotCount);

        if (totalLines > 0) {
            System.out.printf("Доля запросов от Googlebot: %.2f%%%n", (googlebotCount * 100.0 / totalLines));
            System.out.printf("Доля запросов от YandexBot: %.2f%%%n", (yandexBotCount * 100.0 / totalLines));
        }

        System.out.println("Общий объем трафика: " + statistics.getTotalTraffic());
        System.out.printf("Средний объем трафика за час: %.2f%n", statistics.getTrafficRate());

        System.out.println("Существующие страницы с кодом 200:");
        for (String page : statistics.getExistingPages()) {
            System.out.println(page);
        }

        System.out.println("Несуществующие страницы с кодом 404:");
        for (String page : statistics.getNonExistentPages()) {
            System.out.println(page);

        }

        System.out.println("Статистика по операционным системам:");
        Map<String, Integer> osFrequency = statistics.getOsFrequency();
        for (Map.Entry<String, Integer> entry : osFrequency.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("Статистика браузеров:");
        for (Map.Entry<String, Double> entry : statistics.getBrowserStatistics().entrySet()) {
            System.out.printf("%s: %.2f%%%n", entry.getKey(), entry.getValue() * 100);


        }
    }
}