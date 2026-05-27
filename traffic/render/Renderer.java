package traffic.render;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Renderer {

    // Bộ nhớ RAM tạm thời (Cache) để lưu trữ các ảnh xe cộ sau khi load
    private static final Map<String, BufferedImage> spriteCache = new HashMap<>();

    /**
     * HÀM TẢI TRƯỚC TÀI NGUYÊN - Gọi ở mục 1 trong file Main.java
     */
    public static void loadSprites() {
        //System.out.println("[Renderer] Đang tải tài nguyên ảnh vào RAM...");
        
        // Thống nhất đường dẫn đến thư mục chứa ảnh xe của nhóm ông
        String basePath = "traffic/resources/images/";

        // Cấu hình danh sách các file ảnh cần nạp
        String[] vehicleTypes = {"CAR", "BIKE", "MOTORBIKE", "AMBULANCE", "FIRE_TRUCK"};
        String[] fileNames = {"car.png", "bike.png", "motorbike.png", "ambulance.png", "firetruck.png"};

        for (int i = 0; i < vehicleTypes.length; i++) {
            try {
                File file = new File(basePath + fileNames[i]);
                if (file.exists()) {
                    BufferedImage img = ImageIO.read(file);
                    spriteCache.put(vehicleTypes[i], img);
                    System.out.println("[Renderer] Load successful: " + vehicleTypes[i]);
                } else {
                    System.out.println("[Renderer - Warning] Cannot found at: " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                System.out.println("[Renderer - Error] Cannot read image" + vehicleTypes[i] + ": " + e.getMessage());
            }
        }
    }

    /**
     * Hàm lấy ảnh từ RAM ra để vẽ (Dùng bên SimulationPanel)
     * @param type Loại xe cần lấy ảnh ("CAR", "BIKE",...)
     * @return BufferedImage Đối tượng ảnh chuẩn của Java
     */
    public static BufferedImage getSprite(String type) {
        if (type == null) return null;
        // Trả về ảnh tương ứng từ bộ nhớ cache, nếu không có trả về null
        return spriteCache.get(type.toUpperCase());
    }
}