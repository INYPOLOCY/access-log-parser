import java.io.*;

public class Main {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.println("Введите путь к файлу:");
                String path = reader.readLine();
                File file = new File(path);

                if (!file.exists() || file.isDirectory()) {
                    System.out.println("Указанный путь неверен: файл не существует или это директория.");
                    continue;
                }

                processFile(file);
            }
        } catch (IOException ignored) {
        }
    }

    private static void processFile(File file) throws IOException {
        int totalLines = 0;
        int googlebotCount = 0;
        int yandexBotCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                totalLines++;
                if (line.length() > 1024) {
                    throw new LineTooLongException("Длина строки превышает 1024 символа!");
                }

                String userAgent = extractUserAgent(line);
                if (userAgent.contains("Googlebot")) {
                    googlebotCount++;
                } else if (userAgent.contains("YandexBot")) {
                    yandexBotCount++;
                }
            }
        }

        System.out.println("Общее количество запросов: " + totalLines);
        System.out.println("Количество запросов от Googlebot: " + googlebotCount);
        System.out.println("Количество запросов от YandexBot: " + yandexBotCount);

        if (totalLines > 0) {
            System.out.printf("Доля запросов от Googlebot: %.2f%%%n", (googlebotCount * 100.0 / totalLines));
            System.out.printf("Доля запросов от YandexBot: %.2f%%%n", (yandexBotCount * 100.0 / totalLines));
        }
    }

    private static String extractUserAgent(String line) {
        String[] parts = line.split("\"");
        return parts.length > 5 ? parts[5] : "";
    }

    private static class LineTooLongException extends RuntimeException {
        public LineTooLongException(String message) {
            super(message);
        }
    }
}