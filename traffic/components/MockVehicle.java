package traffic.components;

public class MockVehicle {
    private String type;       // Loại xe: "CAR", "BIKE", "AMBULANCE"
    private int x;             // Tọa độ X gốc trên ảnh map
    private int y;             // Tọa độ Y gốc trên ảnh map
    private int speed;         // Vận tốc xe (km/h)
    private double angle;  // Hướng đi: "BẮC", "NAM", "ĐÔNG", "TÂY"

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

    
}
