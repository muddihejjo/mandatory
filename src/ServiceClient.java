import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServiceClient {
    private String name;
    private Socket socket;
    private OutputStream output;
    private InputStream input;


    public ServiceClient(String name, Socket socket, OutputStream output, InputStream input) {
        this.name = name;
        this.socket = socket;
        this.output = output;
        this.input = input;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public OutputStream getOutput() {
        return output;
    }

    public void setOutput(OutputStream output) {
        this.output = output;
    }

    public InputStream getInput() {
        return input;
    }

    public void setInput(InputStream input) {
        this.input = input;


    }
    @Override
    public String toString(){
        return "Client:" + "name: " + name + "socket: " + socket + "input: " + input + "output: " + output;



    }
}
