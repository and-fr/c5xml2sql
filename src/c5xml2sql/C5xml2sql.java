package c5xml2sql;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class C5xml2sql extends Application {
    
    private static Stage pStage;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // prepare scene
        Parent root = FXMLLoader.load(getClass().getResource("LayoutMain.fxml"));
        Scene scene = new Scene(root);
        
        // prepare stage
        pStage = primaryStage;
        pStage.setTitle("C5xml2sql");
        pStage.setScene(scene);
        pStage.show();
    }
    
    
    public static Stage getPrimaryStage(){
        return pStage;
    }

}
