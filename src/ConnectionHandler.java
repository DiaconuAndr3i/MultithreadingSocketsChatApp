import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionHandler extends Thread{

    private static final List<ConnectionHandler> connectionHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    public ConnectionHandler(Socket socket){
        try{
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            this.username = bufferedReader.readLine();
            connectionHandlers.add(this);

            String message = "Server: " + this.username + " joined";
            sendMessage(message);
        }catch (IOException e){
            closeConnection(socket, bufferedReader, bufferedWriter);
        }
    }

    public void closeConnection(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        connectionHandlers.remove(this);
        String message = "Server: " + this.username + " has left";
        sendMessage(message);
        try {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        for(ConnectionHandler ch: connectionHandlers){
            try{
                if(!ch.username.equals(this.username)){
                    ch.bufferedWriter.write(message);
                    ch.bufferedWriter.newLine();
                    ch.bufferedWriter.flush();
                }
            }catch (IOException e){
                closeConnection(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    @Override
    public void run(){
        String message;

        while(socket.isConnected()){
            try{
                message = bufferedReader.readLine();
                if(message.endsWith("/close")) {
                    System.out.println("Client has disconnected");
                    closeConnection(socket, bufferedReader, bufferedWriter);
                    break;
                }
                sendMessage(message);
            }catch (IOException e){
                closeConnection(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
}
