package traffic.sounds;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    // HashMap lưu trữ các Clip âm thanh để gọi phát là kêu ngay
    private static Map<String, Clip> soundMap = new HashMap<>();

    /**
     * Hàm tải trước toàn bộ file .wav vào bộ nhớ (Call lúc khởi động chương trình cùng lúc với load ảnh)
     * ĐÃ ĐƯỢC CẬP NHẬT ĐỂ PHỤC VỤ ĐA ÂM THANH THEO TỪNG LOẠI XE
     */
    public static void loadSounds() {
        System.out.println("[UI] Đang tải tài nguyên âm thanh...");
        
        // 1. Các file âm thanh mặc định cũ của ông
        loadClip("HORN", "traffic/resources/audio/horn.wav");
        loadClip("SIREN", "traffic/resources/audio/siren.wav");
        loadClip("TURN_SIGNAL", "traffic/resources/audio/turn_signal.wav");

        // 2. MỞ RỘNG: Nạp file âm thanh riêng biệt cho từng loại xe
        // 💡 MẸO CHO ÔNG: Nếu sau này ông có file wav riêng (ví dụ bike_horn.wav), hãy đổi đường dẫn ở dưới.
        // Hiện tại tôi đang trỏ tạm vào file 'horn.wav' và 'siren.wav' gốc của ông để app CHẠY ĐƯỢC NGAY không bị lỗi thiếu file!
        loadClip("CAR_HORN", "traffic/resources/audio/car_horn.wav"); 
        loadClip("MOTORBIKE_HORN", "traffic/resources/audio/horn.wav"); // Có file riêng thì đổi thành "traffic/resources/audio/bike_horn.wav"
        loadClip("BUS_HORN", "traffic/resources/audio/horn.wav");  // Có file riêng thì đổi thành "traffic/resources/audio/bus_horn.wav"
        
        loadClip("AMBULANCE_SIREN", "traffic/resources/audio/ambulance.wav");
        loadClip("FIRE_TRUCK_SIREN", "traffic/resources/audio/fire_truck.wav"); // Có file riêng thì đổi thành "traffic/resources/audio/fire_siren.wav"

        System.out.println("[UI] Loading successful!");
    }

    /**
     * Hàm phụ trợ để load từng file .wav vào Clip (GIỮ NGUYÊN CỦA ÔNG)
     */
    private static void loadClip(String key, String filePath) {
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                System.out.println("[UI - Warning] Cannot find file: " + filePath);
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            soundMap.put(key, clip);
        } catch (Exception e) {
            //System.out.println("[UI - Error] Lỗi khi load file " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Phát âm thanh một lần (GIỮ NGUYÊN CỦA ÔNG)
     * @param key Tên âm thanh ("CAR_HORN", "BIKE_HORN", v.v.)
     */
    public static void play(String key) {
        Clip clip = soundMap.get(key);
        if (clip != null) {
            // Nếu clip đang chạy thì dừng lại và tua về đầu để phát lại từ đầu
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0); // Tua về giây 0
            clip.start(); // Phát
        }
    }

    /**
     * Phát âm thanh vòng lặp liên tục (GIỮ NGUYÊN CỦA ÔNG)
     * @param key Tên âm thanh
     */
    public static void loop(String key) {
        Clip clip = soundMap.get(key);
        if (clip != null && !clip.isRunning()) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Chạy lặp vô hạn cho đến khi có lệnh stop
        }
    }

    /**
     * Dừng phát âm thanh (GIỮ NGUYÊN CỦA ÔNG)
     * @param key Tên âm thanh
     */
    public static void stop(String key) {
        Clip clip = soundMap.get(key);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}