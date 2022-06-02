package c5xml2sql;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;


public class LayoutMainController implements Initializable {
    
    public static LayoutMainController layoutMainController;
    
    @FXML public TextArea appLog;
    @FXML private ProgressBar  progressBar;

    @FXML private void handleMenuExit(ActionEvent event) { App.menuExit(); }
    @FXML private void handleMenuAbout(ActionEvent event) { App.menuAbout(); }
    @FXML private void handleMenuSelectFolder(ActionEvent event) { App.menuSelectFolder(); }
    @FXML private void handleMenuSelectFile(ActionEvent event) { App.menuSelectFile(); }
    @FXML private void handleMenuConvert(ActionEvent event) { App.menuConvert(); }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        layoutMainController = this;
        progressBar.progressProperty().bind(App.progressDataProperty());

        Log.add(App.MSG_INFO);
       
    }    
 
}
