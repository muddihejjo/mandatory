import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {
    static ArrayList<ServiceClient> clients = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        System.out.println("=============SERVER==============");


        final int PORT = 5656;

        ServerSocket server = new ServerSocket(PORT);
        if(PORT != 5656){

            System.out.println("wrong Port number. please type again");
        }


        System.out.println("Starting server...\n");

        while (true) {
            System.out.println("Waiting for client request");
            try {


                Socket socket = server.accept();

                ThreadsRecieve(socket);

                System.out.println("client connected");
                String clientIp = socket.getInetAddress().getHostAddress();
                System.out.println("IP: " + clientIp);
                System.out.println("PORT : " + socket.getPort());



            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Could not find local address.");
            }


        }


    }
    public static void ThreadsRecieve(Socket socket){

        Thread thread = new Thread(() -> {
            String USERNAME= "";
            try {
                InputStream input;
                OutputStream output;

                while (true) {
                    input = socket.getInputStream();
                    output = socket.getOutputStream();

                    byte[] dataIn = new byte[1024];
                    input.read(dataIn);
                    String messageIn = new String(dataIn);
                    messageIn = messageIn.trim();


                    USERNAME = messageIn.substring(5, messageIn.lastIndexOf(","));

                    boolean isNameUnique = true;

                    for (ServiceClient c : clients) {
                        if (USERNAME.equals(c.getName())) {
                            String err = "J_ER 2 - No duplicate names";
                            System.out.println(err);
                            byte[] bytes = err.getBytes();
                            output.write(bytes);
                            isNameUnique = false;
                            break;


                        }
                    }

                    if (!isNameUnique) {
                        break;
                    }


                    if (messageIn.substring(0, 4).equals("JOIN")) {
                        String f = "J_OK\r\n";
                        output.write(f.getBytes());

                        String all = "LIST";
                        for(ServiceClient c: clients) {
                            all += " " + c.getName();

                        }
                        sendMessageToAll(all);

                        //iterer igennem arraylist
                        //få fat i navn
                        //LIST navn navn1 ...
                        //String concat i for each loop

                        //sendMsgToAll(concatetString);


                    }
                    if (messageIn.substring(0,4).equals("QUIT")){
                        socket.close();


                    }
                    output = socket.getOutputStream();

                    clients.add(new ServiceClient(USERNAME, socket, output, input));

                    while (true) {


                        input = socket.getInputStream();


                        byte[] MessageFromUser = new byte[1024];
                        input.read(MessageFromUser);
                        String msgIn = new String(MessageFromUser);
                        msgIn = msgIn.trim();




                        for (ServiceClient sc : clients) {
                            if (msgIn.length() > 250) {
                                String messageTooLong = "J_ER 1: Message too long! \r\n";

                                output.write(messageTooLong.getBytes());

                                System.out.println("max 250 characters accepted! " + (msgIn.length() - msgIn.substring(0, 250).length()) + " characters has been cut off!");
                                msgIn = msgIn.substring(0, 250);
                            }


                            //skriver til alle clienterne.
                            output = sc.getOutput();
                            byte[] messageToAll = msgIn.getBytes();
                            output.write(messageToAll);
                        }
                        System.out.println(msgIn);
                    }

                }

            } catch (IOException e) {

                sendMessageToAll(USERNAME + " has left the room\r\n");


                for (int i = 0; i < clients.size(); i++) {
                    if(clients.get(i).getName().equals(USERNAME)){
                        clients.remove(i);
                    }
                }
                //socket.isConnected()
                //socket.isClosed()
                try {
                    socket.close();
                } catch (IOException e1) {
                    System.out.println("close catch");
                    e1.printStackTrace();
                }

            }
        });

        thread.start();
    }
    public static void sendMessageToAll(String msg){
        for (ServiceClient user: clients) {
            Socket socket = user.getSocket();
            try {
                OutputStream outputStream = socket.getOutputStream();
                    byte[] data = msg.getBytes();
                    outputStream.write(data);

            } catch (IOException e) {
                System.out.println("debug");
            }

        }
    }

}





