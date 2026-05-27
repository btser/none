package traffic.main;

import javax.swing.*;
import java.awt.*;

import traffic.components.ControlPanel;
import traffic.components.SimulationPanel;
import traffic.render.Renderer;
import traffic.sounds.SoundManager;

public class Main {
    public static void main(String[] args) {
        Renderer.loadSprites();
        SoundManager.loadSounds();

        // 2. Khởi tạo cửa sổ chính (JFrame)
        JFrame frame = new JFrame("Traffic simulation system");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 700); // Tăng chiều rộng lên 1100 để chứa thêm sidebar 240px cho thoáng
        frame.setLayout(new BorderLayout());

        // Giả lập đối tượng Engine (Sau này thay bằng class thật của Tuấn Anh)
        Object mockEngine = new Object();

        // 3. Khởi tạo 2 Panel thành phần của ông
        SimulationPanel simPanel = new SimulationPanel(mockEngine);
        ControlPanel controlPanel = new ControlPanel(mockEngine);

        // --- SỬA NÚT ZOOM IN (Đã đổi tên biến thành simPanel) ---
        controlPanel.getBtnZoomIn().addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                simPanel.zoomIn(); // Đã sửa từ simulationPanel -> simPanel
            }
        });

        // --- SỬA NÚT ZOOM OUT (Đã đổi tên biến thành simPanel) ---
        controlPanel.getBtnZoomOut().addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                simPanel.zoomOut(); // Đã sửa từ simulationPanel -> simPanel
            }
        });

        // 4. Sắp xếp bố cục: Bản đồ ở giữa (CENTER), Bảng điều khiển ở bên TRÁI (WEST)
        frame.add(simPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.WEST);

        // Hiển thị cửa sổ ở giữa màn hình máy tính
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // 5. GIẢ LẬP VÒNG LẶP ĐỔI MÀU ĐÈN (Test chức năng nhận tín hiệu)
        new Thread(() -> {
            String[] states = {"RED", "GREEN", "YELLOW"};
            int index = 0;
            
            while (true) {
                try {
                    // Cập nhật trạng thái đèn giả lập vào Panel vẽ
                    String currentColor = states[index];
                    simPanel.setMockLightColor(currentColor);
                    
                    //System.out.println("[Tín hiệu nhóm] Đèn đổi sang màu: " + currentColor);
                    
                    // Phát thử tiếng còi xe hoặc xi nhan mỗi lần đổi đèn cho vui tai
                    if (currentColor.equals("GREEN")) {
                        SoundManager.play("HORN");
                    }

                    index = (index + 1) % states.length;
                    Thread.sleep(3000); // 3 giây đổi màu một lần
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}