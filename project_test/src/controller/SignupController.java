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
<<<<<<< HEAD
	private ChoiceBox<String> guChoiceBox;
	@FXML
	private TextField idTextField;
	@FXML
	private PasswordField passwordTextField;
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
=======
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
>>>>>>> refs/heads/HYUNSEOK

	@FXML
	public void initialize() {
<<<<<<< HEAD
		guChoiceBox.getItems().addAll("유성구", "중구", "서구", "동구", "대덕구");
		guChoiceBox.setValue("유성구"); // 기본 값
		Platform.runLater(() -> signupButton.requestFocus());
=======
		RegionChoiceBox.getItems().addAll("유성구", "중구", "서구", "동구", "대덕구");
		RegionChoiceBox.setValue("유성구"); // 기본 값
		Platform.runLater(() -> SignupButton.requestFocus());
>>>>>>> refs/heads/HYUNSEOK
	}

	UserDAO userDAO = null;

	public void Singup(ActionEvent event) {
		System.out.println("회원가입");

<<<<<<< HEAD
		if (idTextField.getText() == null || idTextField.getText().trim().isEmpty()
				|| passwordTextField.getText() == null || passwordTextField.getText().trim().isEmpty()
				|| nameTextField.getText() == null || nameTextField.getText().trim().isEmpty()
				|| phoneNumberTextField.getText() == null || phoneNumberTextField.getText().trim().isEmpty()
				|| guChoiceBox.getValue() == null || guChoiceBox.getValue().trim().isEmpty()
				|| infoLabel.getText().equals("이미 존재하는 아이디입니다.")) {
=======
		if (IdTextField.getText() == null || IdTextField.getText().trim().isEmpty() || PwTextField.getText() == null
				|| PwTextField.getText().trim().isEmpty() || NameTextField.getText() == null
				|| NameTextField.getText().trim().isEmpty() || PhoneTextField.getText() == null
				|| PhoneTextField.getText().trim().isEmpty() || RegionChoiceBox.getValue() == null
				|| RegionChoiceBox.getValue().trim().isEmpty() || InfoLabel.getText().equals("이미 존재하는 아이디입니다.")) {
>>>>>>> refs/heads/HYUNSEOK
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("경고");
			alert.setHeaderText(null);
			alert.setContentText("다시 확인해주세요");
			alert.showAndWait();
		} else {
			userDAO = new UserDAO();

			// DTO 생성
<<<<<<< HEAD
			UserDTO newUser = new UserDTO(idTextField.getText(), passwordTextField.getText(), nameTextField.getText(),
					phoneNumberTextField.getText(), guChoiceBox.getValue());
=======
			UserDTO newUser = new UserDTO(IdTextField.getText(), PwTextField.getText(), NameTextField.getText(),
					PhoneTextField.getText(), RegionChoiceBox.getValue());
>>>>>>> refs/heads/HYUNSEOK

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

<<<<<<< HEAD
	public void idCheck() {
		userDAO = new UserDAO();
		boolean check = userDAO.isIdDuplicated(idTextField.getText());
		if (check) {
			infoLabel.setText("이미 존재하는 아이디입니다.");
			infoLabel.setStyle("-fx-text-fill: yellow; ");
		} else {
			infoLabel.setText("사용가능한 아이디입니다.");
			infoLabel.setStyle("-fx-text-fill: green; ");
=======
	public void IdCheckButton() {
		userDAO = new UserDAO();
		boolean check = userDAO.isIdDuplicated(IdTextField.getText());
		if (check) {
			InfoLabel.setText("이미 존재하는 아이디입니다.");
			InfoLabel.setStyle("-fx-text-fill: yellow; ");
		} else {
			InfoLabel.setText("사용가능한 아이디입니다.");
			InfoLabel.setStyle("-fx-text-fill: green; ");
>>>>>>> refs/heads/HYUNSEOK
		}
	}

	public void setStage(Stage newStage) {
		newStage.setOnCloseRequest(event -> {
			// 회원가입 창 닫을 때 로그인 창 다시 열기
			showLoginWindow();
		});
	}

}
