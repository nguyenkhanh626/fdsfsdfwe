import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        // 1. Kiểm tra nạp Class Driver
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("✅ Đã tìm thấy thư viện Driver SQLite!");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Lỗi: Không tìm thấy thư viện SQLite-JDBC. Vui lòng kiểm tra lại cấu hình.");
            e.printStackTrace();
            return; // Dừng chương trình nếu không thấy thư viện
        }

        // 2. Kiểm tra kết nối thử tới database (tạo file tạm trong bộ nhớ)
        String url = "jdbc:sqlite::memory:"; // Hoặc dùng "jdbc:sqlite:test.db" để tạo file
        
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("✅ Kết nối thành công tới SQLite!");
                System.out.println("Phiên bản Driver: " + conn.getMetaData().getDriverVersion());
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi kết nối SQL: " + e.getMessage());
        }
    }
}