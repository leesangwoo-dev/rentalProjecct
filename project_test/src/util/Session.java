package util;

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

    /**
	 * 주어진 PasswordField에 영문, 숫자, 일부 특수문자만 허용하는 TextFormatter를 적용합니다.
	 * 한글 및 그 외 문자는 입력되지 않습니다.
	 * @param passwordField 적용할 PasswordField
	 */
	public static void applyEnglishOnlyTextFormatter(PasswordField passwordField) {
	    // 허용할 문자 패턴 정의 (영문 대소문자, 숫자, 일부 특수문자)
	    // 여기서는 !@#$%^&*()_+-=[]{};':"|,.<>/?`~ 와 공백을 허용했습니다.
	    // 필요에 따라 허용할 특수문자를 추가하거나 제거할 수 있습니다.
	    Pattern allowedChars = Pattern.compile("[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>/?`~]*");

	    UnaryOperator<TextFormatter.Change> filter = change -> {
	        // 변경될 텍스트가 허용된 패턴과 일치하는지 확인
	        if (allowedChars.matcher(change.getControlNewText()).matches()) {
	            return change; // 변경 허용
	        } else {
	            return null; // 변경 거부 (입력되지 않음)
	        }
	    };

	    TextFormatter<String> textFormatter = new TextFormatter<>(filter);
	    passwordField.setTextFormatter(textFormatter);
	}
}
