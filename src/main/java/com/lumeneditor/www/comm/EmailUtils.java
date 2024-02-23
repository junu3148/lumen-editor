package com.lumeneditor.www.comm;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUtils {


    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final int CODE_LENGTH = 6; // 인증번호 길이 상수로 정의
    private static final int RANDOM_BOUND = 10; // 0부터 9까지의 랜덤 숫자 생성을 위한 상한값
    public static boolean isValidEmail(String email) {

        if (email == null) return false;

        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }


    // 기본적으로 6자리의 메일 인증번호 생성 메서드
    public static String createCode() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int number = ThreadLocalRandom.current().nextInt(RANDOM_BOUND);
            builder.append(number); // 0부터 9까지의 랜덤한 숫자 추가
        }
        return builder.toString(); // 생성된 인증번호 반환
    }
}
