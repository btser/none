import javax.swing.*;

public class MainTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Hệ Thống Mô Phỏng Giao Thông Đô Thị - Giao Diện");
        SimulationPanel panel = new SimulationPanel();

        frame.add(panel);
        frame.setSize(800, 600); // Đặt kích thước cửa sổ phù hợp với ảnh nền ngã tư
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Hiển thị ra giữa màn hình
        frame.setVisible(true);
    }
}