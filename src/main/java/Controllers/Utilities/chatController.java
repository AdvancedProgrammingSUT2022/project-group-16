package Controllers.Utilities;

import Models.chat.Message;
import com.google.gson.Gson;

import javax.print.attribute.standard.Media;
import java.io.DataOutputStream;
import java.io.IOException;

public class chatController {
    public void sendMessage(DataOutputStream dataOutputStream, Message message) throws IOException {
        dataOutputStream.writeUTF(new Gson().toJson(message));
        dataOutputStream.flush();
    }
}
