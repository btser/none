package traffic.main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList; // Thư viện cơ bản, 100% không lỗi
import java.util.List;

import traffic.components.ControlPanel;
import traffic.components.SimulationPanel;
import traffic.render.Renderer;
import traffic.sounds.SoundManager;

public class Main {
    // Tạo danh sách xe tĩnh toàn cục, bọc trong Collections.synchronizedList để chống đụng độ đa luồng
    private static List<traffic.components.MockVehicle> staticList = java.util.Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        Renderer.loadSprites();
        SoundManager.loadSounds();

        // Khởi tạo cửa sổ chính
        JFrame frame = new JFrame("Traffic simulation system");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 700); 
        frame.setLayout(new BorderLayout());

        Object mockEngine = new Object();

        SimulationPanel simPanel = new SimulationPanel(mockEngine);
        ControlPanel controlPanel = new ControlPanel(mockEngine);

        // NÚT ZOOM IN
        controlPanel.getBtnZoomIn().addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                simPanel.zoomIn(); 
            }
        });

        // NÚT ZOOM OUT
        controlPanel.getBtnZoomOut().addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                simPanel.zoomOut(); 
            }
        });

// =====================================================================
        // PAUSE AND RESUME
        // =====================================================================
        final boolean[] isPaused = {false}; 

        // 1. Pause 
        controlPanel.getBtnPause().addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                isPaused[0] = true; // Bật phanh -> Luồng di chuyển xe sẽ đứng im
                //System.out.println("[Hệ thống] Đã bấm PAUSE -> Tạm dừng mô phỏng.");
            }
        });

        // 2. Xử lý khi ấn nút Resume (Tiếp tục)
        controlPanel.getBtnResume().addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                isPaused[0] = false; // Nhả phanh -> Xe tiếp tục chạy tiếp
                //System.out.println("[Hệ thống] Đã bấm RESUME -> Tiếp tục mô phỏng.");
            }
        });

        frame.add(simPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.WEST);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // NẠP SẴN 2 XE BAN ĐẦU VÀO DANH SÁCH TÍN HIỆU NGAY KHI BẬT APP
        staticList.add(new traffic.components.MockVehicle("CAR", 0, 140, 40, 0));
        staticList.add(new traffic.components.MockVehicle("AMBULANCE", 180, 0, 60, 90));
        staticList.add(new traffic.components.MockVehicle("MOTORBIKE", 800, 100, 30, 180));
        staticList.add(new traffic.components.MockVehicle("FIRE_TRUCK", 220, 550, 55, 270));

        // 5. LUỒNG 1: GIẢ LẬP ĐỔI MÀU ĐÈN (3 giây/lần)
        new Thread(() -> {
            String[] states = {"RED", "GREEN", "YELLOW"};
            int index = 0;
            while (true) {
                try {
                    String currentColor = states[index];
                    simPanel.setMockLightColor(currentColor);
                    if (currentColor.equals("GREEN")) {
                        SoundManager.play("HORN");
                    }
                    index = (index + 1) % states.length;
                    Thread.sleep(3000); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    
        // =====================================================================
        // LUỒNG 2: GAME LOOP - DI CHUYỂN XE VÀ TỰ ĐỘNG TIÊU HỦY KHI HẾT ĐƯỜNG (60 FPS)
        // =====================================================================
        new Thread(() -> {
            while (true) {
                try {
                    // 🛠️ BỌC PHANH: CHỈ CẬP NHẬT VỊ TRÍ KHI KHÔNG BỊ PAUSE
                    if (!isPaused[0]) {
                        // Dùng khối synchronized để khóa danh sách lại khi đang tính toán, tránh lỗi đa luồng
                        synchronized (staticList) {
                            // 1. Cập nhật vị trí tịnh tiến thẳng cho từng con xe (GIỮ NGUYÊN BỘ GỐC CỦA ÔNG)
                            // =====================================================================
                            // ĐOẠN XỬ LÝ DI CHUYỂN VÀ BÓP CÒI CỦA XE TRONG LUỒNG 2 (FILE MAIN.JAVA)
                            // =====================================================================
                            for (traffic.components.MockVehicle vehicle : staticList) {
                                if (vehicle.getType().equals("CAR")) {
                                    vehicle.setX(vehicle.getX() + 2); // Xe CAR chạy ngang
                                    
                                    // 🛠️ SỬA ĐOẠN NÀY: Gọi thẳng setHonking thôi, còi sẽ tự kêu chuẩn loại xe và KHÔNG DELAY
                                    if (vehicle.getX() > 300 && vehicle.getX() < 450) {
                                        vehicle.setHonking(true);  // Đi vào ngã tư thì bật còi
                                    } else {
                                        vehicle.setHonking(false); // Ra khỏi ngã tư thì tắt còi
                                    }
                                } else if (vehicle.getType().equals("AMBULANCE")) {
                                    vehicle.setY(vehicle.getY() + 2); // Xe AMBULANCE chạy dọc
                                }
                            }

                            // 2. KIỂM TRA BIÊN: Nếu chạy quá lề thì tự động TIÊU HỦY hoàn toàn khỏi RAM (GIỮ NGUYÊN BỘ GỐC CỦA ÔNG)
                            for (int i = staticList.size() - 1; i >= 0; i--) {
                                traffic.components.MockVehicle v = staticList.get(i);
                                if (v.getType().equals("CAR") && v.getX() > 950) {
                                    staticList.remove(i);
                                    //System.out.println("[Hệ thống] Đã tiêu hủy 1 xe CAR ra khỏi bộ nhớ.");
                                } else if (v.getType().equals("AMBULANCE") && v.getY() > 950) {
                                    staticList.remove(i);
                                    //System.out.println("[Hệ thống] Đã tiêu hủy 1 xe AMBULANCE ra khỏi bộ nhớ.");
                                }
                            }
                        }
                    }

                    // 3. Tạo một bản sao danh sách an toàn gửi sang cho UI Panel vẽ (Đưa ra ngoài if để luôn phục vụ Zoom mượt mà)
                    simPanel.setVehicleList(new ArrayList<>(staticList));

                    // 4. Ép giao diện vẽ lại
                    simPanel.repaint(); 
                    
                    Thread.sleep(16); 
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}