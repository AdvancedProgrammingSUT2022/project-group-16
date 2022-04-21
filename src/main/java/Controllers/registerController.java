package Controllers;

import java.util.Scanner;
import java.util.regex.Matcher;

import Models.User;
import Models.Menu.Menu;
import veiws.mainMenuVeiw;
public class registerController {
    public static Boolean doesUsernameExist(String username) {
        for(int i = 0; i < Menu.allUsers.length; i++) {
            if(Menu.allUsers[i].getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean doesNicknameExist(String nickName) {
        for(int i = 0; i < Menu.allUsers.length; i++) {
            if(Menu.allUsers[i].getNickname().equals(nickName)) {
                return true;
            }
        }
        return false;
    }

    public static void createPlayer(String username, String password, String nickname) {
        int length = Menu.allUsers.length;
        User[] newArr = new User[length + 1];
        for(int i = 0; i < length; i++) {
            newArr[i] = Menu.allUsers[i];
        }
        newArr[length] = new User(username, nickname, password);
        Menu.allUsers = newArr;
    }

    public static Boolean isPasswordCorrect(String username, String password) {
        for(int i = 0; i < Menu.allUsers.length; i++) {
            if(Menu.allUsers[i].getUsername().equals(username) 
                && !Menu.allUsers[i].getPassword().equals(password)) {
                    return true;
            }
        }
        return false;
    }

    public static void loginPlayer(String username, Scanner scanner, Matcher matcher) {
        mainMenuVeiw.run(scanner, matcher);
    }

    public static void logoutPlayer() {
        
    }
}
