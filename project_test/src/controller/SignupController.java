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
import model.UserDTO;
import static util.Session.applyEnglishOnlyTextFormatter;

public class SignupController {

	@FXML
	private ChoiceBox<String> guChoiceBox;
	@FXML
	private TextField idTextField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private TextField nameTextField;
	@FXML
	private TextField phoneNumberTextField;
	@FXML
	private Label infoLabel;
	@FXML
	private Button idCheckButton;
	@FXML
	private Button signupButton;

	@FXML
	public void initialize() {
		guChoiceBox.getItems().addAll("유성구", "중구", "서구", "동구", "대덕구");
		guChoiceBox.setValue("유성구"); // 기본 값
		// 비밀번호 필드에 TextFormatter 적용
	    applyEnglishOnlyTextFormatter(passwordField);
		Platform.runLater(() -> signupButton.requestFocus());
	}

	UserDAO userDAO = null;

	public void Singup(ActionEvent event) {
		if (idTextField.getText() == null || idTextField.getText().trim().isEmpty() || passwordField.getText() == null
				|| passwordField.getText().trim().isEmpty() || nameTextField.getText() == null
				|| nameTextField.getText().trim().isEmpty() || phoneNumberTextField.getText() == null
				|| phoneNumberTextField.getText().trim().isEmpty() || guChoiceBox.getValue() == null
				|| guChoiceBox.getValue().trim().isEmpty() || infoLabel.getText().equals("이미 존재하는 아이디입니다.")) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("경고");
			alert.setHeaderText(null);
			alert.setContentText("다시 확인해주세요");
			alert.showAndWait();
		} else {
			userDAO = new UserDAO();

			// DTO 생성
			UserDTO newUser = new UserDTO(idTextField.getText(), passwordField.getText(), nameTextField.getText(),
					phoneNumberTextField.getText(), guChoiceBox.getValue());

			userDAO.addUser(newUser);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("성공");
			alert.setHeaderText(null);
			alert.setContentText("회원가입이 성공하셨습니다");
			alert.showAndWait();

			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();
			showLoginWindow();
		}
	}

	public static void showLoginWindow() {
		try {
			Parent root = FXMLLoader.load(LoginController.class.getResource("/view/LoginView.fxml"));
			Stage stage = new Stage();
			stage.setTitle("로그인");
			stage.setScene(new Scene(root));
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void idCheckButton() {
		userDAO = new UserDAO();
		boolean check = userDAO.isIdDuplicated(idTextField.getText());
		if (check) {
			infoLabel.setText("이미 존재하는 아이디입니다.");
			infoLabel.setStyle("-fx-text-fill: yellow; ");
		} else {
			infoLabel.setText("사용가능한 아이디입니다.");
			infoLabel.setStyle("-fx-text-fill: green; ");
		}
	}

	public void setStage(Stage newStage) {
		newStage.setOnCloseRequest(event -> {
			// 회원가입 창 닫을 때 로그인 창 다시 열기
			showLoginWindow();
		});
	}

}
