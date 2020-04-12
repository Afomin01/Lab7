package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponse;
import Storable.Coordinates;
import Storable.Location;
import Storable.Route;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ExecuteScript implements ICommand {
    private String fileName;
    private String temp;
    private String CollectionFile;
    private int line = 0;
    private ArrayList<String> executeScriptStack= new ArrayList<>();
    private ServerResponse serverResponse;
    private String currentLine;
    private ICommand commandToExecute;
    private String user;

    public ExecuteScript(String fileName, String user) {
        this.fileName = fileName;
        this.user=user;
    }

    public void setCollectionFile(String collectionFile) {
        CollectionFile = collectionFile;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Execute_Script;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {
        if (executeScriptStack.contains(fileName)) {
            serverResponse.addText("\nОбнаружена рукурсия. Прерывание команды.");
            return serverResponse;
        }
        else {
            serverResponse = new ServerResponse();
            File file;
            try {
                file = new File(fileName);
            }catch (Exception e){
                return new ServerResponse("Некорректное имя файла");
            }
            try {
                if (!file.exists()) throw new FileNotFoundException();
                if (!file.canRead() || !file.canWrite()) throw new SecurityException();
                if (file.length() == 0){
                    serverResponse.addText("Файл пуст");
                    return serverResponse;
                }

                Scanner scanner = new Scanner(file);
                String[] SplitCommand;

                while(scanner.hasNext()) {
                    line++;
                    currentLine = scanner.nextLine();
                    SplitCommand = currentLine.trim().split(" ");
                    switch (SplitCommand[0]) {
                        case "":
                            break;
                        case "add":
                            if (SplitCommand.length == 1) {
                                Route adding = elementCreator(scanner);
                                if (adding != null){
                                    commandToExecute = new Add(adding);
                                    serverResponse.addText(commandToExecute.execute(manager).getText());
                                }
                                else throw new ScriptException();
                            } else throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                            break;
                        case "add_if_max":
                            if (SplitCommand.length == 1) {
                                Route adding = elementCreator(scanner);
                                if (adding != null){
                                    commandToExecute = new AddIfMax(adding);
                                    serverResponse.addText(commandToExecute.execute(manager).getText());
                                }
                                else throw new ScriptException();
                            } else throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                            break;
                        case "clean":
                            if (SplitCommand.length == 1){
                                commandToExecute = new Clear(user);
                                serverResponse.addText(commandToExecute.execute(manager).getText());
                            }
                            else throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                            break;
                        case "count_greater_than_distance":
                            if (SplitCommand.length == 2){
                                try {
                                    commandToExecute = new CountGreaterThanDistance(Double.parseDouble(SplitCommand[1]));
                                    serverResponse.addText(commandToExecute.execute(manager).getText());
                                }catch (NumberFormatException e){
                                    serverResponse.addText("Некорректный ввод числового значения в строке."+line+" Необходим double");
                                    return serverResponse;
                                }
                            }
                            else throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                            break;
                        case "history":
                            if (SplitCommand.length == 1){
                                commandToExecute = new History(user);
                                serverResponse.addText(commandToExecute.execute(manager).getText());
                            }
                            else throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                            break;
                        case "execute_script":
                            if (SplitCommand.length == 2){
                                temp = fileName;
                                executeScriptStack.add(SplitCommand[1]);
                                fileName=SplitCommand[1];
                                execute(manager);
                                for(int i = executeScriptStack.size()-1; i>=0; i--){
                                    if(executeScriptStack.get(i).equals(fileName)) executeScriptStack.remove(i);
                                    break;
                                }
                                fileName = temp;
                            }
                            else throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                            break;
                        case "exit":
                            if (SplitCommand.length == 1){
                                serverResponse.setText("Завершение работы");
                                serverResponse.setShutdown(true);
                                return serverResponse;
                            }
                            else throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                        case "help":
                            if (SplitCommand.length == 1){
                                commandToExecute = new Help();
                                serverResponse.addText(commandToExecute.execute(manager).getText());
                            }
                            else throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                            break;
                        case "info":
                            if (SplitCommand.length == 1){
                                commandToExecute = new Info();
                                serverResponse.addText(commandToExecute.execute(manager).getText());
                            }
                            else throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                            break;
                        case "print_unique_distance":
                            if (SplitCommand.length == 1){
                                commandToExecute = new PrintUniqueDistance();
                                serverResponse.addText(commandToExecute.execute(manager).getText());
                            }
                            else throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                            break;
                        case "remove_all_by_distance":
                            try {
                                if (SplitCommand.length == 2) {
                                    commandToExecute = new RemoveAllByDistance(Double.parseDouble(SplitCommand[1]),user);
                                    serverResponse.addText(commandToExecute.execute(manager).getText());
                                } else
                                    throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                            }
                            catch (NumberFormatException e){
                                serverResponse.addText("Некорректный ввод числового значения в строке."+line+" Необходим double");
                                return serverResponse;
                            }
                            break;
                        case "remove_greater":
                            if (SplitCommand.length == 2){
                                Route adding = elementCreator(scanner);
                                if (adding != null){
                                    commandToExecute = new RemoveGreater( adding,user);
                                    serverResponse.addText(commandToExecute.execute(manager).getText());
                                }
                            }
                            else throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                            break;
                        case "save":
                            if (SplitCommand.length == 1){
                                Save save = new Save();
                                save.setFileName(CollectionFile,user);
                                commandToExecute = save;
                                serverResponse.addText(commandToExecute.execute(manager).getText());
                            }
                            else throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                            break;
                        case "show":
                            if (SplitCommand.length == 1){
                                commandToExecute = new Show();
                                serverResponse.addText(commandToExecute.execute(manager).getText());
                            }
                            else throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                            break;
                        case "remove_by_id":
                            try {
                                if (SplitCommand.length == 2) {
                                    commandToExecute = new RemoveById(Long.parseLong(SplitCommand[1]),user);
                                    serverResponse.addText(commandToExecute.execute(manager).getText());
                                } else
                                    throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                            }
                            catch (NumberFormatException e){
                                serverResponse.addText("Некорректный ввод числового значения в строке."+line+" Необходим double");
                                return serverResponse;
                            }
                            break;
                        case "update":
                            if (SplitCommand.length == 3){
                                try {
                                    Route adding = elementCreator(scanner);
                                    if (adding != null) {
                                        commandToExecute = new UpdateId(Long.parseLong(SplitCommand[1]), adding,user);
                                        serverResponse.addText(commandToExecute.execute(manager).getText());
                                    } else throw new ScriptException();
                                }catch (NumberFormatException e){
                                    serverResponse.addText("Некорректный ввод числового значения в строке."+line+" Необходим double");
                                    return serverResponse;
                                }
                            }
                            else throw new ScriptException(line, currentLine, "Слишком много или недостаточно аргументов для команды");
                            break;
                        default:
                            serverResponse.addText("Неизвестная команда в строке "+ line + ". Отмена выполнения скрипта");
                            return serverResponse;
                    }
                }
                if(executeScriptStack.size()==1){
                    return serverResponse;
                }
            } catch (FileNotFoundException e) {
                serverResponse.setText("Файл по указанному пути не существует");
            } catch (SecurityException e) {
                serverResponse.setText("Файл защищен от чтения/записи");
            }catch (ScriptException e){
                serverResponse.addText(e.getMessage());
            } catch (Exception e) {
                serverResponse.setText("Ошибка исполнения скрипта");
            }
        }
        return serverResponse;
    }

    private Route elementCreator(Scanner scanner) {
        try {
            Route adding = new Route();
            Coordinates coord = new Coordinates();
            Location Lfrom = new Location();
            Location Lto = new Location();
            adding.setId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
            adding.setCreationDate(new Date());
            line++;
            currentLine = scanner.nextLine();
            adding.setName(currentLine);
            line++;
            currentLine = scanner.nextLine();
            coord.setx(Double.parseDouble(currentLine));
            line++;
            currentLine = scanner.nextLine();
            coord.sety(Double.parseDouble(currentLine));
            adding.setCoordinates(coord);
            line++;
            currentLine = scanner.nextLine();
            Lfrom.setX(Integer.parseInt(currentLine));
            line++;
            currentLine = scanner.nextLine();
            Lfrom.setY(Long.parseLong(currentLine));
            line++;
            currentLine = scanner.nextLine();
            Lfrom.setName(currentLine);
            adding.setFrom(Lfrom);
            line++;
            currentLine = scanner.nextLine();
            Lto.setX(Integer.parseInt(currentLine));
            line++;
            currentLine = scanner.nextLine();
            Lto.setY(Long.parseLong(currentLine));
            line++;
            currentLine = scanner.nextLine();
            Lto.setName(currentLine);
            adding.setTo(Lto);
            line++;
            currentLine = scanner.nextLine();
            adding.setDistance(Double.parseDouble(currentLine));
            return adding;
        }
        catch (NumberFormatException e){
            serverResponse.setText("\nОбнаружен неверный формат числа в строке "+line+ "\n\t"+currentLine+"\n");
            return null;
        }
        catch (Exception e){
            serverResponse.setText("\nОбнаружена ошибка в строке" + line + "\n\t"+currentLine+"\n");
            return null;
        }
    }
}

class ScriptException extends Exception{
    public ScriptException(int lineNum, String line, String cause) {
        super("\nОбнаружена ошибка в строке "+lineNum + "\n\t"+line+"\n"+cause);
    }

    public ScriptException() {
        super("\nОтмена выполнения скрипта");
    }
}
