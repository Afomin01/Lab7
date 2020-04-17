import Commands.ExecuteScript;
import Commands.Exit;
import Commands.ICommand;
import Commands.LogIn;
import Instruments.ClientRequest;
import Instruments.SerializeManager;
import Instruments.ServerResponse;

import javax.xml.bind.DatatypeConverter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;

public class DDosClient implements Runnable {

    InputStream fromServer;
    OutputStream toServer;
    String scriptName;

    DDosClient(String scriptName) {
        try {
            this.scriptName=scriptName;
            Socket channel = new Socket("localhost", 4004);

            fromServer = channel.getInputStream();
            toServer = channel.getOutputStream();
            toServer.flush();


        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD2");
            ServerResponse sr;
            String password = "12Qwerty";
            String login = "user2";
            toServer.write(SerializeManager.toByte(new ClientRequest(new LogIn(), login, "%")));
            byte[] b = new byte[10000];
            fromServer.read(b);
            sr = (ServerResponse) SerializeManager.fromByte(b);
            if (sr.isAccess()) {
                password = password + sr.getAdditionalInfo();
                messageDigest.reset();
                messageDigest.update(password.getBytes());
                password = DatatypeConverter.printHexBinary(messageDigest.digest());

                toServer.write(SerializeManager.toByte(new ClientRequest(new LogIn(), login, password)));
                b = new byte[10000];
                fromServer.read(b);
                sr = (ServerResponse) SerializeManager.fromByte(b);
                if (sr.isAccess()) {
                }
            }
            while (true) {
                Thread.sleep(500);
                ICommand command = new ExecuteScript(scriptName, "user2");
                if (command != null) {

                    toServer.write(SerializeManager.toByte(new ClientRequest(command, "user2", password)));

                    b = new byte[10000000];
                    fromServer.read(b);
                    ServerResponse response = (ServerResponse) SerializeManager.fromByte(b);
                    System.out.println(response.getCode().toString());
                    Main.serverResponseDecode(response);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}