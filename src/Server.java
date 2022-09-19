import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static ServerSocket serverSocket;
    private static Server server = null;
    private Server() {
        try{
            System.out.println("Server is running");
            int PORT = 9999;
            serverSocket = new ServerSocket(PORT);
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("New client has connected");
                ConnectionHandler clientHandler = new ConnectionHandler(socket);
                clientHandler.start();
            }
        }catch (IOException e){
            closeServer();
        }
    }

    public static void start() {
        if(server == null){
            server = new Server();
        }
    }

    private static void closeServer(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server.start();
    }
}
