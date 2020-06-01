package Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection implements Runnable {
    String serverName = "132.72.65.132"; //localhost  OR  132.72.65.132
    int serverPortNumber = 9876;
    private Socket server;
    private BufferedReader in;

    public ServerConnection() throws IOException {
        this.server = new Socket(serverName,serverPortNumber);
        this.in=new BufferedReader(new InputStreamReader(server.getInputStream()));
    }

    @Override
    public void run() {
        try{

            while(true){
                String serverResponse=in.readLine();
                if(serverResponse.equals("test Alert")){
                    System.out.println("get alert!!!!!!!!!!");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                in.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
