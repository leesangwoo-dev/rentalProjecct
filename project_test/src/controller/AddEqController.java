package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

import dao.EquipmentDAO;
import dao.RentalOfficeDAO;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import model.EquipmentViewDTO;
import utils.Session;

public class AddEqController {

	@FXML
	private ComboBox<String> guComboBox, officeComboBox; // 콤보박스 지역(구), 대여소(동)
	@FXML
	private Label dragLabel, phoneLabel; // 라벨(drag/drop), 대여소전화번호
	@FXML
	private StackPane imageDropPane; // 이미지 drag/drop 영역
	@FXML
	private ImageView imagePreview; // 이미지뷰
	@FXML
	private Button chooseImageButton, btnNewEq, AddButton; // 이미지 파일 선택 버튼, 새 장비, 장비 추가
	// 장비명, 일련번호, 대여료(1일기준), 장비가격, 장비취득일
	@FXML
	private TextField eqNameField, serialNumField, costField, unitPriceField, acquisitionDateField;
	@FXML
	private TextArea eqInfoArea; // 장비 설명 영역

	@FXML
	private ComboBox<String> eqComboBox; // 기존 장비 불러오는 콤보박스
	@FXML
	private HBox newEqPane;	// 새로운 장비 추가

	private File selectedImageFile; // 선택된 이미지 파일
	private RentalOfficeDAO rentalOfficeDAO = new RentalOfficeDAO(); // 대여소DAO
	private final EquipmentDAO eqDAO = new EquipmentDAO(); // 기존 장비 정보 불러오기

	// init
	@FXML
	public void initialize() {
		setLocationInfo();
		setEqListUp();
	}

	// 구, 대여소, 대여소 전화번호 세팅
	private void setLocationInfo() {
		// 구 목록
		List<String> guList = rentalOfficeDAO.getDistinctGuList();
		guComboBox.setItems(FXCollections.observableArrayList(guList));

		// 전체 대여소 목록
		List<String> officeList = rentalOfficeDAO.getAllOfficeNames();
		officeComboBox.setItems(FXCollections.observableArrayList(officeList));

		// 구 선택 시 대여소 필터링
		guComboBox.setOnAction(e -> {
			String selectedGu = guComboBox.getValue();
			if (selectedGu != null) {
				List<String> filteredOffices = rentalOfficeDAO.getOfficeNamesByGu(selectedGu);
				officeComboBox.setItems(FXCollections.observableArrayList(filteredOffices));
			}
		});

		// 대여소 선택 시 전화번호 표시
		officeComboBox.setOnAction(e -> {
			String selectedOffice = officeComboBox.getValue();
			if (selectedOffice != null) {
				String phone = rentalOfficeDAO.getPhoneByOfficeName(selectedOffice);
				phoneLabel.setText(phone);
			}
		});
	}

	// 장비 추가시 기존 장비 목록 불러오기
	private void setEqListUp() {
		eqComboBox.setItems(FXCollections.observableArrayList(eqDAO.getAllEqNames()));

		// 콤보 선택 ⇒ 상세 채움, 가격 필드 잠금
		eqComboBox.getSelectionModel().selectedItemProperty().addListener((obs, o, name) -> {
			if (name != null) {
				EquipmentViewDTO dto = eqDAO.findByName(name);
				costField.setText(String.valueOf(dto.getRentalFee()));
				unitPriceField.setText(String.valueOf(dto.getUnitPrice()));
				costField.setEditable(false);
				unitPriceField.setEditable(false);

				// 새 장비모드 초기화
				eqNameField.setVisible(false);
				eqNameField.setManaged(false);
			}
		});
	}

	//새 장비 버튼 -> 기존 장비 목록에서 새로운 장비 명 입력란 및 단가랑 대여료 수정 입력 가능
	@FXML
	private void handleNewEq(ActionEvent e) {
		// 콤보박스 숨기고 TextField 노출
		eqComboBox.getSelectionModel().clearSelection();
		eqComboBox.setVisible(false);
		eqComboBox.setManaged(false);

		eqNameField.clear();
		eqNameField.setVisible(true);
		eqNameField.setManaged(true);

		// 가격 필드 편집 가능 + 초기화
		costField.clear();
		costField.setEditable(true);
		unitPriceField.clear();
		unitPriceField.setEditable(true);
	}

	// 장비 목록 리프래시용 부모?(장비목록) 컨트롤러 받아오기 용도
	private AdminViewController adminController;
	public void setMainController(AdminViewController adminController) {
		this.adminController = adminController;
	}

