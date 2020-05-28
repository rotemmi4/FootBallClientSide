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
    String serverName = "localhost";
    int serverPortNumber = 9876;
    Socket socket = null;
    InputStream is = null;
    DataInputStream dis = null;
    OutputStream os = null;
    DataOutputStream dos = null;

    public String openConnection(String data){
        String ans="";
        String serverResponse="";
        try {
            socket = new Socket(serverName, serverPortNumber);
            BufferedReader input = new BufferedReader(new InputStreamReader((socket.getInputStream())));
//            BufferedReader output = new BufferedReader(new OutputStr((socket.getOutputStream())));
            BufferedReader keyboard= new BufferedReader((new InputStreamReader((System.in))));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            out.println(data);
            //String command = keyboard.readLine();
            serverResponse= input.readLine();
            System.out.println(serverResponse);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return serverResponse;
    }

}
