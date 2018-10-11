
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private static Thread IMAV;
    private static Socket socket;
    private static InputStream input;
    private static OutputStream output;
    private static Thread receiveMsgFromServer;

    public static void main(String[] args) {
        System.out.println(">>>Client<<<");


        Scanner console = new Scanner(System.in); //input from keyboard

        System.out.println("Please type in the IP for the server. (0 for localhost): ");
        String ipToConnect = args.length >= 1 ? args[0] : console.nextLine();


        System.out.println("What is the PORT of the server:");
        int portToConnect = args.length >= 2 ? Integer.parseInt(args[1]) : console.nextInt();


        final int PORT_SERVER = portToConnect;
        final String IPSERVER_STR = ipToConnect.equals("0") ? "127.0.0.1" : ipToConnect;


        Scanner enterUsername = new Scanner(System.in);
        String userName;


        while (true) {
            System.out.println("Please enter your Username: ");
            userName = enterUsername.nextLine();
            if (IsUsernameValid(userName))
                break;
            System.out.println("Sorry, Username may only be max 12 chars long, only letters, digits,‘-‘ and ‘_’ allowed. \n");


        }

        InetAddress ip;
        try {


            ip = InetAddress.getByName(IPSERVER_STR);
            System.out.println("\nConnecting..");
            System.out.println("SERVER IP :" + IPSERVER_STR);
            System.out.println("SERVER PORT : " + PORT_SERVER);
            System.out.println("Username :" + userName);
            socket = new Socket(ip, PORT_SERVER);

            input = socket.getInputStream();
            output = socket.getOutputStream();

            String messageTS = "JOIN " + userName + ", " + IPSERVER_STR + ": " + PORT_SERVER;
            byte[] dataTS = messageTS.getBytes();
            output.write(dataTS);
            System.out.println("\nTo leave the chat, type QUIT");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        receiveMsg();
        while (true) {
            try {

                OutputStream output = socket.getOutputStream();
                console = new Scanner(System.in);

                System.out.println("what do you want to send?:");
                String userInput = console.nextLine();


                String messageTS = "DATA " + userName + ": " + userInput;


                if (userInput.equals("QUIT")) {

                    System.out.println("closing client...You have left the chat!");

                    System.exit(0);

                } else if (messageTS.equalsIgnoreCase("DATA " + userName + ": " + "QUIT")) {
                    receiveMsgFromServer.stop();
                    IMAV.stop();
                    socket.close();
                    System.exit(1);
                    System.out.println("closing");
                    break;


                }

                byte[] dataTS = messageTS.getBytes();
                output.write(dataTS);


            } catch (UnknownHostException | ConnectException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    public static void receiveMsg() {

        Thread receiveMsgFromServer = new Thread(() -> { //vi gør det her i denne her thread
            try {
                while (true) {
                    byte[] dataIn = new byte[1024];
                    input.read(dataIn);
                    String msgIn = new String(dataIn);
                    msgIn = msgIn.trim();
                    System.out.println(msgIn);
                    if (msgIn.contains("J_ER")) {
                        System.out.println("Username already in use. Pick another one. Restart client");
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        receiveMsgFromServer.start();
        imavThread();


    }


    private static boolean IsUsernameValid(String userName) {

        return userName.matches("^[a-zA-Z0-9_-]{1,12}$");

    }

    static void imavThread() {
        IMAV = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60000);
                    output.write("IMAV".getBytes());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        IMAV.start();
    }

}
