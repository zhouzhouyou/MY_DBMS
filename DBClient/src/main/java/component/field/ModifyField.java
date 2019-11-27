package component.field;

import com.jfoenix.controls.JFXSlider;
import controllers.MainWindowController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static util.Constant.MODIFY_FIELD_RES;

public class ModifyField extends AnchorPane implements Initializable {
    private MainWindowController controller;
    private String oldValue;
    public Text originValue;
    public JFXSlider newValue;

    public ModifyField(MainWindowController controller, String oldValue) {
        this.controller = controller;
        this.oldValue = oldValue;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MODIFY_FIELD_RES));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        originValue.setText(oldValue);
        newValue.setMin(Double.parseDouble(oldValue));
    }
}
