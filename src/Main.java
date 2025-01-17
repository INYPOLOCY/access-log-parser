import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int correctFilesCount = 0;

        while (true) {
            System.out.println("Введите путь к файлу:");
            String path = scanner.nextLine();

            File file = new File(path);
            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();

            if (!fileExists || isDirectory) {
                System.out.println("Указанный путь неверен: файл не существует или это директория.");
                continue;
            }

            correctFilesCount++;
            System.out.println("Путь указан верно");
            System.out.printf("Это файл номер %d\n", correctFilesCount);

            try {
                processFile(file);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private static void processFile(File file) throws IOException {
        int lineCount = 0;
        int maxLength = 0;
        int minLength = Integer.MAX_VALUE;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                int currentLength = line.length();

                if (currentLength > 1024) {
                    throw new LineTooLongException("Длина строки превышает 1024 символа!");
                }

                if (currentLength > maxLength) {
                    maxLength = currentLength;
                }

                if (currentLength < minLength) {
                    minLength = currentLength;
                }
            }
        }

        System.out.println("Общее количество строк в файле: " + lineCount);
        System.out.println("Длина самой длинной строки в файле: " + maxLength);
        System.out.println("Длина самой короткой строки в файле: " + minLength);
    }
}

class LineTooLongException extends RuntimeException {
    public LineTooLongException(String message) {
        super(message);
    }
}