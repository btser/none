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

    private double zoomScale = 1.0;

    private int mapDrawX = 0;
    private int mapDrawY = 0;
    private int mapDrawW = 800;
    private int mapDrawH = 550;

    // Danh sách xe động nhận từ file Main.java truyền sang để vẽ
    private java.util.List<MockVehicle> uiVehicleList = new java.util.ArrayList<>();

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

    public void setVehicleList(java.util.List<MockVehicle> list) {
        this.uiVehicleList = list;
    }

    public void zoomIn() {
        this.zoomScale += 0.1;
        if (this.zoomScale > 3.0) this.zoomScale = 3.0;
        this.repaint();
    }

    public void zoomOut() {
        this.zoomScale -= 0.1;
        if (this.zoomScale < 0.5) this.zoomScale = 0.5;
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
        int paddingLeft = 70; 

        if (backgroundImage != null) {
            double imgRatio = (double) backgroundImage.getWidth() / backgroundImage.getHeight();
            double panelRatio = (double) (panelWidth - paddingLeft) / panelHeight;

            if (imgRatio > panelRatio) {
                mapDrawW = panelWidth - paddingLeft;
                mapDrawH = (int) ((panelWidth - paddingLeft) / imgRatio);
            } else {
                mapDrawH = panelHeight;
                mapDrawW = (int) (panelHeight * imgRatio);
            }

            mapDrawW = (int) (mapDrawW * zoomScale);
            mapDrawH = (int) (mapDrawH * zoomScale);
            
            mapDrawX = paddingLeft;
            mapDrawY = 20; 

            g2d.drawImage(backgroundImage, mapDrawX, mapDrawY, mapDrawW, mapDrawH, null);
        }

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
            {150, 44,  1, 1}, {210, 213, 1, 1}, {325, 102,  2, 0}, {67,  140, 2, 0}, 
            {555, 44, 1, 1}, {615, 213, 1, 1}, {723, 102, 2, 0}, {465, 140, 2, 0}, 
            {150, 332, 1, 1}, {210, 495, 1, 1}, {312, 380, 2, 0}, {80, 418, 2, 0}, 
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

    private void drawVehicles(Graphics2D g2d) {
        if (backgroundImage == null) return;

        double scaleX = (double) mapDrawW / backgroundImage.getWidth();
        double scaleY = (double) mapDrawH / backgroundImage.getHeight();

        for (MockVehicle vehicle : uiVehicleList) {
            BufferedImage vehicleImg = traffic.render.Renderer.getSprite(vehicle.getType());

            if (vehicleImg != null) {
                // =====================================================================
                // 🛠️ TỰ ĐỘNG ÉP TỶ LỆ RIÊNG CHO TỪNG LOẠI XE (SỬA ĐOẠN NÀY LÀ XONG)
                // =====================================================================
                double vehicleSizeFactor = 0.4; // Mặc định cho ô tô và cứu thương của ông đang đẹp

                // Check loại xe để tinh chỉnh lại hệ số cho vừa mắt
                switch (vehicle.getType()) {
                    case "MOTORBIKE":
                        vehicleSizeFactor = 0.12; // Ép xe máy nhỏ hẳn lại (ông tăng giảm số này tùy ý)
                        break;
                    case "FIRE_TRUCK":
                        vehicleSizeFactor = 0.12; // Ép xe cứu hỏa nhỏ bớt lại cho cân đối với map
                        break;
                    case "CAR":
                        vehicleSizeFactor = 0.18;  // Giữ nguyên mức ông thấy ok
                        break;
                    case "AMBULANCE":
                        vehicleSizeFactor = 0.18;  // Giữ nguyên mức ông thấy ok
                        break;
                }
                
                // Kích thước xe tự động ăn theo hệ số riêng biệt ở trên
                int vw = (int) (vehicleImg.getWidth() * vehicleSizeFactor * scaleX);
                int vh = (int) (vehicleImg.getHeight() * vehicleSizeFactor * scaleY);

                // Tọa độ vẽ xe
                int vx = mapDrawX + (int) (vehicle.getX() * scaleX);
                int vy = mapDrawY + (int) (vehicle.getY() * scaleY);

                int centerX = vx + vw / 2;
                int centerY = vy + vh / 2;

                // Xoay ảnh xe theo góc hướng data
                java.awt.geom.AffineTransform oldTransform = g2d.getTransform();
                g2d.rotate(Math.toRadians(vehicle.getAngle()), centerX, centerY);

                // Vẽ ảnh xe chuẩn tỷ lệ gốc
                g2d.drawImage(vehicleImg, vx, vy, vw, vh, null);

                // =====================================================================
                // 🛠️ CHỈ THÊM ĐOẠN ĐÈN NHÁY NÀY - XOAY THEO HƯỚNG XE, CO GIÃN THEO ZOOM
                // =====================================================================
                if (vehicle.getType().equals("AMBULANCE") || vehicle.getType().equals("FIRE_TRUCK")) {
                    // Tạo công tắc nhấp nháy dựa trên thời gian hệ thống (cứ 200ms đổi trạng thái một lần)
                    boolean toggle = (System.currentTimeMillis() / 200) % 2 == 0;
                    
                    // Tính kích thước bóng đèn LED co giãn theo tỉ lệ zoom map
                    int ledSize = (int)(6 * scaleX); 
                    if (ledSize < 4) ledSize = 4; // Chống cháy nếu thu nhỏ quá đèn bị biến mất

                    if (toggle) {
                        // Trạng thái 1: Bên trái màu ĐỎ, bên phải màu XANH
                        g2d.setColor(Color.RED);
                        g2d.fillOval(centerX - ledSize, centerY - ledSize/2, ledSize, ledSize);
                        
                        g2d.setColor(Color.BLUE);
                        g2d.fillOval(centerX, centerY - ledSize/2, ledSize, ledSize);
                    } else {
                        // Trạng thái 2: Đảo ngược lại (Trái XANH, phải ĐỎ)
                        g2d.setColor(Color.BLUE);
                        g2d.fillOval(centerX - ledSize, centerY - ledSize/2, ledSize, ledSize);
                        
                        g2d.setColor(Color.RED);
                        g2d.fillOval(centerX, centerY - ledSize/2, ledSize, ledSize);
                    }
                }

                g2d.setTransform(oldTransform);


                                        //  Note & check khi bấm còi

                /*if (vehicle.isHonking()) {
                    // Check loại xe để gọi đúng âm thanh tương ứng
                    
                    // Vẽ hiệu ứng chữ "BÍP BÍP / PÍ PO" trên đầu xe (Ăn theo vx, vy ở trên)
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    if (vehicle.getType().equals("AMBULANCE") || vehicle.getType().equals("FIRE_TRUCK")) {
                        g2d.setColor(Color.ORANGE);
                        g2d.drawString("PÍ PO PÍ PO!!!", vx, vy - 18);
                    } else {
                        g2d.setColor(Color.RED);
                        g2d.drawString("BÍP BÍP!!!", vx, vy - 18);
                    }
                }*/

                // Hiển thị thông số vận tốc
                /*g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 11));
                g2d.drawString(vehicle.getSpeed() + " km/h", vx, vy - 5);*/
            } else {
                // Khối chống cháy nếu thiếu ảnh
                int vx = mapDrawX + (int) (vehicle.getX() * scaleX);
                int vy = mapDrawY + (int) (vehicle.getY() * scaleY);
                g2d.setColor(Color.BLUE);
                g2d.fillRect(vx, vy, (int)(35*scaleX), (int)(35*scaleY));
            }
        }
    }
}