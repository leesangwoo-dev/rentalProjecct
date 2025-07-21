package controller;

import static util.Session.*;

import java.io.IOException;

import dao.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MyInfoController {

	@FXML
	private Button eqList;
	@FXML
	private TextField nameField;
	@FXML
	private TextField passwordField;
	@FXML
	private TextField newPasswordField;
	@FXML
	private TextField phoneField;
	@FXML
	private ChoiceBox<String> guChoiceBox;
	@FXML
	private Button updateButton;

	@FXML
	public void initialize() {
		nameField.setText(userName);
		phoneField.setText(userPhoneNumber);
		ObservableList<String> guOptions = FXCollections.observableArrayList("중구", "유성구", "서구", "동구", "대덕구");
		guChoiceBox.setItems(guOptions);
		guChoiceBox.setValue(userGu);
	}

	public void handleEqList(ActionEvent event) {
		try {
			// FXML 파일 로드 (패키지 경로 맞춰주세요!)
			Parent mainView = FXMLLoader.load(getClass().getResource("/view/root.fxml"));

			// 현재 창(Stage)을 얻어서 씬 변경
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(mainView));
			stage.setTitle("MainView");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleUpdateButton(ActionEvent event) {
		String name = nameField.getText();
		String oldPassword = passwordField.getText();
		String newPassword = newPasswordField.getText();
		String phoneNumber = phoneField.getText();
		String gu = guChoiceBox.getValue();
		UserDAO userDAO = new UserDAO();

		if (oldPassword.isEmpty()) {
			showAlert(AlertType.WARNING, "비밀번호 입력", "기존 비밀번호를 반드시 입력해주세요.");
			return;
		}

		// 새 비밀번호 필드가 비어있으면 null을 전달하여 기존 비밀번호를 유지하도록 처리
		String finalNewPassword = (newPassword != null && !newPassword.isEmpty()) ? newPassword : null;

		// UserDAO를 통해 DB 업데이트 프로시저 호출 (평문 버전)
		String updateStatus;
		try {
			updateStatus = userDAO.updateUserInfo(userId, oldPassword, name, phoneNumber, gu, finalNewPassword);
			// 결과에 따른 사용자 피드백
			switch (updateStatus) {
			case "SUCCESS":
				showAlert(AlertType.INFORMATION, "정보 수정", "사용자 정보가 성공적으로 수정되었습니다."); // 메시지 변경
				userPassword = finalNewPassword;
				userName = name;
				userPhoneNumber = phoneNumber;
				userGu = gu;
				passwordField.clear();
				newPasswordField.clear();
				// 현재 창 닫기
				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				stage.close(); // 현재 창을 닫습니다.
				break;
			case "WRONG_PASSWORD":
				showAlert(AlertType.ERROR, "정보 수정 실패", "기존 비밀번호가 올바르지 않습니다.");
				passwordField.clear();
				break;
			default:
				showAlert(AlertType.ERROR, "오류 발생", "사용자 정보 수정 중 오류가 발생했습니다:\n" + updateStatus); // 메시지 변경
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showAlert(AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
