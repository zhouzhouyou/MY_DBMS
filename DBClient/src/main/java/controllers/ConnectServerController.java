package controllers;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import org.apache.commons.lang3.math.NumberUtils;
import util.client.Client;
import util.client.ClientHolder;
import util.dialog.Dialog;
import util.stage.ControlledStage;
import util.stage.StageController;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import static util.Constant.*;

public class ConnectServerController implements Initializable, ControlledStage {
    public static FXMLLoader loader = new FXMLLoader(MainWindowController.class.getResource(SELECT_DATA_CENTER_RES));
    public JFXTextField serverIP;
    public JFXTextField serverPort;
    public StackPane root;
    private StageController stageController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reset();
    }

    public void connect(ActionEvent actionEvent) {
        String ip = serverIP.getText();
        String port = serverPort.getText();
        if (ip.length()==0 || !NumberUtils.isDigits(port)) {
            new Dialog(INVALID_DATA_RES, root);
            return;
        }

        try {
            Socket socket = new Socket(ip, Integer.parseInt(port));
            ClientHolder.INSTANCE.setClient(new Client(socket));
            stageController.loadStage(LOGIN, LOGIN_RES);
            stageController.setStage(LOGIN, SELECT_DATA_CENTER);
        } catch (IOException e) {
            new Dialog(TIME_OUT_RES, root);
        }
    }

    public void reset() {
        serverIP.setText("localhost");
        serverPort.setText("10086");
    }

    @Override
    public void setStageController(StageController stageController) {
        this.stageController = stageController;
    }
}
