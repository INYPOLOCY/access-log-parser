import java.util.Scanner;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println("Введите первое число:");
        int x = new Scanner(System.in).nextInt();
        System.out.println("Введите второе число:");
        int y = new Scanner(System.in).nextInt();

        System.out.println("Сумма: "+(x+y));
        System.out.println("Разница: "+(x-y));
        System.out.println("Произведение: "+(x*y));
        System.out.println("Частное: "+(double)x/y);
    }
}