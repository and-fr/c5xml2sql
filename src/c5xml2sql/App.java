package c5xml2sql;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;



public class App {

    public static final String MSG_INFO =
        "1) Select directory with XML files in Configuration menu,\n" +
        "2) Set SQL output file in Configuration menu,\n" +
        "3) Choose Convert from File menu.";

    private static final String MSG_ABOUT =
        "Civilization 5: XML to SQL code converter\n" +
        "v.1.2018.08.03 (c) Artisanix\n\n" +
        "https://forums.civfanatics.com/members/artisanix.250557/";
    
    public static String selectedDirPath;
    public static String selectedFilePath;
    public static List<String> xmlFiles = new ArrayList<>();
    public static Integer xmlFilesNum;
    public static String sqlCode = "";
    
    // progressBar settings
    public static Double progressIncrement;
    private static DoubleProperty progressData = new SimpleDoubleProperty(0.0);
    public static DoubleProperty progressDataProperty() { return progressData; }
    public static void setProgressIncrement () {
        progressData.setValue(0.0);
        if (xmlFilesNum > 0) {
            Double x = 1 / xmlFilesNum.doubleValue();
            progressIncrement = Math.round(x * 1000.0) / 1000.0;
            if (progressIncrement == 0.0) {
                progressIncrement = 0.001;
            }
        }
    }
    public static void resetProgress() { progressData.setValue(0.0); }
    public static void increaseProgress () {
        progressData.setValue(progressData.getValue() + progressIncrement);
    }
    
    

    /*
    MENU OPTIONS
    */
    
    
    // EXIT
    public static void menuExit() {
        System.exit(0);
    }


    // ABOUT
    public static void menuAbout() {
        Stage stage = C5xml2sql.getPrimaryStage();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(stage);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText(MSG_ABOUT);
        alert.showAndWait();
    }


    // SELECT FOLDER
    public static void menuSelectFolder() {
        Stage stage = C5xml2sql.getPrimaryStage();
        
        DirectoryChooser directoryChooser = new DirectoryChooser();
        java.io.File selectedDirectory = directoryChooser.showDialog(stage);
            
        if(selectedDirectory != null){
            selectedDirPath = selectedDirectory.getAbsolutePath();
            Log.add("\n\nFolder with XML files: " + selectedDirPath);
        }
    }


    // SELECT FILE
    public static void menuSelectFile() {
        Stage stage = C5xml2sql.getPrimaryStage();
        
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SQL files (*.sql)", "*.sql");
        fileChooser.getExtensionFilters().add(extFilter);
            
        java.io.File file = fileChooser.showSaveDialog(stage);
            
        if(file != null){
            selectedFilePath = file.getAbsolutePath();
            Log.add("\n\nOutput SQL file: " + selectedFilePath);
        }
    }

    
    // CONVERT
    public static void menuConvert() {

        if (App.isConfigured()) {
        
            Log.clear();
            resetProgress();
            Task task = new Task<Void>() {
                @Override public Void call() {

                    if (File.getXmlFiles()) {
                        Xml2Sql.parse();
                    }
                    
                    if (sqlCode.isEmpty()){
                        Log.add("\n\nNO_SQL_CODE_GENERATED");
                    } else {
                        File.save(sqlCode, selectedFilePath);
                    }
                    // System.out.println("serious code here...");

                return null;
                }
            };
            new Thread(task).start();
        }
    }


    
    
    public static Boolean isConfigured(){
        Stage stage = C5xml2sql.getPrimaryStage();
        String errConfigMsg = "";
        Boolean isConfigured = true;

        if(App.selectedDirPath == null || App.selectedDirPath.isEmpty()){
            errConfigMsg = errConfigMsg + " No XML directory set. ";
            isConfigured = false;
        }
        if(App.selectedFilePath == null || App.selectedFilePath.isEmpty()){
            errConfigMsg = errConfigMsg + " No SQL output file set. ";
            isConfigured = false;
        }

        if (!"".equals(errConfigMsg)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(errConfigMsg);
            alert.showAndWait();
        }
        
        return isConfigured;
    }

}
