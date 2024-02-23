package com.lumeneditor.www.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;

public class CustomNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    /**
     * 엔티티의 필드 이름을 데이터베이스 컬럼 이름으로 변환합니다.
     * 이 변환 과정에서 CamelCase 이름이 snake_case 이름으로 변환됩니다.
     * 예를 들어, "userName" 필드는 "user_name" 컬럼 이름으로 매핑됩니다.
     *
     * @param name Hibernate 엔티티의 필드 이름을 나타내는 Identifier 객체입니다.
     * @param context 현재 데이터베이스 환경에 대한 정보를 제공하는 JdbcEnvironment 객체입니다.
     * @return 변환된 데이터베이스 컬럼 이름을 나타내는 Identifier 객체를 반환합니다.
     */
    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        // `addUnderscores` 메서드를 호출하여 엔티티 필드 이름에서 CamelCase를 snake_case로 변환합니다.
        // 변환된 이름과 원래 이름의 인용 상태를 유지하여 새 Identifier 객체를 생성합니다.
        return new Identifier(addUnderscores(name.getText()), name.isQuoted());
    }


    /**
     * 주어진 문자열에서 각 단어 사이에 밑줄을 추가하고, 모든 문자를 소문자로 변환합니다.
     * 단어는 소문자와 대문자 사이 경계에서 분리되며, 점(.)은 밑줄(_)로 대체됩니다.
     *
     * @param name 변환할 원본 문자열입니다.
     * @return 변환된 문자열을 반환합니다.
     */
    protected static String addUnderscores(String name) {
        final StringBuilder buf = new StringBuilder(name.replace('.', '_'));
        int i = 1;

        while (i < buf.length() - 1) {
            if (Character.isLowerCase(buf.charAt(i - 1)) &&
                    Character.isUpperCase(buf.charAt(i)) &&
                    Character.isLowerCase(buf.charAt(i + 1))) {
                buf.insert(i, '_');
                i += 2;
            } else {
                i++;
            }
        }
        return buf.toString().toLowerCase();
    }


}
