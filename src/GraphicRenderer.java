import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class GraphicRenderer {
    // Lưu trữ các bức ảnh vào Bộ nhớ (Cache) để tránh việc đọc file liên tục gây lag
    private Map<String, Image> vehicleSprites = new HashMap<>();

    public GraphicRenderer() {
        loadSprites();
    }

    // Hàm nạp ảnh từ thư mục resources
    private void loadSprites() {
        try {
            vehicleSprites.put("Car", ImageIO.read(new File("src/resources/car.png")));
            vehicleSprites.put("Motorbike", ImageIO.read(new File("src/resources/motorbike.png")));
            vehicleSprites.put("Ambulance", ImageIO.read(new File("src/resources/ambulance.png")));
            System.out.println("=== Đã tải toàn bộ ảnh xe thành công! ===");
        } catch (Exception e) {
            System.out.println("[LỖI] Không tìm thấy file ảnh trong thư mục resources/. Đang dùng hình vẽ tạm thời.");
        }
    }

    // Hàm vẽ xe chính xác theo vị trí và góc xoay
    public void render(Graphics2D g2d, Vehicle v, boolean ambulanceFlash) {
        Image img = vehicleSprites.get(v.type);

        // Lưu lại trạng thái hệ tọa độ gốc
        AffineTransform oldTransform = g2d.getTransform();

        // 1. Dịch chuyển tâm vẽ đến tọa độ (x, y) của xe
        g2d.translate(v.x, v.y);
        
        // 2. Xoay hệ trục tọa độ theo góc của xe (đổi từ Độ sang Radian)
        g2d.rotate(Math.toRadians(v.angle));

        if (img != null) {
            // Lấy kích thước thật của ảnh để căn tâm
            int width = img.getWidth(null);
            int height = img.getHeight(null);

            // 3. Vẽ ảnh sao cho TÂM của ảnh nằm đúng tọa độ (0,0) mới
            g2d.drawImage(img, -width / 2, -height / 2, null);

            // Hiệu ứng đèn nhấp nháy cho xe cứu thương (nếu là Ambulance)
            if (v.type.equals("Ambulance") && ambulanceFlash) {
                g2d.setColor(new Color(255, 0, 0, 200)); // Màu đỏ trong suốt
                g2d.fillOval(-5, -height/2 - 5, 10, 10); // Vẽ đốm đèn nhấp nháy trên đầu xe
            }
        } else {
            // Nếu m chưa bỏ ảnh vào thư mục, code sẽ tự vẽ hình hộp tạm thời để không bị lỗi crash
            g2d.setColor(Color.RED);
            g2d.fillRect(-20, -10, 40, 20);
            g2d.setColor(Color.WHITE);
            g2d.drawString(v.type, -15, 5);
        }

        // Khôi phục lại trạng thái hệ tọa độ cũ để không ảnh hưởng đến xe khác
        g2d.setTransform(oldTransform);
    }
}