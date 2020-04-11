import Commands.ICommand;
import Commands.LogIn;
import Commands.SignUp;
import Exceptions.EOFCommandGetException;
import Instruments.ClientRequest;
import Instruments.ServerResponse;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main {
    //private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Console reader = System.console();
    static boolean foo = false;
    static String login="", password="";

    public static void main(String[] args){
        try{
            /*SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket channel = (SSLSocket) sslsocketfactory.createSocket("localhost", 9999);*/

            Socket channel = new Socket("localhost", 4004);

            ObjectInputStream fromServerStream = new ObjectInputStream(channel.getInputStream());
            ObjectOutputStream toServerStream = new ObjectOutputStream(channel.getOutputStream());
            toServerStream.flush();

            ServerResponse sr = (ServerResponse) fromServerStream.readObject();
            outputInfo(sr.getText());

            String currentCommand;
            CommandFactory commandFactory = new CommandFactory();
            String temp;
            while (true){
                outputInfo("Ведите команду log_in, чтобы войти, или sign_up, чтобы зарегестрироваться");
                temp = reader.readLine();
                if(temp.equals("log_in")||temp.equals("sign_up")) break;
            }
            MessageDigest messageDigest = MessageDigest.getInstance("MD2");
            while (true){
                outputInfo("Ведите логин:");
                login=reader.readLine();
                outputInfo("Ведите пароль:");
                password= String.valueOf(reader.readPassword());
                if(temp.equals("log_in")){
                    toServerStream.writeObject(new ClientRequest(new LogIn(),login,""));
                    sr = (ServerResponse) fromServerStream.readObject();
                    if(sr.isAccess()){
                        password=password+sr.getText();
                        messageDigest.reset();
                        messageDigest.update(password.getBytes());
                        password = DatatypeConverter.printHexBinary(messageDigest.digest());
                        toServerStream.writeObject(new ClientRequest(new LogIn(),login,password));
                        sr = (ServerResponse) fromServerStream.readObject();
                        if(sr.isAccess()){
                            outputInfo(sr.getText());
                            break;
                        }
                        else outputInfo(sr.getText());
                    }else outputInfo(sr.getText());
                }
                if(temp.equals("sign_up")){
                    toServerStream.writeObject(new ClientRequest(new SignUp(),login,""));
                    sr = (ServerResponse) fromServerStream.readObject();
                    if(sr.isAccess()){

                        outputInfo("Ведите e-mail:");
                        String email= String.valueOf(reader.readPassword());

                        password=password+sr.getText();
                        messageDigest.reset();
                        messageDigest.update(password.getBytes());
                        password = DatatypeConverter.printHexBinary(messageDigest.digest());
                        toServerStream.writeObject(new ClientRequest(new SignUp(),login,password));
                        sr = (ServerResponse) fromServerStream.readObject();
                        if(sr.isAccess()){
                            outputInfo(sr.getText());
                            break;
                        }
                        else outputInfo(sr.getText());
                    }else outputInfo(sr.getText());

                }
            }
            while (true) {
                try {
                    currentCommand = reader.readLine();
                    if (currentCommand == null) throw new EOFCommandGetException();
                    ICommand command = commandFactory.getCommand(currentCommand.trim().split(" "), reader);
                    if (command != null) {
                        toServerStream.writeObject(new ClientRequest(command,login,password));
                        ServerResponse response = (ServerResponse) fromServerStream.readObject();
                        if (response.isShutdown()) {
                            System.out.println(response.getText());
                            System.exit(0);
                        }
                        System.out.println(response.getText() + "\n");
                        outputDollar();
                    }
                }catch (IOException e) {
                    long t = System.currentTimeMillis();
                    long end = t + 15000;
                    foo=false;
                    while (System.currentTimeMillis() < end) {
                        try {
                            outputInfo("Ожидание...");
                            Socket chan = new Socket("localhost", 4004);
                            fromServerStream = new ObjectInputStream(chan.getInputStream());
                            toServerStream = new ObjectOutputStream(chan.getOutputStream());
                            toServerStream.flush();

                            //reader = new BufferedReader(new InputStreamReader(System.in));
                            reader = System.console();
                            sr = (ServerResponse) fromServerStream.readObject();
                            outputInfo(sr.getText());
                            foo=true;
                            break;
                        } catch (Exception ignored) {
                            Thread.sleep(500);
                        }
                    }
                    if(!foo){
                        outputInfo("Ошибка повторного подключения. Завершение работы...");
                        System.exit(1);
                    }
                } catch (EOFCommandGetException e) {
                    outputInfo(e.getMessage());
                    System.exit(1);
                }
            }

        } catch (IOException | ClassNotFoundException | InterruptedException | NoSuchAlgorithmException e) {
            outputInfo("Ошибка подключения. Завершение работы...");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void outputInfo(String text) {
        System.out.println(text);
        System.out.print("$");
    }

    public static void outputDollar(){
        System.out.print("$");
    }
}
