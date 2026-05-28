package traffic.components;

public class MockVehicle {
    private String type;       // Loại xe: "CAR", "BIKE", "AMBULANCE"
    private int x;             // Tọa độ X gốc trên ảnh map
    private int y;             // Tọa độ Y gốc trên ảnh map
    private int speed;         // Vận tốc xe (km/h)
    private double angle;      // Hướng đi: "BẮC", "NAM", "ĐÔNG", "TÂY"
    private boolean honking = false;

    public MockVehicle(String type, int x, int y, int speed, double angle) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.angle = angle;
    }

    public String getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public double getAngle() {
        return angle;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public boolean isHonking() {
        return honking;
    }

    // =====================================================================
    // 🛠️ HÀM SETTER THÔNG MINH: ĐÃ ĐƯỢC NÂNG CẤP ĐỂ TỰ CHỌN VÀ PHÁT SOUND CHỐNG DELAY
    // =====================================================================
    public void setHonking(boolean honking) {
        // Chỉ kích hoạt âm thanh khi trạng thái thay đổi từ IM LẶNG sang BÓP CÒI (false -> true)
        // Điều này giúp còi chỉ kêu ĐÚNG 1 PHÁT, không bị dập-tắt-liên-tục do Game Loop 60FPS
        if (honking && !this.honking) {
            // Xe loại nào tự động gọi Sound loại đó trong SoundManager
            switch (this.type) {
                case "CAR":
                    traffic.sounds.SoundManager.play("CAR_HORN");
                    break;
                case "BIKE":
                case "MOTORBIKE": // Ăn theo cả tên MOTORBIKE đang nạp trong file Main
                    traffic.sounds.SoundManager.play("MOTORBIKE_HORN");
                    break;
                case "BUS":
                    traffic.sounds.SoundManager.play("BUS_HORN");
                    break;
                case "AMBULANCE":
                    traffic.sounds.SoundManager.play("AMBULANCE_SIREN");
                    break;
                case "FIRE_TRUCK":
                    traffic.sounds.SoundManager.play("FIRE_SIREN");
                    break;
                default:
                    traffic.sounds.SoundManager.play("HORN"); // Tiếng còi mặc định dự phòng
                    break;
            }
        }
        
        // Cập nhật lại giá trị biến để SimulationPanel (UI) nhận biết và vẽ chữ "BÍP BÍP / PÍ PO"
        this.honking = honking;
    }
}