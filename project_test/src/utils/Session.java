package utils;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextFormatter;

public class Session {
	// 로그인된 사용자의 정보
	public static Long userId;
    public static String userLoginId;
    public static String userPassword;
    public static String userName;
    public static String userPhoneNumber;
    public static String userGu;
    public static String userRole;


    // 비밀번호 필드에서 영어와 일부 특수문자 외에 입력 제어
	public static void applyEnglishOnlyTextFormatter(PasswordField passwordField) {
	    // 허용할 문자 패턴 정의 (영문 대소문자, 숫자, 일부 특수문자)
	    // 여기서는 !@#$%^&*()_+-=[]{};':"|,.<>/?`~ 허용
	    Pattern allowedChars = Pattern.compile("[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>/?`~]*");

	    UnaryOperator<TextFormatter.Change> filter = change -> {
	        // 변경될 텍스트가 허용된 패턴과 일치하는지 확인
	        if (allowedChars.matcher(change.getControlNewText()).matches()) {
	            return change;
	        } else {
	            return null;
	        }
	    };

	    TextFormatter<String> textFormatter = new TextFormatter<>(filter);
	    passwordField.setTextFormatter(textFormatter);
	}
}
