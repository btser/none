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
     * Thư mục lưu trữ: assets/audio/
     */
    public static void loadSounds() {
        System.out.println("[UI] Đang tải tài nguyên âm thanh...");
        loadClip("HORN", "traffic/resources/audio/horn.wav");
        loadClip("SIREN", "traffic/resources/audio/siren.wav");
        loadClip("TURN_SIGNAL", "traffic/resources/audio/turn_signal.wav");
        System.out.println("[UI] Loading succesful!");
    }

    /**
     * Hàm phụ trợ để load từng file .wav vào Clip
     */
    private static void loadClip(String key, String filePath) {
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                System.out.println("[UI - Warning] Cannot found file: " + filePath);
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
     * Phát âm thanh một lần (Ví dụ: bấm còi bíp bíp, hoặc tiếng tạch xi nhan)
     * @param key Tên âm thanh ("HORN", "TURN_SIGNAL")
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
     * Phát âm thanh vòng lặp liên tục (Dành riêng cho còi hú SIREN của xe cứu thương)
     * @param key Tên âm thanh ("SIREN")
     */
    public static void loop(String key) {
        Clip clip = soundMap.get(key);
        if (clip != null && !clip.isRunning()) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Chạy lặp vô hạn cho đến khi có lệnh stop
        }
    }

    /**
     * Dừng phát âm thanh (Ví dụ: xe cứu thương chạy ra khỏi map thì phải tắt còi hú)
     * @param key Tên âm thanh ("SIREN")
     */
    public static void stop(String key) {
        Clip clip = soundMap.get(key);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}
