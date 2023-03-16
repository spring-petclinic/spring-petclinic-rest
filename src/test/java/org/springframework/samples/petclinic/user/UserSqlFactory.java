package org.springframework.samples.petclinic.user;

public class UserSqlFactory {

    private UserSqlFactory() {
    }

    public static String insertAdmin() {
        return """
            INSERT INTO users(username,password,enabled)
            VALUES ('admin','{noop}admin', true)
            """;
    }

    public static String insertRoleVetAdmin() {
        return """
            INSERT INTO roles (username, role)
            VALUES ('admin', 'ROLE_VET_ADMIN')
            """;
    }


}
