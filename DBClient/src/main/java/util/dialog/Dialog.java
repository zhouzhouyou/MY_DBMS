package util.dialog;

import com.jfoenix.controls.JFXDialog;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class Dialog {
    public Dialog(String resource, StackPane rootStackPane) {
        JFXDialog dialog = new JFXDialog();
        try {
            FXMLLoader fxmlLoader =new FXMLLoader(getClass().getResource(resource));
            Parent root = fxmlLoader.load();
            Region content = (Region) root;
            dialog.setContent(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.show(rootStackPane);
    }
}
