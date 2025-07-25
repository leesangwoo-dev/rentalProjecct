// src/controller/RootController.java
package controller;

import static utils.DBUtil.getConnection;
import static utils.Session.*;

import java.sql.Connection;

import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// 로그인 페이지 컨트롤러
public class LoginController {

	@FXML
	private TextField idTextField;
	@FXML
	private PasswordField passwordField;

	// 로그인 속도 향상을 위한 DB 연결 
	Connection conn;

	@FXML
	public void initialize() {
		try {
			conn = getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 비밀번호 필드에 TextFormatter 적용
	    applyEnglishOnlyTextFormatter(passwordField);
	}

	// 로그인 후 성공하면 메인화면으로 가는 메서드 호출
	public void Login(ActionEvent event) {
		if (handleLogin(event)) {
			try {
				Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				currentStage.close();
				MainStage(userRole);
				conn.close(); // 로그인이 성공하면 LoginController에서 연결한 DB Connection 종료
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return;
		}
	}
	
	// 메인화면 띄워주는 메서드
	private void MainStage(String role) throws Exception {
		Stage newStage = new Stage();
		Parent parent = null;
		if(role.equals("ADMIN")) {
			parent = FXMLLoader.load(getClass().getResource("/view/AdminView.fxml"));
		} else {
			parent = FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
		}
		Scene scene = new Scene(parent);
		newStage.setTitle("Rental Program");
		newStage.setScene(scene);
		newStage.show();
	}

	// 회원가입을 클릭했을 때 로그인 화면을 숨기고 회원가입 창을 띄우는 메서드 호출
	public void signup(ActionEvent event) {
		try {
			Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
			currentStage.hide();

			signUpPopup();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 회원가입창을 띄우는 메서드
	public void signUpPopup() throws Exception {

		Stage newStage = new Stage();
		Parent parent = FXMLLoader.load(getClass().getResource("/view/SignupView.fxml"));
		Scene dd = new Scene(parent);

		newStage.setTitle("회원가입");
		newStage.setScene(dd);
		SignupController sc = new SignupController();
		sc.setStage(newStage);
		newStage.show();

	}

	// 로그인 시도 메서드
	public boolean handleLogin(ActionEvent event) {
		String userId = idTextField.getText();
		String password = passwordField.getText();

		UserDAO userDao = new UserDAO();
		if (userDao.isLoginValid(userId, password)) {
			// 로그인 성공
			return true;
			// 다음 화면으로 전환 등
		} else {
			// 로그인 실패
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("로그인 실패");
			alert.setHeaderText(null);
			alert.setContentText("아이디 또는 비밀번호가 잘못되었습니다.");
			alert.showAndWait();
			return false;
		}
	}
}
