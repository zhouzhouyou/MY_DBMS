package controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import util.client.Client;
import util.client.ClientHolder;
import util.dialog.Dialog;
import util.result.Result;
import util.result.ResultFactory;
import util.stage.ControlledStage;
import util.stage.StageController;

import java.net.URL;
import java.util.ResourceBundle;

import static util.Constant.*;
import static util.SQL.CONNECT;

public class LoginController implements Initializable, ControlledStage {
    public JFXTextField username;
    public JFXPasswordField passwordField;
    public StackPane root;
    private StageController stageController;
    private Client client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reset();
        client = ClientHolder.INSTANCE.getClient();
    }

    @Override
    public void setStageController(StageController stageController) {
        this.stageController = stageController;
    }

    public void login(ActionEvent actionEvent) {
        String userName = username.getText();
        String password = passwordField.getText();

        String sql = String.format("%s %s %s", CONNECT, userName, password);
        Result result = client.getResult(sql);
        if (result.code == ResultFactory.SUCCESS) {
            stageController.loadStage(MAIN_WINDOW, MAIN_WINDOW_RES);
            stageController.setStage(MAIN_WINDOW, LOGIN);
        } else {
            new Dialog(NOT_MATCH, root);
        }
    }

    public void reset() {
        username.setText("system");
        passwordField.setText("123456");
    }
}
