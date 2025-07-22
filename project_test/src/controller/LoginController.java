// src/controller/RootController.java
package controller;

import static util.DBUtil.getConnection;
import static util.Session.userLoginId;

import java.sql.Connection;
import java.sql.SQLException;

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
	private TextField IdTextField;
	@FXML
	private PasswordField PwTextField;

	Connection conn;
	@FXML
	public void initialize()
	{
		try {
			conn = getConnection();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Login(ActionEvent event) {
		System.out.println("로그인 버튼 클릭");
		System.out.println(IdTextField.getText());
		System.out.println(PwTextField.getText());
		
		if(handleLogin(event))
		{
			try {
				Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				currentStage.close();
				userLoginId = IdTextField.getText();
				MainStage();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			return;
		}
		
	}

	public void signup(ActionEvent event) {
		System.out.println("회원 가입");
		try {
			Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
			currentStage.hide();

			signUpPopup();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void MainStage() throws Exception {

		Stage newStage = new Stage();
		//Parent parent = FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
		Parent parent = FXMLLoader.load(getClass().getResource("/view/AdminView.fxml"));
		Scene dd = new Scene(parent);

		newStage.setTitle("관리자 페이지");
		newStage.setScene(dd);
		newStage.show();

	}// end

	public void signUpPopup() throws Exception {

		Stage newStage = new Stage();
		Parent parent = FXMLLoader.load(getClass().getResource("/view/SignupView.fxml"));
		Scene dd = new Scene(parent);

		newStage.setTitle("회원가입");
		newStage.setScene(dd);
		SignupController sc = new SignupController();
		sc.setStage(newStage);
		newStage.show();

	}// end

	public boolean handleLogin(ActionEvent event) {
		String userId = IdTextField.getText();
		String password = PwTextField.getText();

		UserDAO userDao = new UserDAO();
		if (userDao.isLoginValid(conn, userId, password)) {
			// 로그인 성공
			System.out.println("로그인 성공");
			// 로그인 성공시 db연결 해제
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
