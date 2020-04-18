package Client;

import Commands.Exit;
import Commands.ICommand;
import Commands.LogIn;
import Commands.SignUp;
import Instruments.ClientRequest;
import Instruments.SerializeManager;
import Instruments.ServerResponse;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class Main {
    private static Console reader = System.console();
    static boolean foo = false;
    static String login="", password="";

    public static void main(String[] args){
        try{
            int port = 47836;
            if (args.length == 1) {
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    outputInfo("Ошибка парсинга порта. Выбран порт по-умолчанию: "+port);
                }
            }

            Socket channel = new Socket("localhost", port);
            channel.setReceiveBufferSize(100000000);

            InputStream fromServer = channel.getInputStream();
            OutputStream toServer = channel.getOutputStream();
            toServer.flush();

            ServerResponse sr;

            String currentCommand;
            CommandFactory commandFactory = new CommandFactory();
            String temp;
            while (true){
                outputInfo("Ведите команду log_in, чтобы войти, или sign_up, чтобы зарегестрироваться");
                dollar();
                temp = reader.readLine();
                if (temp==null){
                    outputInfo("Введен специальный символ. Завершение работы...");
                    System.exit(0);
                }
                if(temp.equals("log_in")||temp.equals("sign_up")) break;
            }

            MessageDigest messageDigest = MessageDigest.getInstance("MD2");

            if(temp.equals("sign_up")) outputInfo("Пароль должен быть не менее 5 символов в длинну, содержать как минимум 1 цифру и 1 прописную букву.");
            while (true){
                outputInfo("Ведите имя пользователя:");
                dollar();
                login=reader.readLine();
                if (login==null){
                    outputInfo("Введен специальный символ. Завершение работы...");
                    System.exit(0);
                }
                if(temp.equals("sign_up") && login.length()<3){
                    outputInfo("Имя пользователя не может быть короче 3 символов.");
                    continue;
                }
                outputInfo("Ведите пароль:");
                dollar();
                password= String.valueOf(reader.readPassword());
                if (password==null){
                    outputInfo("Введен специальный символ. Завершение работы...");
                    System.exit(0);
                }
                if(temp.equals("sign_up") && (password.length()<5 || !password.matches(".*\\d+.*") || !password.matches(".*[A-Z].*"))){
                    outputInfo("Пароль не соответствует требованиям: пароль должен быть не менее 5 символов в длинну, содержать как минимум 1 цифру и 1 прописную букву.");
                    continue;
                }
                if(temp.equals("log_in")){
                    try {
                        toServer.write(SerializeManager.toByte(new ClientRequest(new LogIn(), login, "%")));
                        byte[] b = new byte[100000];
                        fromServer.read(b);
                        sr = (ServerResponse) SerializeManager.fromByte(b);
                        if (sr.isAccess()) {
                            password = password + sr.getAdditionalInfo();
                            messageDigest.reset();
                            messageDigest.update(password.getBytes());

                            password = DatatypeConverter.printHexBinary(messageDigest.digest());

                            toServer.write(SerializeManager.toByte(new ClientRequest(new LogIn(), login, password)));
                            b = new byte[100000];
                            fromServer.read(b);
                            sr = (ServerResponse) SerializeManager.fromByte(b);
                            if (sr.isAccess()) {
                                outputInfo("Авторизация успешна");
                                dollar();
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

                                reader = System.console();
                                foo=true;
                                break;
                            } catch (Exception ignored) {
                                Thread.sleep(500);
                            }
                        }
                        if(!foo){
                            outputInfo("Ошибка подключения. Завершение работы...");
                            System.exit(1);
                        }
                    }
                }
                if(temp.equals("sign_up")){
                    try {
                    outputInfo("Повторите пароль:");
                    dollar();
                    String temppsw= String.valueOf(reader.readPassword());
                    if(temppsw.equals(password)) {

                        toServer.write(SerializeManager.toByte(new ClientRequest(new SignUp(), login, "%")));
                        byte[] b = new byte[100000];
                        fromServer.read(b);
                        sr = (ServerResponse) SerializeManager.fromByte(b);
                        if (sr.isAccess()) {

                            String email;
                            while (true) {
                                outputInfo("Ведите корректный e-mail, либо \'no\' для отказа ввода:");
                                dollar();
                                email = String.valueOf(reader.readLine());
                                if (email.equals("no") || email.matches(".*@.*\\..*")){
                                    break;
                                }
                            }
                            SignUp signUp = new SignUp();
                            signUp.setEmail(email);
                            if(!email.equals("no")) signUp.setPassword(password);
                            String passwordc = password;

                            password = password + sr.getAdditionalInfo();
                            messageDigest.reset();
                            messageDigest.update(password.getBytes());
                            password = DatatypeConverter.printHexBinary(messageDigest.digest());
                            signUp.setSalt(sr.getAdditionalInfo());

                            toServer.write(SerializeManager.toByte(new ClientRequest(signUp, login, password)));
                            b = new byte[100000];
                            fromServer.read(b);
                            sr = (ServerResponse) SerializeManager.fromByte(b);
                            if (sr.isAccess()) {
                                outputInfo("Регистарция и авторизация успешна.");
/*                                if(!email.equals("no")) {
                                    try {
                                        sendEmail(email, passwordc, login);
                                        passwordc="";
                                    } catch (Exception e) {
                                        outputInfo("Ошибка отрпавки письма.");
                                    }
                                }*/
                                dollar();
                                break;
                            } else{
                                switch (sr.getCode()){
                                    case ERROR:
                                        outputInfo("Ошибка отпарвки письма на почту. Проверьте правильность ввода почты. ");
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

                                reader = System.console();
                                foo=true;
                                break;
                            } catch (Exception ignored) {
                                Thread.sleep(500);
                            }
                        }
                        if(!foo){
                            outputInfo("Ошибка подключения. Завершение работы...");
                            System.exit(1);
                        }
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
                        outputInfo("Введен специальный символ. Завершение работы...");
                        System.exit(1);
                    }
                    ICommand command = commandFactory.getCommand(currentCommand.trim().split(" "), reader, login);
                    if (command != null) {

                        toServer.write(SerializeManager.toByte(new ClientRequest(command,login,password)));

                        byte[] b = new byte[100000];
                        fromServer.read(b);
                        ServerResponse response = (ServerResponse) SerializeManager.fromByte(b);
                        serverResponseDecode(response);
                        dollar();
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

                            reader = System.console();
                            foo=true;
                            outputInfo("Подключение восстановлено.");
                            dollar();
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
            outputInfo("Ошибка подключения к серверу. Завершение работы...");
            System.exit(1);
        }
    }

    public static void outputInfo(String text) {
        System.out.println(text);
    }
    public static void dollar(){
        System.out.print("$");
    }

    public static void serverResponseDecode(ServerResponse response) throws NullPointerException{
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
                for (ServerResponse r : response.getScriptReport()){
                    serverResponseDecode(r);
                }
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
                break;
            case SCRIPT_REC:
                outputInfo("В процессе выполнения скрипта юыла обнаружена рекурсия. Дальнейшее выполнение не производится."+response.getAdditionalInfo());
                break;
            case SCRIPT_FILE_ERR:
                outputInfo("Обнаружена ошибка чтения файла. Проверьте доступность и правильность пути к файлу.");
                break;
            case SCRIPT_COMMAND_ERR:
                outputInfo("В процессе выполнения была обнаружена ошибка в файле скрипта. Дальнейшее выполнение невозможно."+response.getAdditionalInfo());
                break;
            case SHOW:
                outputInfo("Элементы коллекции:\n"+response.getAdditionalInfo());
        }
    }
/*    public static void sendEmail(String mail, String password, String user) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Properties property = new Properties();
        property.load(ClassLoader.getSystemClassLoader().getResourceAsStream("email.properties"));

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(property.getProperty("email"), property.getProperty("password"));
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("no-reply@ProgLab.com"));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(mail));
        message.setSubject("Programming lab password and login");
        message.setText("Dear user,"
                + "\n\n Here are your login details for client-server app:\n\npassword: " + password + "\nlogin: " + login + "\n\nAll the best,\n helios");

        Transport.send(message);
        outputInfo("Email sent");
    }*/
}
