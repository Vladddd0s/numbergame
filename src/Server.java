import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class Server {
    public static void main(String args[]) throws IOException {
        ServerSocket serverSocket = new ServerSocket( 4000);
        ExecutorService pool= Executors.newFixedThreadPool(100);

        while(true){
            Game game = new Game();

            pool.execute(game.new Player(serverSocket.accept()));
            pool.execute(game.new Player(serverSocket.accept()));

        }
    }

}
class Game{
    Integer winner=0;
    Integer num=0;
    Integer rnum=-1;
    boolean start=false;
    boolean isready=false;
    Player p1;
    Player p2;

    private static boolean isDigit(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    class Player implements Runnable {
        private BufferedReader in;
        private BufferedWriter out;
        Socket socket;
        int playernum;
        int guess;
        public Player(Socket socket) throws IOException{
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            synchronized (num){
                num++;
                this.playernum=num;
                if(p1==null){
                    p1=this;
                }else{
                    p2=this;
                    start=true;
                }
            }
        }
        private void send(String msg) {
            try {
                out.write(msg + "\n");
                out.flush();
            } catch (IOException ignored) {}
        }

        @Override
        public void run() {
            System.err.println("player "+this.playernum+" connected");
            String inputLine;
            try {

                send("You are player"+num);

                while(true){

                    System.err.println("num"+num);
                    if (num == 2 && rnum == -1) {

                        synchronized (rnum) {
                            if(!isready) {

                                rnum = (int) (Math.random() * 10);
                                isready = true;
                                System.err.println("number :"+rnum);
                            }


                        }

                    }
                    if(isready) {

                        //System.err.println("num"+num);
                        inputLine = in.readLine();

                        if (isDigit(inputLine)) {
                            guess = Integer.parseInt(inputLine);
                            if (this.guess == rnum) {
                                synchronized (winner) {
                                    winner = this.playernum;
                                    this.send("You Win");
                                    isready=false;
                                    if (winner == 1) {
                                        if (p2 != null)
                                            p2.send("Player 1 won");

                                    } else {
                                        if (p2 != null)
                                            p1.send("Player 2 won");

                                    }
                                }
                                rnum = -1;
                            } else {
                                this.send("wrong");

                            }

                        } else {
                            if (inputLine.equals("quit")) {
                               
                                if (this.playernum==1)
                                    p2.send("other player left");
                                else
                                    p1.send("other player left");
                                break;

                            }
                        }
                    }


                }

                socket.close();
            }catch (Exception e) {
                System.out.println("Client connection termintated");
            }




        }


    }

}
