package controller;

import static utils.Session.applyEnglishOnlyTextFormatter;
import static utils.Session.userGu;
import static utils.Session.userLoginId;
import static utils.Session.userName;
import static utils.Session.userPassword;
import static utils.Session.userPhoneNumber;
import static utils.ShowAlert.showAlert;

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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class MyInfoController {

	@FXML
	private TextField nameTextField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private PasswordField newPasswordField;
	@FXML
	private TextField phoneNumberTextField;
	@FXML
	private ChoiceBox<String> guChoiceBox;

	@FXML
	public void initialize() {
		nameTextField.setText(userName);
		phoneNumberTextField.setText(userPhoneNumber);
		applyEnglishOnlyTextFormatter(passwordField);
		applyEnglishOnlyTextFormatter(newPasswordField);
		ObservableList<String> guOptions = FXCollections.observableArrayList("중구", "유성구", "서구", "동구", "대덕구");
		guChoiceBox.setItems(guOptions);
		guChoiceBox.setValue(userGu);
	}

	// 장비 목록 조회 페이지(메인페이지)
	public void handleEqList(ActionEvent event) {
		try {
			Parent mainView = FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(mainView));
			stage.setTitle("MainView");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 정보 수정 버튼
	@FXML
	private void handleUpdateButton(ActionEvent event) {
		String name = nameTextField.getText();
		String oldPassword = passwordField.getText();
		String newPassword = newPasswordField.getText();
		String phoneNumber = phoneNumberTextField.getText();
		String gu = guChoiceBox.getValue();
		UserDAO userDAO = new UserDAO();

		if (oldPassword.isEmpty()) {
			showAlert(AlertType.WARNING, "비밀번호 입력", "기존 비밀번호를 반드시 입력해주세요.");
			return;
		}

		try {
			// 새 비밀번호 필드가 비어있으면 null을 전달하여 기존 비밀번호를 유지하도록 처리
			String finalNewPassword = (newPassword != null && !newPassword.isEmpty()) ? newPassword : null;
			// UserDAO를 통해 DB 업데이트 프로시저 호출
			String updateStatus = userDAO.updateUserInfo(userLoginId, oldPassword, name, phoneNumber, gu, finalNewPassword);
			// 결과에 따른 사용자 피드백
			switch (updateStatus) {
			case "SUCCESS":
				showAlert(AlertType.INFORMATION, "정보 수정", "사용자 정보가 성공적으로 수정되었습니다.");
				userPassword = finalNewPassword;
				userName = name;
				userPhoneNumber = phoneNumber;
				userGu = gu;
				passwordField.clear();
				newPasswordField.clear();
				// 현재 창 닫기
				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				stage.close();
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
			e.printStackTrace();
		}
	}
}
