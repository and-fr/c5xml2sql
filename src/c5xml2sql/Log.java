package c5xml2sql;

import static c5xml2sql.LayoutMainController.layoutMainController;

public class Log {
    
    public static void add(String text){
        layoutMainController.appLog.appendText(text);
    }


    public static void clear(){
        layoutMainController.appLog.clear();
    }

}
