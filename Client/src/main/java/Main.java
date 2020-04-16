import Commands.ICommand;
import Commands.LogIn;
import Commands.SignUp;
import Exceptions.EOFCommandGetException;
import Instruments.ClientRequest;
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;

import javax.xml.bind.DatatypeConverter;
import java.io.Console;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main {
    private static Console reader = System.console();
    static boolean foo = false;
    static String login="", password="";

    public static void main(String[] args){
        try{
            /*SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket channel = (SSLSocket) sslsocketfactory.createSocket("localhost", 4004);*/
            int port = 4004;
            if (args.length == 1) {
                try {
                    port = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    outputText("Ошибка парсинга порта. Выбран порт по-умолчанию: "+port);
                }
            }

            Socket channel = new Socket("localhost", port);

            ObjectInputStream fromServerStream = new ObjectInputStream(channel.getInputStream());
            ObjectOutputStream toServerStream = new ObjectOutputStream(channel.getOutputStream());
            toServerStream.flush();

            ServerResponse sr; //= (ServerResponse) fromServerStream.readObject();
            //outputInfo(sr.getAdditionalInfo());

            String currentCommand;
            CommandFactory commandFactory = new CommandFactory();
            String temp;
            while (true){
                outputInfo("Ведите команду log_in, чтобы войти, или sign_up, чтобы зарегестрироваться");
                temp = reader.readLine();
                if(temp.equals("log_in")||temp.equals("sign_up")) break;
            }

            MessageDigest messageDigest = MessageDigest.getInstance("MD2");

            if(temp.equals("sign_up")) outputText("Пароль должен быть не менее 5 символов в длинну, содержать как минимум 1 цифру и 1 прописную букву.");
            while (true){
                outputInfo("Ведите имя пользователя:");
                login=reader.readLine();
                if(temp.equals("sign_up") && login.length()<3){
                    outputText("Имя пользователя не может быть короче 3 символов.");
                    continue;
                }
                outputInfo("Ведите пароль:");
                password= String.valueOf(reader.readPassword());
                if(temp.equals("sign_up") && (password.length()<5 || !password.matches(".*\\d+.*") || !password.matches(".*[A-Z].*"))){
                    outputText("Пароль не соответствует требованиям: пароль должен быть не менее 5 символов в длинну, содержать как минимум 1 цифру и 1 прописную букву.");
                    continue;
                }
                if(temp.equals("log_in")){
                    toServerStream.write(1);
                    toServerStream.writeObject(new ClientRequest(new LogIn(),login,"%"));
                    sr = (ServerResponse) fromServerStream.readObject();
                    System.out.println(sr.isAccess());
                    if(sr.isAccess()){
                        password=password+sr.getAdditionalInfo();
                        messageDigest.reset();
                        messageDigest.update(password.getBytes());
                        password = DatatypeConverter.printHexBinary(messageDigest.digest());
                        toServerStream.write(1);
                        toServerStream.writeObject(new ClientRequest(new LogIn(),login,password));
                        sr = (ServerResponse) fromServerStream.readObject();
                        if(sr.isAccess()){
                            outputInfo("Авторизация успешна");
                            break;
                        }
                        else{
                            switch (sr.getCode()){
                                case ERROR:
                                    outputInfo("Неизвестная ошибка. Попробуйте еще раз.");
                                    break;
                                case INCORRECT_LOG_IN:
                                    outputInfo("Неверный логин/пароль.");
                                    break;
                            }
                        }
                    }else{
                        switch (sr.getCode()){
                            case ERROR:
                                outputInfo("Неизвестная ошибка. Попробуйте еще раз.");
                                break;
                            case INCORRECT_LOG_IN:
                                System.out.println("erwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
                                outputInfo("Неверный логин/пароль.");
                                break;
                        }
                    }
                }
                if(temp.equals("sign_up")){
                    outputInfo("Повторите пароль:");
                    String temppsw= String.valueOf(reader.readPassword());
                    if(temppsw.equals(password)) {
                        toServerStream.write(1);
                        toServerStream.writeObject(new ClientRequest(new SignUp(), login, "%"));
                        sr = (ServerResponse) fromServerStream.readObject();
                        if (sr.isAccess()) {

                            outputInfo("Ведите e-mail:");
                            String email = String.valueOf(reader.readLine());

                            password = password + sr.getAdditionalInfo();
                            messageDigest.reset();
                            messageDigest.update(password.getBytes());
                            password = DatatypeConverter.printHexBinary(messageDigest.digest());
                            SignUp signUp = new SignUp();
                            signUp.setSalt(sr.getAdditionalInfo());
                            toServerStream.write(1);
                            toServerStream.writeObject(new ClientRequest(signUp, login, password));
                            sr = (ServerResponse) fromServerStream.readObject();
                            if (sr.isAccess()) {
                                outputInfo(sr.getAdditionalInfo());
                                break;
                            } else{
                                switch (sr.getCode()){
                                    case ERROR:
                                        outputInfo("Неизвестная ошибка. Попробуйте еще раз.");
                                        break;
                                    case INCORRECT_LOG_IN:
                                        outputInfo("Пользователь с данным login уже существует.");
                                        break;
                                }
                            }
                        } else{
                            switch (sr.getCode()){
                                case ERROR:
                                    outputInfo("Неизвестная ошибка. Попробуйте еще раз.");
                                    break;
                                case INCORRECT_LOG_IN:
                                    outputInfo("Пользователь с данным login уже существует");
                                    break;
                            }
                        }
                    }else{
                        outputInfo("Пароли не совпадают.");
                    }
                }
            }

            while (true) {
                try {
                    currentCommand = reader.readLine();
                    if (currentCommand == null) throw new EOFCommandGetException();
                    ICommand command = commandFactory.getCommand(currentCommand.trim().split(" "), reader, login);
                    if (command != null) {
                        toServerStream.write(1);
                        toServerStream.writeObject(new ClientRequest(command,login,password));
                        ServerResponse response = (ServerResponse) fromServerStream.readObject();
                        switch (response.getCode()){

                            case SEARCH_OK:
                                outputInfo("Результаты поиска:\n"+response.getAdditionalInfo() + "\n");
                                break;
                            case COUNT:
                                outputInfo("Результаты подсчета:\n"+response.getAdditionalInfo() + "\n");
                                break;
                            case SEARCH_NOT_FOUND:
                                outputInfo("Поиск не дал результатов.");
                                break;
                            case SQL_ERROR:
                                outputInfo("Ошибка исполнения запроса. Попробуйте еще раз.");
                                break;
                            case ADD_OK:
                                outputInfo("Элемент успешно добавлен.");
                                break;
                            case NO_CHANGES:
                                outputInfo("Коллекция не изменилась");
                                break;
                            case DELETE_OK:
                                outputInfo("Удаление успешно");
                                break;
                            case CLEAR_OK:
                                outputInfo("Все Ваши элементы успешно удалены");
                                break;
                            case UPDATE_OK:
                                outputInfo("Поля элемента успешно обновлены");
                                break;
                            case ERROR:
                                outputInfo("Неизвестная ошибка. Попробуйте еще раз.");
                                break;
                            case SCRIPT_RESULT:
                                outputInfo("Результат работы скрипта:\n"+response.getAdditionalInfo() + "\n");
                                break;
                            case SERVER_FATAL_ERROR:
                                outputInfo("\nКритическая ошибка сервера. Завершение работы...");
                                System.exit(0);
                                break;
                            case TEXT_ONLY:
                                outputInfo("Ответ сервера: \n"+response.getAdditionalInfo() + "\n");
                                break;
                            case EXIT:
                                outputInfo( "\nЗавершение работы...");
                                System.exit(0);
                                break;
                            case CONNECTED:
                                outputInfo("Соединение установлено");
                                break;
                            case SURPRISE_NOT_CORRECT_LOGIN_OR_PASSWORD:
                                outputInfo("Каким-то образом вы пытались выполнить команду, введя неверные данные для входа! Хакерам здесь не рады!:)");
                        }
                    }
                }catch (IOException e) {
                    long t = System.currentTimeMillis();
                    long end = t + 15000;
                    foo=false;
                    while (System.currentTimeMillis() < end) {
                        try {
                            outputInfo("Ожидание...");
                            Socket chan = new Socket("localhost", port);
                            fromServerStream = new ObjectInputStream(chan.getInputStream());
                            toServerStream = new ObjectOutputStream(chan.getOutputStream());
                            toServerStream.flush();

                            reader = System.console();
                            sr = (ServerResponse) fromServerStream.readObject();
                            outputInfo(sr.getAdditionalInfo());
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
            System.exit(1);
        }
    }

    public static void outputInfo(String text) {
        System.out.println(text);
        System.out.print("$");
    }
    public static void outputText(String text){
        System.out.println(text);
    }
}
