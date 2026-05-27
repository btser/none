package traffic.components;

import traffic.components.MockVehicle;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SimulationPanel extends JPanel {

    private BufferedImage backgroundImage;
    private Object simulationEngine;
    private String currentMockColor = "RED"; 

    // Biến quản lý hệ số Zoom động (1.0 là bình thường, tăng/giảm khi bấm nút)
    private double zoomScale = 1.0;

    // Tọa độ và kích thước thực tế của Map khi vẽ
    private int mapDrawX = 0;
    private int mapDrawY = 0;
    private int mapDrawW = 800;
    private int mapDrawH = 550;

    public SimulationPanel(Object engine) {
        this.simulationEngine = engine;
        this.setBackground(new Color(45, 45, 45)); 

        try {
            String mapPath = "traffic/resources/images/map.png";
            File file = new File(mapPath);
            if (file.exists()) {
                backgroundImage = ImageIO.read(file);
                System.out.println("[UI] Load map image success!");
            } else {
                System.out.println("[UI - Error] Cannot find map file at: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("[UI - Error] Load map exception: " + e.getMessage());
        }
    }

    // --- HÀM XỬ LÝ ĐỘNG KHI USER BẤM ZOOM ---
    public void zoomIn() {
        this.zoomScale += 0.1; // Mỗi lần bấm tăng 10% kích thước
        if (this.zoomScale > 3.0) this.zoomScale = 3.0; // Giới hạn tối đa zoom 3x
        this.repaint();
    }

    public void zoomOut() {
        this.zoomScale -= 0.1; // Mỗi lần bấm giảm 10% kích thước
        if (this.zoomScale < 0.5) this.zoomScale = 0.5; // Giới hạn tối thiểu 0.5x
        this.repaint();
    }

    public void setMockLightColor(String color) {
        this.currentMockColor = color;
        this.repaint(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();

        // Đẩy map sang phải 70 pixel để tạo khoảng cách rộng rãi, chống đè với Sidebar điều khiển
        int paddingLeft = 70; 

        if (backgroundImage != null) {
            double imgRatio = (double) backgroundImage.getWidth() / backgroundImage.getHeight();
            double panelRatio = (double) (panelWidth - paddingLeft) / panelHeight;

            // Tính toán kích thước theo khung chuẩn của cửa sổ hiện tại
            if (imgRatio > panelRatio) {
                mapDrawW = panelWidth - paddingLeft;
                mapDrawH = (int) ((panelWidth - paddingLeft) / imgRatio);
            } else {
                mapDrawH = panelHeight;
                mapDrawW = (int) (panelHeight * imgRatio);
            }

            // ÁP DỤNG BIẾN ZOOM ĐỘNG VÀO KÍCH THƯỚC MAP
            mapDrawW = (int) (mapDrawW * zoomScale);
            mapDrawH = (int) (mapDrawH * zoomScale);
            
            // Đặt tọa độ X xuất phát từ lề đệm paddingLeft để map dịch sang phải thông thoáng
            mapDrawX = paddingLeft;
            mapDrawY = 20; // Dịch xuống một tí cho đẹp cân đối trên dưới

            // Vẽ ảnh map theo tọa độ dịch phải và kích thước đã nhân hệ số Zoom
            g2d.drawImage(backgroundImage, mapDrawX, mapDrawY, mapDrawW, mapDrawH, null);
        }

        // Vẽ đèn và xe
        drawAllTrafficLights(g2d);
        drawVehicles(g2d);
    }

    private void drawSingleLight(Graphics2D g2d, int x, int y, int d, String state, boolean isHorizontal) {
        int gap = Math.max(1, d / 4); 
        Color off = new Color(60, 60, 60); 

        if (isHorizontal) {
            g2d.setColor(new Color(20, 20, 20));
            g2d.fillRoundRect(x - gap, y - gap, (d + gap) * 3 + gap, d + (gap * 2), gap, gap);
            g2d.setColor(state.equalsIgnoreCase("RED") ? Color.RED : off); g2d.fillOval(x, y, d, d);
            g2d.setColor(state.equalsIgnoreCase("YELLOW") ? Color.YELLOW : off); g2d.fillOval(x + d + gap, y, d, d);
            g2d.setColor(state.equalsIgnoreCase("GREEN") ? Color.GREEN : off); g2d.fillOval(x + (d + gap) * 2, y, d, d);
        } else {
            g2d.setColor(new Color(20, 20, 20));
            g2d.fillRoundRect(x - gap, y - gap, d + (gap * 2), (d + gap) * 3 + gap, gap, gap);
            g2d.setColor(state.equalsIgnoreCase("RED") ? Color.RED : off); g2d.fillOval(x, y, d, d);
            g2d.setColor(state.equalsIgnoreCase("YELLOW") ? Color.YELLOW : off); g2d.fillOval(x, y + d + gap, d, d);
            g2d.setColor(state.equalsIgnoreCase("GREEN") ? Color.GREEN : off); g2d.fillOval(x, y + (d + gap) * 2, d, d);
        }
    }

    private void drawAllTrafficLights(Graphics2D g2d) {
        String stateEW = currentMockColor; 
        String stateNS = stateEW.equals("RED") ? "GREEN" : (stateEW.equals("GREEN") ? "YELLOW" : "RED");

        double scaleX = (double) mapDrawW / 800.0;
        double scaleY = (double) mapDrawH / 550.0;

        int dynamicD = (int) (12 * (mapDrawW / 800.0));
        if (dynamicD < 6) dynamicD = 6; 

        int[][] lightsConfig = {
            // --- INTERSECTION 1: Top-Left ---
            {150, 44,  1, 1}, {210, 213, 1, 1}, {325, 102,  2, 0}, {67,  140, 2, 0}, 
            // --- INTERSECTION 2: Top-Right ---
            {555, 44, 1, 1}, {615, 213, 1, 1}, {723, 102, 2, 0}, {465, 140, 2, 0}, 
            // --- INTERSECTION 3: Bottom-Left ---
            {150, 332, 1, 1}, {210, 495, 1, 1}, {312, 380, 2, 0}, {80, 418, 2, 0}, 
            // --- INTERSECTION 4: Bottom-Right ---
            {555, 332, 1, 1}, {615, 495, 1, 1}, {712, 380, 2, 0}, {480, 418, 2, 0}  
        };

        for (int[] config : lightsConfig) {
            int finalX = mapDrawX + (int) (config[0] * scaleX);
            int finalY = mapDrawY + (int) (config[1] * scaleY);
            
            String finalState = (config[2] == 1) ? stateEW : stateNS;
            boolean hoz = (config[3] == 1);

            drawSingleLight(g2d, finalX, finalY, dynamicD, finalState, hoz);
        }
    }


                                    // Fake data
    private java.util.List<MockVehicle> getActualVehicleList() {
        java.util.List<MockVehicle> list = new java.util.ArrayList<>();
        // Test thử: Xe cứu thương chạy thẳng hướng Bắc (0 độ)
        list.add(new MockVehicle("AMBULANCE", 380, 450, 50, 0.0));
        // Test thử: Con xe CAR đang ôm cua nghiêng góc 45 độ chuẩn bị sang ngã rẽ phải!
        list.add(new MockVehicle("CAR", 220, 180, 35, 45.0)); 
        return list;
    }


    private void drawVehicles(Graphics2D g2d) {
        if (backgroundImage == null || simulationEngine == null) return;

        double scaleX = (double) mapDrawW / backgroundImage.getWidth();
        double scaleY = (double) mapDrawH / backgroundImage.getHeight();

        // Lấy danh sách xe thực tế (Lúc này xe đã có thuộc tính getAngle() trả về độ)
        java.util.List<MockVehicle> vehicleList = getActualVehicleList(); 

        for (MockVehicle vehicle : vehicleList) {
            BufferedImage vehicleImg = traffic.render.Renderer.getSprite(vehicle.getType());

            // 1. Tính toán vị trí tâm (Center) của con xe sau khi nhân tỷ lệ Zoom
            int vx = mapDrawX + (int) (vehicle.getX() * scaleX);
            int vy = mapDrawY + (int) (vehicle.getY() * scaleY);
            int vw = (int) (30 * scaleX);
            int vh = (int) (50 * scaleY);

            // Tọa độ tâm của con xe để làm điểm neo xoay
            int centerX = vx + vw / 2;
            int centerY = vy + vh / 2;

            // --- 💡 KỸ THUẬT XOAY ẢNH MƯỢT MÀ THEO GÓC ĐỘ ---
            // Lưu lại trạng thái gốc của Graphics trước khi xoay để các xe khác không bị quay theo
            java.awt.geom.AffineTransform oldTransform = g2d.getTransform();

            // Chuyển đổi góc từ Độ (Degree) sang Radian vì Java chỉ hiểu Radian
            double radians = Math.toRadians(vehicle.getAngle());

            // Ra lệnh cho Graphics xoay một góc 'radians' quanh tâm 'centerX, centerY' của con xe
            g2d.rotate(radians, centerX, centerY);

            // 2. Vẽ con xe (Lúc này Graphics đã xoay nên ảnh vẽ ra sẽ tự động nghiêng theo góc cua)
            if (vehicleImg != null) {
                g2d.drawImage(vehicleImg, vx, vy, vw, vh, null);
            } else {
                g2d.setColor(Color.BLUE);
                g2d.fillRect(vx, vy, vw, vh);
            }

            // Khôi phục lại trạng thái Graphics về ban đầu để vẽ các thành phần tiếp theo không bị nghiêng
            g2d.setTransform(oldTransform);

            // 3. Hiển thị thông số vận tốc và góc xoay thật lên đầu xe cho thầy cô check
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString(vehicle.getSpeed() + " km/h", vx, vy - 15);
            g2d.drawString("Góc: " + (int)vehicle.getAngle() + "°", vx, vy - 5);

            // Cơ chế bóp còi hú
            if (vehicle.getType().equalsIgnoreCase("AMBULANCE") || vehicle.getSpeed() > 60) {
                traffic.sounds.SoundManager.play("HORN");
            }
        }
    }
}