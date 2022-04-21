package veiws;

import java.util.Scanner;
import java.util.regex.Matcher;

import Controllers.registerController;
import enums.Command;

public class registerAndLoginVeiw {

    public static void run() {
        Scanner scanner = new Scanner(System.in);
        String command;
        Matcher matcher;

        while(true) {
            command = scanner.nextLine();
            if((matcher = Command.compareRegex(command, Command.enterMenu)).find()) {
                System.out.println("menu navigation is not possible");
            }
            else if(command.equals("menu exit")) break;
            else if(command.equals("menu show-current")) {
                System.out.println("Login Menu");
            }
            else if((matcher = Command.compareRegex(command, Command.registerUser)).find()) {
                if(registerController.doesUsernameExist(matcher.group("username"))) {
                    System.out.println("user with username " + matcher.group("username") + " already exists");
                }
                else if(registerController.doesNicknameExist(matcher.group("nickname"))) {
                    System.out.println("user with nickname " + matcher.group("nickname") + " already exists");
                }
                else {
                    System.out.println("user created successfully!");
                    registerController.createPlayer(matcher.group("username"), matcher.group("password"), matcher.group("nickname"));
                }
            }
            else if((matcher = Command.compareRegex(command, Command.loginUser)).find()) {
                if(!registerController.doesUsernameExist(matcher.group("username"))
                    || registerController.isPasswordCorrect(matcher.group("username"), matcher.group("password"))) {
                    System.out.println("Username and password didn't match!");
                }
                else {
                    System.out.println("user logged in successfully!");
                    registerController.loginPlayer(matcher.group("username"), scanner, matcher);
                }
            }
            else {
                System.out.println("invalid command");
            }
        }
        scanner.close();
    }
}
