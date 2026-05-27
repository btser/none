package traffic.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {

    private JButton btnAddVehicle;
    private JButton btnZoomIn;
    private JButton btnZoomOut;
    private JButton btnPause;
    private JButton btnResume;

    private Object simulationEngine; 

    public ControlPanel(Object engine) {
        this.simulationEngine = engine;

        this.setPreferredSize(new Dimension(240, 0));
        this.setBackground(new Color(33, 37, 43)); 
        
        javax.swing.border.Border etchedBorder = BorderFactory.createEtchedBorder();
        this.setBorder(BorderFactory.createTitledBorder(
            etchedBorder, 
            "BẢNG ĐIỀU KHIỂN MÔ PHỎNG", 
            javax.swing.border.TitledBorder.CENTER, 
            javax.swing.border.TitledBorder.TOP, 
            new Font("Arial", Font.BOLD, 12), 
            Color.WHITE
        ));

        this.setLayout(new GridLayout(0, 1, 0, 15));
        initComponents();
        setupEvents();
    }
    
    private void initComponents() {
        Font buttonFont = new Font("Arial", Font.BOLD, 13);

        btnAddVehicle = new JButton("SPAWN");
        btnZoomIn = new JButton("ZOOM IN");
        btnZoomOut = new JButton("ZOOM OUT");
        btnPause = new JButton("PAUSE");
        btnResume = new JButton("RESUME");

        btnAddVehicle.setFont(buttonFont);
        btnZoomIn.setFont(buttonFont);
        btnZoomOut.setFont(buttonFont);
        btnPause.setFont(buttonFont);
        btnResume.setFont(buttonFont);

        btnPause.setBackground(new Color(255, 204, 204));  
        btnResume.setBackground(new Color(204, 255, 204)); 

        this.add(btnAddVehicle);
        this.add(btnZoomIn);
        this.add(btnZoomOut);
        this.add(btnPause);
        this.add(btnResume);
    }

    private void setupEvents() {
        // Nút Spawn
        btnAddVehicle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("[UI] Clicked: Thêm xe -> Gọi hàm spawn của Bảo.");
            }
        });

        // Bỏ logic instanceof lỗi thời, để trống để file Main hứng trực tiếp qua Getter
        btnZoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("[UI] Clicked: Zoom In.");
            }
        });

        btnZoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("[UI] Clicked: Zoom Out.");
            }
        });

        // Nút Pause
        btnPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("[UI] Clicked: Pause -> Gọi hàm dừng Thread mô phỏng.");
                btnPause.setEnabled(false);
                btnResume.setEnabled(true);
            }
        });

        // Nút Resume
        btnResume.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("[UI] Clicked: Resume -> Gọi hàm chạy tiếp Thread mô phỏng.");
                btnPause.setEnabled(true);
                btnResume.setEnabled(false);
            }
        });
    }

    // --- CÁC HÀM GETTER ĐỂ FILE MAIN KẾT NỐI SỰ KIỆN ---
    public JButton getBtnZoomIn() {
        return this.btnZoomIn;
    }

    public JButton getBtnZoomOut() {
        return this.btnZoomOut;
    }

    public JButton getBtnPause() {
        return this.btnPause;
    }

    public JButton getBtnResume() {
        return this.btnResume;
    }
}