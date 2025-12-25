package ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * RoundedPanel - Panel tùy chỉnh hỗ trợ bo góc và hiệu ứng đổ bóng.
 * Sử dụng thay thế cho JPanel thông thường để giao diện mềm mại hơn.
 */
public class RoundedPanel extends JPanel {
    private int cornerRadius = 15;
    private Color backgroundColor;
    private boolean hasShadow = true;
    private int shadowSize = 3;
    private int shadowOpacity = 50;
    private Color shadowColor = Color.BLACK;

    public RoundedPanel() {
        this(15, Color.WHITE);
    }

    public RoundedPanel(int radius) {
        this(radius, Color.WHITE);
    }

    public RoundedPanel(int radius, Color bgColor) {
        super();
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        setOpaque(false); // Bắt buộc để vẽ background thủ công
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Bật khử răng cưa để đường cong mượt mà
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int shadowGap = hasShadow ? shadowSize : 0;

        // Vẽ bóng (Shadow)
        if (hasShadow) {
            g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), shadowOpacity));
            // Vẽ bóng lệch một chút xuống dưới và sang phải
            g2.fillRoundRect(shadowGap, shadowGap, width - shadowGap * 2, height - shadowGap * 2, cornerRadius, cornerRadius);
        }

        // Vẽ nền chính (Background)
        g2.setColor(backgroundColor);
        // Nếu có bóng thì vẽ nhỏ lại một chút để lộ bóng ra
        g2.fillRoundRect(0, 0, width - (hasShadow ? shadowGap : 0), height - (hasShadow ? shadowGap : 0), cornerRadius, cornerRadius);
        
        // Vẽ viền (Border) nhẹ nếu cần
        g2.setColor(new Color(200, 200, 200, 50));
        g2.drawRoundRect(0, 0, width - (hasShadow ? shadowGap : 0) - 1, height - (hasShadow ? shadowGap : 0) - 1, cornerRadius, cornerRadius);
    }

    // --- Getters & Setters (Giúp tăng dòng code và tùy biến dễ dàng) ---

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }
    
    public void setHasShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
        repaint();
    }
}