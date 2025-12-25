package tabs;

import javax.swing.*;
import MainApp.QuanLyNhanVienGUI;
import objects.NhanVien;
import objects.PhongBan;
import ui.components.RoundedPanel;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class TabDashboard extends JPanel {

    private QuanLyNhanVienGUI parent;
    private JPanel cardsContainer;
    private RoundedPanel chartPanel1;
    private RoundedPanel chartPanel2;

    // Màu sắc chủ đạo chuẩn Enterprise
    private final Color COL_BLUE = new Color(59, 130, 246);
    private final Color COL_GREEN = new Color(16, 185, 129);
    private final Color COL_ORANGE = new Color(245, 158, 11);
    private final Color COL_RED = new Color(239, 68, 68);

    public TabDashboard(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(241, 245, 249)); // Màu nền xám nhạt đồng bộ
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Header
        JLabel lblTitle = new JLabel("Tổng Quan Hoạt Động");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(30, 41, 59));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Body (Scrollable)
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        
        // --- Row 1: Thẻ số liệu (Metric Cards) ---
        cardsContainer = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsContainer.setOpaque(false);
        cardsContainer.setMaximumSize(new Dimension(2000, 140)); // Chiều cao cố định
        body.add(cardsContainer);
        
        body.add(Box.createRigidArea(new Dimension(0, 20))); // Khoảng cách

        // --- Row 2: Biểu đồ (Charts) ---
        JPanel chartsContainer = new JPanel(new GridLayout(1, 2, 20, 20));
        chartsContainer.setOpaque(false);
        
        // Container cho Biểu đồ tròn
        chartPanel1 = new RoundedPanel(20, Color.WHITE);
        chartPanel1.setLayout(new BorderLayout());
        
        // Container cho Biểu đồ cột
        chartPanel2 = new RoundedPanel(20, Color.WHITE);
        chartPanel2.setLayout(new BorderLayout());
        
        chartsContainer.add(chartPanel1);
        chartsContainer.add(chartPanel2);
        
        body.add(chartsContainer);
        add(body, BorderLayout.CENTER);

        // Load dữ liệu lần đầu
        refreshDashboard();
    }
    
    /**
     * Hàm làm mới dữ liệu toàn bộ Dashboard.
     * Được gọi khi khởi tạo hoặc khi các Tab khác thay đổi dữ liệu.
     */
    public void refreshDashboard() {
        // 1. Cập nhật các Thẻ số liệu (Cards)
        cardsContainer.removeAll();
        
        int totalNV = (parent.danhSachNV != null) ? parent.danhSachNV.size() : 0;
        int totalPB = (parent.danhSachPB != null) ? parent.danhSachPB.size() : 0;
        int totalDA = (parent.danhSachDuAn != null) ? parent.danhSachDuAn.size() : 0;
        
        // Tính tổng lương (Ví dụ đơn giản)
        // Trong thực tế bạn có thể lấy từ bảng lương
        long totalLuong = 0; 
        // Giả lập tính lương cơ bản
        if (parent.danhSachNV != null) {
           // Ở đây bạn có thể sum lương nếu object NV có field lương
           // Tạm thời để số nhân viên vi phạm làm ví dụ cho thẻ đỏ
           totalLuong = parent.danhSachNV.stream().filter(nv -> nv.getDiemViPham() > 0).count();
        }

        cardsContainer.add(new MetricCard("Tổng Nhân Sự", String.valueOf(totalNV), "Người", COL_BLUE));
        cardsContainer.add(new MetricCard("Phòng Ban", String.valueOf(totalPB), "Phòng", COL_GREEN));
        cardsContainer.add(new MetricCard("Dự Án", String.valueOf(totalDA), "Dự án", COL_ORANGE));
        cardsContainer.add(new MetricCard("Nhân sự Vi phạm", String.valueOf(totalLuong), "Cảnh báo", COL_RED));
        
        // 2. Cập nhật Biểu đồ
        chartPanel1.removeAll();
        chartPanel2.removeAll();
        
        // --- XỬ LÝ DỮ LIỆU BIỂU ĐỒ TRÒN (QUAN TRỌNG: FIX LỖI KT/KỸ THUẬT) ---
        Map<String, Long> nvTheoPB = new HashMap<>();
        
        if (parent.danhSachNV != null) {
            for (NhanVien nv : parent.danhSachNV) {
                // Lấy mã phòng ban từ nhân viên (VD: "KT")
                String maPB = nv.getPhongBan();
                
                // Tra cứu tên đầy đủ (VD: "Kỹ thuật")
                String tenPBDayDu = getTenPhongBan(maPB);
                
                // Gom nhóm theo TÊN ĐẦY ĐỦ
                nvTheoPB.put(tenPBDayDu, nvTheoPB.getOrDefault(tenPBDayDu, 0L) + 1);
            }
        }

        // --- XỬ LÝ DỮ LIỆU BIỂU ĐỒ CỘT (THÂM NIÊN) ---
        Map<Integer, Long> nvTheoThamNien = new HashMap<>();
        if (parent.danhSachNV != null) {
            for (NhanVien nv : parent.danhSachNV) {
                int thamNien = nv.getThamNien();
                nvTheoThamNien.put(thamNien, nvTheoThamNien.getOrDefault(thamNien, 0L) + 1);
            }
        }

        // Add Chart Panels vào UI
        chartPanel1.add(new PieChartPanel("Cơ cấu Nhân sự theo Phòng ban", nvTheoPB));
        chartPanel2.add(new BarChartPanel("Thống kê Thâm niên làm việc", nvTheoThamNien));
        
        cardsContainer.revalidate();
        cardsContainer.repaint();
        chartPanel1.revalidate();
        chartPanel1.repaint();
        chartPanel2.revalidate();
        chartPanel2.repaint();
    }

    /**
     * Helper: Tra cứu tên phòng ban từ Mã
     * Giúp đồng bộ hiển thị: Mã "KT" sẽ luôn hiện là "Kỹ thuật"
     */
    private String getTenPhongBan(String maPB) {
        if (maPB == null) return "Chưa phân loại";
        if (parent.danhSachPB != null) {
            for (PhongBan pb : parent.danhSachPB) {
                if (pb.getMaPhongBan().equalsIgnoreCase(maPB.trim())) {
                    return pb.getTenPhongBan();
                }
            }
        }
        return maPB; // Fallback nếu không tìm thấy
    }

    // =========================================================================
    // INNER CLASSES CHO UI (WIDGETS & CHARTS)
    // =========================================================================

    /**
     * Widget thẻ số liệu đẹp mắt
     */
    private class MetricCard extends RoundedPanel {
        public MetricCard(String title, String value, String unit, Color color) {
            super(15, Color.WHITE);
            setLayout(null);
            
            // Dải màu trang trí bên trái
            JPanel stripe = new JPanel();
            stripe.setBackground(color);
            stripe.setBounds(0, 0, 6, 140);
            add(stripe);
            
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lblTitle.setForeground(Color.GRAY);
            lblTitle.setBounds(20, 20, 150, 20);
            add(lblTitle);
            
            JLabel lblValue = new JLabel(value);
            lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
            lblValue.setForeground(color);
            lblValue.setBounds(20, 50, 150, 45);
            add(lblValue);

            JLabel lblUnit = new JLabel(unit);
            lblUnit.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblUnit.setForeground(Color.LIGHT_GRAY);
            lblUnit.setBounds(25, 95, 100, 20);
            add(lblUnit);
        }
    }

    /**
     * Panel vẽ biểu đồ tròn (Pie Chart) thủ công
     */
    private class PieChartPanel extends JPanel {
        private String title;
        private Map<String, Long> data;
        // Bảng màu dịu mắt
        private Color[] colors = {
            new Color(59, 130, 246), // Blue
            new Color(16, 185, 129), // Green
            new Color(245, 158, 11), // Orange
            new Color(239, 68, 68),  // Red
            new Color(139, 92, 246), // Purple
            new Color(236, 72, 153)  // Pink
        };

        public PieChartPanel(String title, Map<String, Long> data) {
            this.title = title;
            this.data = data;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Vẽ tiêu đề
            g2.setColor(new Color(51, 65, 85));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.drawString(title, 20, 30);

            if (data == null || data.isEmpty()) {
                g2.drawString("Chưa có dữ liệu", 100, 100);
                return;
            }

            long total = data.values().stream().mapToLong(Long::longValue).sum();
            if (total == 0) return;

            int startAngle = 90;
            int i = 0;
            
            // Tọa độ vẽ biểu đồ
            int chartX = 30;
            int chartY = 60;
            int chartSize = 200;
            
            // Tọa độ vẽ chú thích (Legend)
            int legendX = 260;
            int legendY = 70;

            for (Map.Entry<String, Long> entry : data.entrySet()) {
                // Tính góc
                int angle = (int) Math.round((entry.getValue() * 360.0) / total);
                
                // Fix lỗi làm tròn khiến vòng tròn bị hở: nếu là phần tử cuối, lấy phần còn lại
                if (i == data.size() - 1) {
                    angle = 450 - startAngle; 
                }

                g2.setColor(colors[i % colors.length]);
                g2.fillArc(chartX, chartY, chartSize, chartSize, startAngle, angle);
                
                // Vẽ chú thích
                g2.fillRect(legendX, legendY, 15, 15);
                g2.setColor(new Color(51, 65, 85));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                
                double percent = (entry.getValue() * 100.0) / total;
                String label = String.format("%s: %d (%.1f%%)", entry.getKey(), entry.getValue(), percent);
                g2.drawString(label, legendX + 25, legendY + 12);
                
                startAngle += angle;
                legendY += 30; // Xuống dòng cho chú thích tiếp theo
                i++;
            }
            
            // Vẽ lỗ tròn ở giữa để tạo hiệu ứng Donut Chart (Nhìn hiện đại hơn)
            g2.setColor(Color.WHITE);
            g2.fillOval(chartX + 50, chartY + 50, 100, 100);
        }
    }

    /**
     * Panel vẽ biểu đồ cột (Bar Chart) thủ công
     */
    private class BarChartPanel extends JPanel {
        private String title;
        private Map<Integer, Long> data;

        public BarChartPanel(String title, Map<Integer, Long> data) {
            this.title = title;
            this.data = data;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(51, 65, 85));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.drawString(title, 20, 30);

            if (data == null || data.isEmpty()) return;
            
            long maxVal = data.values().stream().mapToLong(Long::longValue).max().orElse(1);
            
            int startX = 50;
            int bottomY = 250;
            int barWidth = 40;
            int gap = 30;
            int maxHeight = 180; // Chiều cao tối đa của cột

            // Vẽ trục tọa độ
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(startX, 50, startX, bottomY); // Trục Y
            g2.drawLine(startX, bottomY, 500, bottomY); // Trục X

            int x = startX + 20;
            
            // Sắp xếp theo thâm niên tăng dần để biểu đồ đẹp
            var sortedKeys = data.keySet().stream().sorted().toList();

            for (Integer key : sortedKeys) {
                Long val = data.get(key);
                int barHeight = (int) ((val * maxHeight) / maxVal);
                
                // Vẽ cột
                g2.setColor(new Color(99, 102, 241)); // Màu Indigo
                g2.fillRoundRect(x, bottomY - barHeight, barWidth, barHeight, 5, 5);
                
                // Vẽ số lượng trên đỉnh cột
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String valStr = val.toString();
                int strWidth = g2.getFontMetrics().stringWidth(valStr);
                g2.drawString(valStr, x + (barWidth - strWidth) / 2, bottomY - barHeight - 5);
                
                // Vẽ nhãn trục X (Số năm)
                g2.setColor(Color.GRAY);
                g2.drawString(key + " năm", x, bottomY + 20);
                
                x += barWidth + gap;
            }
        }
    }
}