package Commands;

import java.io.Serializable;

public enum EAvailableCommands implements Serializable {
    Add("{element}","добавить новый элемент в коллекцию"),
    Add_If_Max("{element}","добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции"),
    Clear("","очистить коллекцию"),
    Count_Greater_Than_Distance("distance","вывести количество элементов, значение поля distance которых больше заданного"),
    Execute_Script("file_name","считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме"),
    Exit("","завершить программу (без сохранения в файл)"),
    Help("","вывести справку по доступным командам"),
    History("","вывести последние 7 команд (без их аргументов)"),
    Info("","вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д."),
    Print_Unique_Distance("distance","вывести уникальные значения поля distance"),
    Remove_All_By_Distance("distance","удалить из коллекции все элементы, значение поля distance которого эквивалентно заданному"),
    Remove_By_Id("id","удалить элемент из коллекции по его id"),
    Remove_Greater("{element}","удалить из коллекции все элементы, превышающие заданный"),
    Save("","сохранить коллекцию в файл"),
    Show("","вывести в стандартный поток вывода все элементы коллекции в строковом представлении"),
    Update("id, {element}","обновить значение элемента коллекции, id которого равен заданному");

    public final String arguments;
    public final String info;

    EAvailableCommands(String arguments, String label) {
        this.arguments = arguments;
        this.info = label;
    }

    public String getCommandInfo(){
        return name().toLowerCase()+":   "+arguments+"    "+info;
    }

    public String getArguments(){
        if(arguments==null) return "";
        else return "Аргументы: "+ arguments;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
