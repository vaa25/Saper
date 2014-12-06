import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    private Level level;

    @FXML
    private RadioMenuItem mediumRadioMenuItem;

    @FXML
    private RadioMenuItem noviceRadioMenuItem;

    @FXML
    private Pane fieldPane;

    @FXML
    private Label bombsLeftLabel;

    @FXML
    private RadioMenuItem customRadioMenuItem;

    @FXML
    private RadioMenuItem professionalRadioMenuItem;
    @FXML
    private void professionalSelected(ActionEvent event){

    }

    @FXML
    private void mediumSelected(ActionEvent event){

    }

    @FXML
    private void noviceSelected(ActionEvent event){

    }

    @FXML
    private void customSelected(ActionEvent event){

    }
    @FXML
    void fieldPaneClicked(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
