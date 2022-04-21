package veiws;

import java.util.Scanner;
import java.util.regex.Matcher;

public class mainMenuVeiw {
    public static void run(Scanner scanner, Matcher matcher) {
        String command;
        while(true) {
            command = scanner.nextLine();
            if(command.equals("user logout")) {
                System.out.println("user logged out successfully!");
                break;
            }
        }
    }
}
