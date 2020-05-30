package Service;

import java.io.*;
import java.net.Socket;
import java.util.Observable;

/**
 * This class implements java socket client
 * @author pankaj
 *
 */
public class Client extends Observable {
    String serverName = "localhost"; //localhost  OR  132.72.65.132
    int serverPortNumber = 9876;
    Socket socket = null;

    public String openConnection(String data) throws Exception {
        String serverResponse="";
        try {
            socket = new Socket(serverName, serverPortNumber);
            BufferedReader input = new BufferedReader(new InputStreamReader((socket.getInputStream())));
            //BufferedReader output = new BufferedReader(new OutputStr((socket.getOutputStream())));
            BufferedReader keyboard= new BufferedReader((new InputStreamReader((System.in))));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            out.println(data);
            //String command = keyboard.readLine();
            serverResponse= input.readLine();
            System.out.println(serverResponse);

        } catch (IOException e) {
            throw new Exception("can't connect to the DB or the Server");
        } finally {
        }
        return serverResponse;
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
