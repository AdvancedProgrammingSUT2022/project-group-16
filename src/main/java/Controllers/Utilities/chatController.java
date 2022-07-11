package Controllers.Utilities;

import Controllers.RegisterController;
import Models.Menu.Menu;
import Models.User;
import Models.chat.Message;
import com.google.gson.Gson;
import enums.chatEnum;
import enums.registerEnum;

import javax.print.attribute.standard.Media;
import java.io.*;
import java.util.ArrayList;

public class chatController {
    public static ArrayList<Message> publicMessages = new ArrayList<>();

    public void sendMessage(User sender, User receiver, String message) {
        Message tmp = new Message(sender.getUsername(), receiver.getUsername(), message);
        sender.getPrivateChats().get(receiver.getUsername()).add(tmp);
        receiver.getPrivateChats().get(sender.getUsername()).add(tmp);
    }

    public void updateDatabase()
    {
        try {
            Gson gson = new Gson();
            String arr = (new BufferedReader(new FileReader(chatEnum.filePath.regex))).readLine();
            if(arr != null)
            {
                arr = arr.substring(1,arr.length() - 1);
                String[] splitedArr = arr.split("},");
                for(int i = 0; i < splitedArr.length; i++)
                {
                    if(i != splitedArr.length - 1){
                        splitedArr[i] += "}";
                    }
                    publicMessages.add(gson.fromJson(splitedArr[i], Message.class));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//update arrayList with Json database

    public void writeDataOnJson()
    {
        try {
            Writer writer = new FileWriter(chatEnum.filePath.regex);
            new Gson().toJson(publicMessages, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//update Json database with arrayList
}
