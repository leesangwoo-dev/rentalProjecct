package util;

public class Session {
	// 로그인된 사용자의 정보
	public static Long userId;
    public static String userLoginId;
    public static String userPassword;
    public static String userName;
    public static String userPhoneNumber;
    public static String userGu;
    public static String userRole;

    // 예: 로그인 상태 확인용 메서드
    public static boolean isLogIn() {
        return userLoginId != null;
    }
}
