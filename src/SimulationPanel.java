import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class SimulationPanel extends JPanel {
    private Image bgImage; // Ảnh nền ngã tư m tải lên
    private GraphicRenderer graphicRenderer;
    private boolean ambulanceFlash = false;
    
    // Danh sách xe giả lập để m tự chạy UI riêng trước
    private List<Vehicle> mockVehicles = new ArrayList<>();

    public SimulationPanel() {
        graphicRenderer = new GraphicRenderer();

        // Nạp ảnh nền ngã tư
        try {
            bgImage = ImageIO.read(new File("resources/background_intersection.png"));
        } catch (Exception e) {
            System.out.println("[LỖI] Không tìm thấy ảnh nền background_intersection.png");
        }

        // TẠO DỮ LIỆU XE GIẢ LẬP ĐỂ TEST ĐỘC LẬP
        // Cấu trúc: id, loại xe, tọa độ x, tọa độ y, góc xoay (0: sang phải, 90: đi xuống, 180: sang trái, 270: đi lên)
        mockVehicles.add(new Vehicle("1", "Car", 50, 260, 0));            // Xe con đi từ trái sang phải
        mockVehicles.add(new Vehicle("2", "Motorbike", 430, 50, 90));     // Xe máy đi từ trên xuống dưới
        mockVehicles.add(new Vehicle("3", "Ambulance", 750, 330, 180));   // Xe cứu thương đi từ phải sang trái

        // VÒNG LẶP ĐỊNH THỜI (Mỗi 16ms vẽ lại một lần ~ 60 FPS)
        Timer timer = new Timer(16, e -> {
            updateMockPhysics(); // Tự update xe chạy thử
            ambulanceFlash = !ambulanceFlash; // Đổi trạng thái đèn chớp nháy
            repaint(); // Vẽ lại màn hình
        });
        timer.start();
    }

    // Hàm tự mô phỏng xe chạy để m test UI, sau này cno làm xong logic thì xóa hàm này
    private void updateMockPhysics() {
        for (Vehicle v : mockVehicles) {
            if (v.angle == 0) {
                v.x += 2; // Đi sang phải
                if (v.x > 800) v.x = 0;
            } else if (v.angle == 90) {
                v.y += 2; // Đi xuống
                if (v.y > 600) v.y = 0;
            } else if (v.angle == 180) {
                v.x -= 2; // Đi sang trái
                if (v.x < 0) v.x = 800;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Bật chế độ làm mượt ảnh (Chống răng cưa khi xoay xe)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 1. VẼ ẢNH NỀN NGÃ TƯ
        if (bgImage != null) {
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            // Nếu chưa có ảnh nền, tự vẽ ngã tư màu xám tạm thời để test
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(Color.GRAY);
            g2d.fillRect(0, 220, 800, 150); // Đường ngang
            g2d.fillRect(350, 0, 150, 600); // Đường dọc
        }

        // 2. VẼ TỪNG CÁI XE LÊN TRÊN NỀN NGÃ TƯ
        for (Vehicle vehicle : mockVehicles) {
            graphicRenderer.render(g2d, vehicle, ambulanceFlash);
        }
    }
}