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
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import util.Session;

public class AddEqController {

	@FXML
	private ComboBox<String> guComboBox, officeComboBox;
	@FXML
	private Label dragLabel, phoneLabel;
	@FXML
	private StackPane imageDropPane;
	@FXML
	private ImageView imagePreview;
	@FXML
	private Button chooseImageButton;
	@FXML
	private TextField eqNameField, serialNumField, costField, unitPriceField, acquisitionDateField;
	@FXML
	private TextArea eqInfoArea;

	private File selectedImageFile;
	private RentalOfficeDAO rentalOfficeDAO = new RentalOfficeDAO();

	@FXML
	public void initialize() {
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

	private AdminViewController adminController;

	public void setMainController(AdminViewController adminController) {
		this.adminController = adminController;
	}

	@FXML
	private void handleDragOver(DragEvent event) {
		if (event.getGestureSource() != imageDropPane && event.getDragboard().hasFiles()) {
			event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
		}
		event.consume();
	}
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

	private void loadImagePreview(File file) {
		if (file != null && file.exists()) {
			imagePreview.setImage(new Image(file.toURI().toString()));

			// ▶ Drag/Drop 안내와 버튼 숨기기
			dragLabel.setVisible(false);
			chooseImageButton.setVisible(false);
		}
	}

	@FXML
	private void handleSave(ActionEvent event) {
		// ① 권한 체크
	    if (!Session.userGu.equals(guComboBox.getValue())) {
	        Alert alert = new Alert(Alert.AlertType.WARNING);
	        alert.setTitle("권한 오류");
	        alert.setHeaderText(null);
	        alert.setContentText("본인이 소속된 지역(구)의 장비만 등록할 수 있습니다.");
	        alert.showAndWait();          // 사용자가 확인을 눌러야 계속‑실행
	        return;                       // 권한이 없으므로 저장 중단
	    }
			


		String eqName = eqNameField.getText();
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
		EquipmentDAO eqDAO = new EquipmentDAO();

		int officeId = officeDAO.getOfficeIdByName(officeName); // office_name → office_id
		int eqNum = eqDAO.insertEquipmentAndGetId(eqName, eqInfo, unitPrice, rentalFee);

		boolean success = eqDAO.insertEachEq(serial, eqNum, officeId, "사용가능", getDate, imagePath);

		if (success) {
			System.out.println("장비 등록 성공");
			adminController.loadTableData(""); // 필요 시
		} else {
			System.out.println("장비 등록 실패");
		}
	}

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
