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


    //        public static void main (String args[])
//        {
//            String serverName = "localhost";
//            int serverPortNumber = 9876;
//            Socket socket = null;
//            InputStream is = null;
//            DataInputStream dis = null;
//            OutputStream os = null;
//            DataOutputStream dos = null;
//            try {
//                socket = new Socket(serverName, serverPortNumber);
//                System.out.println("socket was created...");
//                is = socket.getInputStream();
//                System.out.println("input stream was created...");
//                dis = new DataInputStream(is);
//                System.out.println("data input stream was created...");
//                os = socket.getOutputStream();
//                System.out.println("output stream was created...");
//                dos = new DataOutputStream(os);
//                System.out.println("data output stream was created...");
//                dos.writeUTF("login:yasminr");
//                String r=dis.readUTF();
//                System.out.println(r);
//                System.out.println("done");
//
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (is != null) {
//                    try {
//                        is.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (os != null) {
//                    try {
//                        os.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (dis != null) {
//                    try {
//                        dis.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (dos != null) {
//                    try {
//                        dos.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (socket != null) {
//                    try {
//                        socket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
    public String openConnection(String data){
        String ans="";
        try {
            socket = new Socket(serverName, serverPortNumber);
            System.out.println("socket was created...");
            is = socket.getInputStream();
            System.out.println("input stream was created...");
            dis = new DataInputStream(is);
            System.out.println("data input stream was created...");
            os = socket.getOutputStream();
            System.out.println("output stream was created...");
            dos = new DataOutputStream(os);
            System.out.println("data output stream was created...");
            dos.writeUTF(data);
            ans=dis.readUTF();
            System.out.println(ans);
            System.out.println("done");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ans;
    }

}

