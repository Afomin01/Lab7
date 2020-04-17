import Commands.Exit;
import Commands.ICommand;
import Commands.LogIn;
import Commands.SignUp;
import Exceptions.EOFCommandGetException;
import Instruments.ClientRequest;
import Instruments.SerializeManager;
import Instruments.ServerResponse;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
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
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    outputText("Ошибка парсинга порта. Выбран порт по-умолчанию: "+port);
                }
            }

            Socket channel = new Socket("localhost", port);

            InputStream fromServer = channel.getInputStream();
            OutputStream toServer = channel.getOutputStream();
            toServer.flush();

            ServerResponse sr;

            String currentCommand;
            CommandFactory commandFactory = new CommandFactory();
            String temp;
            while (true){
                outputInfo("Ведите команду log_in, чтобы войти, или sign_up, чтобы зарегестрироваться");
                temp = reader.readLine();
                if (temp==null){
                    outputInfo("Введен специальный символ. Завершение работы...");
                    System.exit(0);
                }
                if(temp.equals("log_in")||temp.equals("sign_up")) break;
            }

            MessageDigest messageDigest = MessageDigest.getInstance("MD2");

            if(temp.equals("sign_up")) outputText("Пароль должен быть не менее 5 символов в длинну, содержать как минимум 1 цифру и 1 прописную букву.");
            while (true){
                outputInfo("Ведите имя пользователя:");
                login=reader.readLine();
                if (login==null){
                    outputInfo("Введен специальный символ. Завершение работы...");
                    System.exit(0);
                }
                if(temp.equals("sign_up") && login.length()<3){
                    outputText("Имя пользователя не может быть короче 3 символов.");
                    continue;
                }
                outputInfo("Ведите пароль:");
                password= String.valueOf(reader.readPassword());
                if (password==null){
                    outputInfo("Введен специальный символ. Завершение работы...");
                    System.exit(0);
                }
                if(temp.equals("sign_up") && (password.length()<5 || !password.matches(".*\\d+.*") || !password.matches(".*[A-Z].*"))){
                    outputText("Пароль не соответствует требованиям: пароль должен быть не менее 5 символов в длинну, содержать как минимум 1 цифру и 1 прописную букву.");
                    continue;
                }
                if(temp.equals("log_in")){
                    try {
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
                                outputInfo("Авторизация успешна");
                                break;
                            } else {
                                switch (sr.getCode()) {
                                    case ERROR:
                                        outputInfo("Неизвестная ошибка. Попробуйте еще раз.");
                                        break;
                                    case INCORRECT_LOG_IN:
                                    case SQL_ERROR:
                                        outputInfo("Неверный логин/пароль.");
                                        break;
                                }
                            }
                        } else {
                            switch (sr.getCode()) {
                                case ERROR:
                                    outputInfo("Неизвестная ошибка. Попробуйте еще раз.");
                                    break;
                                case INCORRECT_LOG_IN:
                                case SQL_ERROR:
                                    outputInfo("Неверный логин/пароль.");
                                    break;
                            }
                        }
                    }catch (NullPointerException e){
                        outputText("Сервер не доступен");
                    }
                }
                if(temp.equals("sign_up")){
                    try {
                    outputInfo("Повторите пароль:");
                    String temppsw= String.valueOf(reader.readPassword());
                    if(temppsw.equals(password)) {

                        toServer.write(SerializeManager.toByte(new ClientRequest(new SignUp(), login, "%")));
                        byte[] b = new byte[10000];
                        fromServer.read(b);
                        sr = (ServerResponse) SerializeManager.fromByte(b);
                        if (sr.isAccess()) {

                            outputInfo("Ведите e-mail, либо no для отказза ввода:");//TODO while
                            String email = String.valueOf(reader.readLine());

                            password = password + sr.getAdditionalInfo();
                            messageDigest.reset();
                            messageDigest.update(password.getBytes());
                            password = DatatypeConverter.printHexBinary(messageDigest.digest());
                            SignUp signUp = new SignUp();
                            signUp.setSalt(sr.getAdditionalInfo());

                            toServer.write(SerializeManager.toByte(new ClientRequest(signUp, login, password)));
                            b = new byte[10000];
                            fromServer.read(b);
                            sr = (ServerResponse) SerializeManager.fromByte(b);
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
                                    case SQL_ERROR:
                                        outputInfo("Ошибка регистрации, попробуйте еще раз");
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
                                case SQL_ERROR:
                                    outputInfo("Ошибка регистрации, попробуйте еще раз");
                                    break;
                            }
                        }
                    }else{
                        outputInfo("Пароли не совпадают.");
                    }
                    }catch (NullPointerException e){
                        outputText("Сервер не доступен");//TODO reconnect
                    }
                }
            }

            while (true) {
                try {
                    currentCommand = reader.readLine();
                    if (currentCommand == null){
                        toServer.write(SerializeManager.toByte(new ClientRequest(new Exit(login), login, password)));

                        byte[] b = new byte[10000];
                        fromServer.read(b);
                        ServerResponse resp = (ServerResponse) SerializeManager.fromByte(b);
                        outputText("Введен специальный символ. Завершение работы...");
                        System.exit(1);
                    }
                    ICommand command = commandFactory.getCommand(currentCommand.trim().split(" "), reader, login);
                    if (command != null) {

                        toServer.write(SerializeManager.toByte(new ClientRequest(command,login,password)));

                        byte[] b = new byte[10000];
                        fromServer.read(b);
                        ServerResponse response = (ServerResponse) SerializeManager.fromByte(b);
                        switch (response.getCode()){//TODO null pointer when server offline
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
                                outputText( "\nЗавершение работы...");
                                System.exit(0);
                                break;
                            case CONNECTED:
                                outputInfo("Соединение установлено");
                                break;
                            case SURPRISE_NOT_CORRECT_LOGIN_OR_PASSWORD:
                                outputInfo("Каким-то образом вы пытались выполнить команду, введя неверные данные для входа! Хакерам здесь не рады!:)");
                        }
                    }
                }catch (IOException | NullPointerException e) {
                    long t = System.currentTimeMillis();
                    long end = t + 15000;
                    foo=false;
                    while (System.currentTimeMillis() < end) {
                        try {
                            outputInfo("Ожидание...");
                            channel = new Socket("localhost", port);

                            fromServer = channel.getInputStream();
                            toServer = channel.getOutputStream();
                            toServer.flush();

                            reader = System.console();//TODO log_in no
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
                }
            }

        } catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
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
