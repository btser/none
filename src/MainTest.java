import javax.swing.*;
import java.awt.*;

public class MainTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Test Giao Diện Hệ Thống Giao Thông");
        SimulationPanel simPanel = new SimulationPanel();
        
        // Tạo thanh công cụ phía trên gồm các nút bấm điều khiển mode
        JPanel controlPanel = new JPanel();
        JButton btnBasic = new JButton("Basic Mode (Rect)");
        JButton btnGraphic = new JButton("Graphic Mode (Sprite)");
        JButton btnHorn = new JButton("Thử còi xe");

        btnBasic.addActionListener(e -> simPanel.setViewMode(false));
        btnGraphic.addActionListener(e -> simPanel.setViewMode(true));
        btnHorn.addActionListener(e -> SoundSystem.playCarHorn()); // Bấm là kêu còi luôn

        controlPanel.add(btnBasic);
        controlPanel.add(btnGraphic);
        controlPanel.add(btnHorn);

        frame.setLayout(new BorderLayout());
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(simPanel, BorderLayout.CENTER);

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}