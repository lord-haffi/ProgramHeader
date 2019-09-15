/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.LogRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author leon
 */
public class ErrorReportController implements Initializable {

    private ObservableList<LogRecord> masterData = FXCollections.observableArrayList();
    
    @FXML
    private TableView<?> tableRecords;

    @FXML
    private TableColumn<?, ?> colNr;
    @FXML
    private TableColumn<?, ?> colMessage;
    @FXML
    private TableColumn<?, ?> colLevel;
    @FXML
    private TableColumn<?, ?> colClass;
    @FXML
    private TableColumn<?, ?> colMethod;
    @FXML
    private TableColumn<?, ?> colLogger;
    @FXML
    private TableColumn<?, ?> colTime;
    @FXML
    private TableColumn<?, ?> colThread;
    @FXML
    private TableColumn<?, ?> colResBundle;
    
    @FXML
    private TextArea textFieldError;

    @FXML
    private CheckBox checkboxOS;
    @FXML
    private TextField textFieldPath;
    @FXML
    private Button buttonBrowse;

    @FXML
    private Button buttonCancel;
    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonSend;
    
    public ErrorReportController (LogRecord[] lrs, int newest, int count, Object waiter)

    @FXML
    void browse(ActionEvent event) {

    }

    @FXML
    void cancel(ActionEvent event) {

    }
    @FXML
    void save(ActionEvent event) {

    }
    @FXML
    void send(ActionEvent event) {

    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
