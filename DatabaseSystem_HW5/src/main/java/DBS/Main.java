package DBS;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Connection conn = null;

        // Properties 객체를 사용하여 application.properties 파일에서 정보를 읽어오기
        Properties properties = new Properties();

        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream == null) {
                System.out.println("설정 파일을 찾을 수 없습니다.");
                return;  // 파일이 없으면 종료
            }
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("설정 파일 로드 실패");
            e.printStackTrace();
            return;
        }

        // MySQL 접속 정보 가져오기
        final String URL = properties.getProperty("db.url");
        final String USER = properties.getProperty("db.username");
        final String PASSWORD = properties.getProperty("db.password");

        try {
            // 1. JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. 데이터베이스 연결
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("데이터베이스 연결 성공!");

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 실패");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("데이터베이스 연결 실패");
            e.printStackTrace();
        } finally {
            // 3. 리소스 해제
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        System.out.println("데이터베이스 연결 종료.");
    }
}