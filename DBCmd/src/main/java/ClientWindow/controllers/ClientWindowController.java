package ClientWindow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientWindowController implements Initializable {
    public static FXMLLoader loader = new FXMLLoader(ClientWindowController.class.getResource("/ClientWindow.fxml"));
    public TabPane tabPane;
    public Button addWindow;
    public Button closeWindow;

    public void initialize(URL location, ResourceBundle resources) {
    }



    public void addWindow(MouseEvent mouseEvent) throws IOException {
        Socket socket = new Socket("localhost",10086);
        ClientController clientController = new ClientController(socket);
        tabPane.getTabs().add(clientController);
    }

    public void closeWindow(MouseEvent mouseEvent) {
    }
}
