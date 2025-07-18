// src/controller/RootController.java
package controller;

import dao.UserDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignupController {
	
	@FXML private ChoiceBox<String> RegionChoiceBox;
	@FXML private TextField ID_text;
    @FXML private PasswordField PW_text;
    @FXML private TextField NAME_text;
    @FXML private TextField PHONE_text;
    @FXML private Label infolabel; 
    @FXML private Button ID_check;
    @FXML private Button signUpButton;

	@FXML
	public void initialize() {
		RegionChoiceBox.getItems().addAll("유성구", "중구", "서구", "동구", "대덕구");
		RegionChoiceBox.setValue("유성구"); // 기본 값
		Platform.runLater(() -> signUpButton.requestFocus());
	}

	UserDAO userDAO = null;
	
    public void Singup(ActionEvent event) {
        System.out.println("회원가입");
        
        if (ID_text.getText() == null || ID_text.getText().trim().isEmpty() ||
                PW_text.getText() == null || PW_text.getText().trim().isEmpty() ||
                NAME_text.getText() == null || NAME_text.getText().trim().isEmpty() ||
                PHONE_text.getText() == null || PHONE_text.getText().trim().isEmpty() ||
                RegionChoiceBox.getValue() == null || RegionChoiceBox.getValue().trim().isEmpty()
                || infolabel.getText().equals("이미 존재하는 아이디입니다."))
        {
        	Alert alert = new Alert(AlertType.WARNING);
        	alert.setTitle("경고");
        	alert.setHeaderText(null); // 제목 아래 소제목. 필요 없으면 null
        	alert.setContentText("다시 확인해주세요");
        	alert.showAndWait();
        }
        else
        {
        	userDAO = new UserDAO();
            userDAO.addUser(ID_text.getText(), PW_text.getText(), NAME_text.getText(), PHONE_text.getText(), RegionChoiceBox.getValue());
            
            Stage currentStage = (Stage)((Node)event.getSource()).getScene().getWindow();
            currentStage.close();
            showLoginWindow();
        }
    }
    
    public static void showLoginWindow() {
        try {
            Parent root = FXMLLoader.load(LoginController.class.getResource("/view/login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("로그인");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ID_Check()
    {
    	userDAO = new UserDAO();
    	boolean check = userDAO.isIdDuplicated(ID_text.getText());
    	if(check)
    	{
    		infolabel.setText("이미 존재하는 아이디입니다.");
    		infolabel.setStyle("-fx-text-fill: yellow; ");
    	}
    	else
    	{
    		infolabel.setText("사용가능한 아이디입니다.");
    		infolabel.setStyle("-fx-text-fill: green; ");
    	}
    }
    
    public void setStage(Stage newStage) {
    	newStage.setOnCloseRequest(event -> {
            // 회원가입 창 닫을 때 로그인 창 다시 열기
            showLoginWindow();
        });
    }

	


}
