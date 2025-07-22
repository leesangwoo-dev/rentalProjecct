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

public class SignupController {

	@FXML
	private ChoiceBox<String> RegionChoiceBox;
	@FXML
	private TextField IdTextField;
	@FXML
	private PasswordField PwTextField;
	@FXML
	private TextField NameTextField;
	@FXML
	private TextField PhoneTextField;
	@FXML
	private Label InfoLabel;
	@FXML
	private Button IdCheckButton;
	@FXML
	private Button SignupButton;

	@FXML
	public void initialize() {
		RegionChoiceBox.getItems().addAll("유성구", "중구", "서구", "동구", "대덕구");
		RegionChoiceBox.setValue("유성구"); // 기본 값
		Platform.runLater(() -> SignupButton.requestFocus());
	}

	UserDAO userDAO = null;

	public void Singup(ActionEvent event) {
		System.out.println("회원가입");

		if (IdTextField.getText() == null || IdTextField.getText().trim().isEmpty() || PwTextField.getText() == null
				|| PwTextField.getText().trim().isEmpty() || NameTextField.getText() == null
				|| NameTextField.getText().trim().isEmpty() || PhoneTextField.getText() == null
				|| PhoneTextField.getText().trim().isEmpty() || RegionChoiceBox.getValue() == null
				|| RegionChoiceBox.getValue().trim().isEmpty() || InfoLabel.getText().equals("이미 존재하는 아이디입니다.")) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("경고");
			alert.setHeaderText(null);
			alert.setContentText("다시 확인해주세요");
			alert.showAndWait();
		} else {
			userDAO = new UserDAO();

			// DTO 생성
			UserDTO newUser = new UserDTO(IdTextField.getText(), PwTextField.getText(), NameTextField.getText(),
					PhoneTextField.getText(), RegionChoiceBox.getValue());

			userDAO.addUser(newUser);

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

	public void IdCheckButton() {
		userDAO = new UserDAO();
		boolean check = userDAO.isIdDuplicated(IdTextField.getText());
		if (check) {
			InfoLabel.setText("이미 존재하는 아이디입니다.");
			InfoLabel.setStyle("-fx-text-fill: yellow; ");
		} else {
			InfoLabel.setText("사용가능한 아이디입니다.");
			InfoLabel.setStyle("-fx-text-fill: green; ");
		}
	}

	public void setStage(Stage newStage) {
		newStage.setOnCloseRequest(event -> {
			// 회원가입 창 닫을 때 로그인 창 다시 열기
			showLoginWindow();
		});
	}

}
