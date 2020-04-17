import Instruments.ServerResponse;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {
    public static BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
    public static void main(String[] args) {

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(9);
        executor.execute(new DDosClient("script.txt"));
        executor.execute(new DDosClient("script.txt"));
        executor.execute(new DDosClient("script.txt"));
        executor.execute(new DDosClient("script.txt"));
        executor.execute(new DDosClient("script.txt"));
        executor.execute(new DDosClient("script.txt"));
        executor.execute(new DDosClient("script.txt"));
        executor.execute(new DDosClient("script.txt"));
        executor.execute(new DDosClient("script.txt"));
    }
    public static void outputInfo(String text) {
        System.out.println(text);
    }
    public static void serverResponseDecode(ServerResponse response) throws NullPointerException {
        switch (response.getCode()) {
            case SEARCH_OK:
                outputInfo("Результаты поиска:\n" + response.getAdditionalInfo() + "\n");
                break;
            case COUNT:
                outputInfo("Результаты подсчета:\n" + response.getAdditionalInfo() + "\n");
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
                for (ServerResponse r : response.getScriptReport()) {
                    serverResponseDecode(r);
                }
                break;
            case SERVER_FATAL_ERROR:
                outputInfo("\nКритическая ошибка сервера. Завершение работы...");
                System.exit(0);
                break;
            case TEXT_ONLY:
                outputInfo("Ответ сервера: \n" + response.getAdditionalInfo() + "\n");
                break;
            case EXIT:
                outputInfo("\nЗавершение работы...");
                System.exit(0);
                break;
            case CONNECTED:
                outputInfo("Соединение установлено");
                break;
            case SURPRISE_NOT_CORRECT_LOGIN_OR_PASSWORD:
                outputInfo("Каким-то образом вы пытались выполнить команду, введя неверные данные для входа! Хакерам здесь не рады!:)");
                break;
            case SCRIPT_REC:
                outputInfo("В процессе выполнения скрипта юыла обнаружена рекурсия. Дальнейшее выполнение не производится." + response.getAdditionalInfo());
                break;
            case SCRIPT_FILE_ERR:
                outputInfo("Обнаружена ошибка чтения файла. Проверьте доступность и правильность пути к файлу.");
                break;
            case SCRIPT_COMMAND_ERR:
                outputInfo("В процессе выполнения была обнаружена ошибка в файле скрипта. Дальнейшее выполнение невозможно." + response.getAdditionalInfo());
                break;
        }
    }
}
