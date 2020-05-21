package Commands;

import java.io.Serializable;

public enum EAvailableCommands implements Serializable {
    //provide command in format: disc bundle, name bundle, elements bundle separated with ','.
    Add("commands.disc.add","commands.name.add", "commands.args.element"),
    Add_If_Max("commands.disc.addIfMax","commands.name.addifmax","commands.args.element"),
    Clear("commands.disc.clear","commands.name.clear","commands.args.empty"),
    Count_Greater_Than_Distance("commands.disc.countGreaterThanDistance","commands.name.countgreaterthandistacne","commands.args.distance"),
    Execute_Script("commands.disc.executeScript","commands.name.executescript","commands.args.file_name"),
    Exit("commands.disc.exit","commands.name.exit","commands.args.empty"),
    Help("commands.disc.help","commands.name.help","commands.args.empty"),
    History("commands.disc.history","commands.name.history","commands.args.empty"),
    Info("commands.disc.info","commands.name.info","commands.args.empty"),
    Print_Unique_Distance("commands.disc.printUniqueDistance","commands.name.printuniquedistance", "commands.args.distance"),
    Remove_All_By_Distance("commands.disc.removeAllByDistance","commands.name.removeid", "commands.args.distance"),
    Remove_By_Id("commands.disc.removeById","commands.name.allbydistance","commands.args.id"),
    Remove_Greater("commands.disc.removeGreater","commands.name.removegreater", "commands.args.element"),
    Show("commands.disc.show","commands.name.show","commands.args.empty"),
    Update("commands.disc.update","commands.name.update","commands.args.id,commands.args.element"),
    Not_A_Command("","","");

    public final String args;
    public final String localizedResourceBundleDiscription;
    public final String localizedResourceBundleName;

    EAvailableCommands(String Discription, String Name, String args) {
        this.localizedResourceBundleDiscription = Discription;
        this.localizedResourceBundleName = Name;
        this.args=args;
    }

    public String getCommandInfo(){
        return name().toLowerCase()+":   "+ localizedResourceBundleDiscription +"    "+ localizedResourceBundleName;
    }

    public String getLocalizedResourceBundleDiscription(){
        if(localizedResourceBundleDiscription ==null) return "";
        else return "Аргументы: "+ localizedResourceBundleDiscription;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