	// 드래그 오버 이벤트 핸들러(이미지 이동)
	@FXML
	private void handleDragOver(DragEvent event) {
		if (event.getGestureSource() != imageDropPane && event.getDragboard().hasFiles()) {
			event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
		}
		event.consume();
	}

	// 드래그드랍 핸들러(이미지 첨부)
	@FXML
	private void handleDragDropped(DragEvent event) {
		Dragboard db = event.getDragboard();
		boolean success = false;

		if (db.hasFiles()) {
			selectedImageFile = db.getFiles().get(0);
			loadImagePreview(selectedImageFile);
			success = true;
		}
		event.setDropCompleted(success);
		event.consume();
	}

	//이미지 첨부 버튼 핸들러
	@FXML
	private void handleImageSelect(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("이미지 선택");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
		File file = fileChooser.showOpenDialog(imageDropPane.getScene().getWindow());

		if (file != null) {
			selectedImageFile = file;
			loadImagePreview(selectedImageFile);
		}
	}

	// 첨부한 이미지 이미지뷰에 뿌려주기
	private void loadImagePreview(File file) {
		if (file != null && file.exists()) {
			imagePreview.setImage(new Image(file.toURI().toString()));

			// ▶ Drag/Drop 안내와 버튼 숨기기
			dragLabel.setVisible(false);
			chooseImageButton.setVisible(false);
		}
	}


	// 장비 추가 버튼
	@FXML
	private void handleSave(ActionEvent event) {
		// 권한 체크
		if (!Session.userGu.equals(guComboBox.getValue())) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("권한 오류");
			alert.setHeaderText(null);
			alert.setContentText("본인이 소속된 지역(구)의 장비만 등록할 수 있습니다.");
			alert.showAndWait();
			return;
		}

		//장비명 공백 여부 체크
		boolean isNew = eqNameField.isVisible();
		String eqName = isNew ? eqNameField.getText().trim() : eqComboBox.getValue();

		if (eqName == null || eqName.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("입력 오류");
			alert.setHeaderText(null);
			alert.setContentText("장비명을 입력 또는 선택해 주세요.");
			alert.showAndWait();
			return;
		}

		String serial = serialNumField.getText();
		String eqInfo = eqInfoArea.getText();
		int rentalFee = Integer.parseInt(costField.getText());
		int unitPrice = Integer.parseInt(unitPriceField.getText());
		LocalDate getDate = LocalDate.parse(acquisitionDateField.getText());
		String officeName = officeComboBox.getValue();

		if (selectedImageFile == null || officeName == null)
			return;

		String imagePath = saveImageToUploads(selectedImageFile);

		RentalOfficeDAO officeDAO = new RentalOfficeDAO();
		int officeId = officeDAO.getOfficeIdByName(officeName);

		Integer eqNum;
		if (isNew) {
			eqNum = eqDAO.insertEquipmentAndGetId(eqName, eqInfo, unitPrice, rentalFee);
		} else {
			eqNum = eqDAO.getEqNumByName(eqName);
		}

		boolean success = eqDAO.insertEachEq(serial, eqNum, officeId, "사용가능", getDate, imagePath);

		// 성공시 장비 목록 업데이트 및 폼 리셋
		if (success) {
			System.out.println("장비 등록 성공");
			adminController.loadTableData("");
			resetForm();
		} else {
			System.out.println("장비 등록 실패");
		}
	}

	// 리셋 용도
	private void resetForm() {
		eqComboBox.setVisible(true);
		eqComboBox.setManaged(true);
		eqNameField.setVisible(false);
		eqNameField.setManaged(false);
		eqNameField.clear();
		costField.clear();
		costField.setEditable(false);
		unitPriceField.clear();
		unitPriceField.setEditable(false);
		serialNumField.clear();
		acquisitionDateField.clear();
		eqInfoArea.clear();
		imagePreview.setImage(null);
		dragLabel.setVisible(true);
		chooseImageButton.setVisible(true);
		selectedImageFile = null;
	}

	// uploads파일에 이미지 파일 복사 및 DB저장용 문자열 반환
	public String saveImageToUploads(File originalFile) {
		if (originalFile == null)
			return null;

		String fileName = System.currentTimeMillis() + "_" + originalFile.getName();
		Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "img", "equipment");
		Path targetPath = uploadDir.resolve(fileName);

		try {
			Files.createDirectories(uploadDir);
			Files.copy(originalFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
			return "uploads/img/equipment/" + fileName; // DB에 저장
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
