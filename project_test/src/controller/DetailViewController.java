package controller;

import static util.Session.userLoginId;

import java.time.LocalDate;

import dao.DeatialViewDAO;
import dao.RentalDAO;
import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.DetailViewDTO;
import model.RentalDTO;

// 상세페이지 컨트롤러
public class DetailViewController {

	@FXML
	private Label eqNameLabel; // 장비 이름
	@FXML
	private Label stateLabel; // 장비 상태
	@FXML
	private TextArea eqInfoArea; // 장비 설명 + 취득일 포함

	@FXML
	private ImageView toolImageView; // 장비 이미지

	@FXML
	private Label SerialNumLabel; // 일련번호
	@FXML
	private Label costLabel; // 대여료
	@FXML
	private Label officeLabel; // 대여소
	@FXML
	private Label phoneLabel; // 전화번호

	@FXML
	private DatePicker rentDatePicker; // 대여일
	@FXML
	private DatePicker returnDatePicker; // 반납일

	private final DeatialViewDAO dao = new DeatialViewDAO();
	private final RentalDAO rentalDAO = new RentalDAO();
	private final UserDAO userDAO = new UserDAO();

	private MainViewController mainController;

	// 수정 시 mainView 리프래쉬 용도
	public void setMainController(MainViewController mainController) {
		this.mainController = mainController;
	}

	// 일련번호 및 장비 정보 설정
	public void setSerialNumber(String serialNum) {
		DetailViewDTO dto = dao.getEachEquipmentDetail(serialNum);
		if (dto == null)
			return;

		// 장비 이름 및 상태
		eqNameLabel.setText(dto.getEqName());
		stateLabel.setText(dto.getState());

		// 상태별 스타일
		// 추후 case 명 변경 필요 "대여중"? "대여 가능" "수리?"
		String state = dto.getState();
		switch (state) {
		case "사용가능":
			stateLabel.setStyle(
					"-fx-background-color: #B5F3C1; -fx-text-fill: green; -fx-alignment: center; -fx-font-size: 18px; -fx-padding: 2 6; -fx-border-radius: 4; -fx-background-radius: 4;");
			break;
		case "대여중":
			stateLabel.setStyle(
					"-fx-background-color: #FFB6B6; -fx-text-fill: red; -fx-alignment: center; -fx-font-size: 18px; -fx-padding: 2 6; -fx-border-radius: 4; -fx-background-radius: 4;");
			break;
		case "수리":
			stateLabel.setStyle(
					"-fx-background-color: #FFF6B3; -fx-text-fill: goldenrod; -fx-alignment: center; -fx-font-size: 18px; -fx-padding: 2 6; -fx-border-radius: 4; -fx-background-radius: 4;");
			break;
		default:
			stateLabel.setStyle("-fx-background-color: lightgray; -fx-text-fill: black;");
		}

		// 장비 설명 + 취득일
		String fullInfo = String.format("%s\n\n장비 취득일: %s", dto.getEqInfo(),
				dto.getGetDate() != null ? dto.getGetDate().toString() : "정보 없음");
		eqInfoArea.setText(fullInfo);

		// 하단 정보 라벨 설정
		SerialNumLabel.setText(dto.getSerialNum());
		costLabel.setText(dto.getFee() + "원");
		officeLabel.setText(dto.getRentalOffice());
		phoneLabel.setText(dto.getPhone());

		// 날짜 초기화 (기본 오늘 날짜)
		rentDatePicker.setValue(LocalDate.now());
		returnDatePicker.setValue(LocalDate.now().plusDays(1)); // 기본 1일 대여

		// 이미지 처리
		try {
			if (dto.getImgPath() != null) {
				toolImageView.setImage(new Image(dto.getImgPath()));
				System.out.println(dto.getImgPath());
			} else {
				toolImageView.setImage(null);
			}
		} catch (Exception e) {
			toolImageView.setImage(null);
			System.out.println("이미지 로딩 실패: " + e.getMessage());
		}
	}

	// 대여하기
	@FXML
	private void handleRent() {
		// 유효성 검사
		if (rentDatePicker.getValue() == null || returnDatePicker.getValue() == null) {
			showAlert(Alert.AlertType.WARNING, "날짜를 모두 선택해주세요.");
			return;
		}
		if (returnDatePicker.getValue().isBefore(rentDatePicker.getValue())) {
			showAlert(Alert.AlertType.WARNING, "반납일은 대여일보다 이후여야 합니다.");
			return;
		}

		RentalDTO rental = new RentalDTO();
		rental.setUserId(userDAO.getUserIdByLoginID(userLoginId)); // 로그인한 사용자 ID 가져오기
		rental.setSerialNum(SerialNumLabel.getText());
		rental.setRentalDate(rentDatePicker.getValue().atStartOfDay());
		rental.setReturnDate(returnDatePicker.getValue().atStartOfDay());
		rental.setReturnStatus("대여중");
		rental.setOverdueFee(0L);
		rental.setActualReturnDate(null); // 초기에는 null

		boolean success = rentalDAO.insertRental(rental);

		if (success) {
			showAlert(Alert.AlertType.INFORMATION, "대여가 완료되었습니다.");
		} else {
			showAlert(Alert.AlertType.ERROR, "대여에 실패했습니다.");
		}
		//mainController.loadTableData();
	}

	private void showAlert(Alert.AlertType type, String message) {
		Alert alert = new Alert(type);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
