import java.util.Scanner;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.*;
import java.net.*;
public class Client1 {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader inputUser;
    public Client1()throws Exception{

        try {
            socket = new Socket("localhost", 4000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new Readm().start();
            new Writem().start();
        } catch (IOException e) {
            dw();
        }

    }
    private class Readm extends Thread {
        @Override
        public void run() {

            String str;
            try {
                while (true) {
                    str = in.readLine();
                    System.out.println(str);
                }
            } catch (IOException e) {
                dw();
            }
        }
    }
    public class Writem extends Thread {

        @Override
        public void run() {
            while (true) {
                String userWord;
                try {
                    userWord = inputUser.readLine();
                    if (userWord.equals("quit")) {
                        out.write("quit" + "\n");
                       out.flush();
                        dw();
                        break;
                    } else {
                        out.write(userWord + "\n");
                    }
                    out.flush();
                } catch (IOException e) {
                    dw();
                }

            }
        }
    }
    public void dw(){
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }


    public static void main(String[] args) throws IOException  {
        try {
            Client1 client=new Client1();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
