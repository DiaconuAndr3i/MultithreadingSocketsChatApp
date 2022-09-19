import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Socket socket;
    private boolean done = false;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private final String username;
    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    public Client(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
        try{
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch (IOException e){
            dropConnection(socket, bufferedReader, bufferedWriter);
        }
    }
    public void dropConnection(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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

    public void sendMessage(){
        try{
            bufferedWriter.write(this.username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (!done){
                String messageFromClient = scanner.nextLine();
                String message = this.username + ": " + messageFromClient;
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                if(messageFromClient.endsWith("/close")){
                    done = true;
                    dropConnection(socket, bufferedReader, bufferedWriter);
                }
            }
        }catch (IOException e){
            dropConnection(socket, bufferedReader, bufferedWriter);
        }
    }

    public void receiveMessage(){
        new Thread(() -> {
            String messageFromConnection;
            while(!done){
                try{
                    messageFromConnection = bufferedReader.readLine();
                    if(messageFromConnection != null){
                        System.out.println(messageFromConnection);
                    }
                }
                catch (IOException e){
                    dropConnection(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        System.out.println("Enter username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        try{
            Socket socket = new Socket(HOST, PORT);
            Client client = new Client(socket, username);
            client.receiveMessage();
            client.sendMessage();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
