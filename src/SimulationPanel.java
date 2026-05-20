import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class SimulationPanel extends JPanel {
    // Mode vẽ: true = Graphic (Sprite), false = Basic (Hình hộp)
    private boolean isGraphicMode = false; 
    
    // Biến phục vụ hiệu ứng đèn chớp nháy của xe cứu thương
    private boolean ambulanceFlash = false;

    // Danh sách xe giả lập để m tự chạy UI test một mình
    private List<Vehicle> mockVehicles = new ArrayList<>();

    public SimulationPanel() {
        // 1. Tạo dữ liệu giả để test UI độc lập
        mockVehicles.add(new Vehicle("1", "Car", 100, 150, 0));          // Xe đi ngang
        mockVehicles.add(new Vehicle("2", "Motorbike", 300, 200, 45));   // Xe đi chéo 45 độ
        mockVehicles.add(new Vehicle("3", "Ambulance", 200, 300, 90));   // Xe đi xuống 90 độ

        // 2. Định thời (Timer): Cứ mỗi 16ms (60 FPS) cập nhật UI một lần
        Timer timer = new Timer(16, e -> {
            updateMockPositions(); // Tự cho xe chạy giả lập để test
            ambulanceFlash = !ambulanceFlash; // Đảo trạng thái đèn cứu thương liên tục
            repaint(); // Yêu cầu vẽ lại màn hình
        });
        timer.start();
    }

    // Hàm tự cộng tọa độ cho xe chạy thử, sau này ghép bài với nhóm thì XÓA HÀM NÀY ĐI
    private void updateMockPositions() {
        for (Vehicle v : mockVehicles) {
            v.x += 1; // Cho xe chạy về phía trước thử nghiệm
            if (v.x > 800) v.x = 0; // Chạy hết màn hình thì quay lại
        }
    }

    // Hàm đổi Mode vẽ khi người dùng bấm nút trên UI
    public void setViewMode(boolean graphicMode) {
        this.isGraphicMode = graphicMode;
    }

    // NƠI VẼ CHÍNH
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ bản đồ nền (đường sá m có thể tự vẽ vài nét cơ bản)
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 100, 800, 100); // Ví dụ một con đường ngang

        // Duyệt qua từng cái xe để vẽ
        for (Vehicle vehicle : mockVehicles) {
            // Lưu lại cấu hình hệ trục tọa độ gốc
            AffineTransform oldTransform = g2d.getTransform();

            // Dịch chuyển tâm trục tọa độ đến vị trí của xe và xoay theo hướng xe
            g2d.translate(vehicle.x, vehicle.y);
            g2d.rotate(Math.toRadians(vehicle.angle));

            // BẮT ĐẦU VẼ XE (Lúc này mặc định tâm xe là gốc 0,0)
            if (!isGraphicMode) {
                // --- MODE 1: BASIC (Vẽ hình chữ nhật) ---
                renderBasic(g2d, vehicle);
            } else {
                // --- MODE 2: GRAPHIC (Vẽ ảnh Sprite) ---
                renderGraphic(g2d, vehicle);
            }

            // Vẽ hiệu ứng đèn nhấp nháy nếu là xe cứu thương
            if (vehicle.type.equals("Ambulance") && ambulanceFlash) {
                g2d.setColor(Color.RED);
                g2d.fillOval(-5, -15, 10, 10); // Vẽ đốm đèn đỏ nhấp nháy trên đầu xe
            }

            // Khôi phục lại hệ trục tọa độ cũ để vẽ cái xe tiếp theo
            g2d.setTransform(oldTransform);
        }
    }

    // Hàm vẽ hình hộp (Basic Mode)
    private void renderBasic(Graphics2D g2d, Vehicle v) {
        if (v.type.equals("Car")) {
            g2d.setColor(Color.BLUE);
            g2d.fillRect(-20, -10, 40, 20); // Kích thước xe ô tô
        } else if (v.type.equals("Motorbike")) {
            g2d.setColor(Color.RED);
            g2d.fillRect(-10, -5, 20, 10);  // Kích thước xe máy
        } else if (v.type.equals("Ambulance")) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(-25, -12, 50, 24); // Xe cứu thương to hơn
        }
    }

    // Hàm vẽ ảnh Sprite (Graphic Mode)
    private void renderGraphic(Graphics2D g2d, Vehicle v) {
        // Mẹo: Dùng tạm chữ hoặc hình vẽ đẹp hơn để test nếu chưa có file ảnh Sprite .png
        // Khi có ảnh .png, m dùng: Image img = new ImageIcon("path/to/car.png").getImage();
        // g2d.drawImage(img, -20, -10, null);
        g2d.setColor(Color.ORANGE);
        g2d.drawString("🚙 " + v.type, -20, 5); // Dùng tạm Emoji để test tính năng xoay ảnh cực tiện
    }
}