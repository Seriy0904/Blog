package uz.urgench.blog;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class ChatCacheCalls {
    public static void saveToCache(String sender, String message, Context context) throws IOException {
        String fileName = sender + getSaltString(2) + ".blogChat";
        FileOutputStream fileobj = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        byte[] byteArray = message.getBytes();
        fileobj.write(byteArray);
    }
    private static String getSaltString(int length) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();

    }
}
