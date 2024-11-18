package DBS;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Connection conn = getDBConnection(); // DB 접속 메서드 호출

        // 데이터베이스 연결 성공 여부 확인
        if (conn == null) {
            System.out.println("데이터베이스 연결 실패");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        // 메뉴 반복
        while (true) {
            System.out.println("=== Book DB 메뉴 ===");
            System.out.println("1. 책 삽입");
            System.out.println("2. 책 삭제");
            System.out.println("3. 책 조회");
            System.out.println("4. 종료");
            System.out.print("선택: ");

            int command = scanner.nextInt();
            scanner.nextLine(); // 버퍼 비우기

            switch (command) {
                case 1:
                    insertBook(conn, scanner);
                    break;
                case 2:
                    deleteBook(conn, scanner);
                    break;
                case 3:
                    selectBooks(conn);
                    break;
                case 4:
                    System.out.println("프로그램을 종료합니다.");
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return;
                default:
                    System.out.println("잘못된 선택입니다. 다시 선택해주세요.");
            }
        }
    }

    // DB 접속을 담당하는 메서드
    private static Connection getDBConnection() {
        Connection conn = null;
        Properties properties = new Properties();

        // application.properties 파일에서 정보를 읽어오기
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream == null) {
                System.out.println("설정 파일을 찾을 수 없습니다.");
                return null;  // 파일이 없으면 null 반환
            }
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("설정 파일 로드 실패");
            e.printStackTrace();
            return null;
        }

        // MySQL 접속 정보 가져오기
        final String URL = properties.getProperty("db.url");
        final String USER = properties.getProperty("db.username");
        final String PASSWORD = properties.getProperty("db.password");

        try {
            // JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 데이터베이스 연결
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 실패");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("데이터베이스 연결 실패");
            e.printStackTrace();
        }

        return conn;
    }

    // 책 삽입 메서드
    private static void insertBook(Connection conn, Scanner scanner) {
        System.out.print("책 ID를 입력하세요: ");
        int bookId = scanner.nextInt();
        scanner.nextLine(); // 버퍼 비우기
        System.out.print("책 제목을 입력하세요: ");
        String bookName = scanner.nextLine();
        System.out.print("출판사를 입력하세요: ");
        String publisher = scanner.nextLine();
        System.out.print("책 가격을 입력하세요: ");
        int price = scanner.nextInt();

        String sql = "INSERT INTO Book (bookid, bookname, publisher, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.setString(2, bookName);
            stmt.setString(3, publisher);
            stmt.setInt(4, price);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("책 삽입 성공");
            }
        } catch (SQLException e) {
            System.out.println("책 삽입 실패");
            e.printStackTrace();
        }

        System.out.println();
    }

    // 책 삭제 메서드
    private static void deleteBook(Connection conn, Scanner scanner) {
        System.out.print("삭제할 책 ID를 입력하세요: ");
        int bookId = scanner.nextInt();
        scanner.nextLine(); // 버퍼 비우기

        String sql = "DELETE FROM Book WHERE bookid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("책 삭제 성공");
            } else {
                System.out.println("해당 책 ID의 데이터가 없습니다.");
            }
        } catch (SQLException e) {
            System.out.println("책 삭제 실패");
            e.printStackTrace();
        }

        System.out.println();
    }

    // 책 조회 메서드
    private static void selectBooks(Connection conn) {
        String sql = "SELECT bookid, bookname, publisher, price FROM Book";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int bookId = rs.getInt("bookid");
                String bookName = rs.getString("bookname");
                String publisher = rs.getString("publisher");
                int price = rs.getInt("price");

                System.out.println(bookId + " | " + bookName + " | " + publisher + " | " + price);
            }
        } catch (SQLException e) {
            System.out.println("책 조회 실패");
            e.printStackTrace();
        }

        System.out.println();
    }
}