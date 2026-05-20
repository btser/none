import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


public class SoundSystem {
    public static void playSound(String fileSound) {
        try {
            File soundFile = new File(fileSound);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }

    public static void playCarHorn() {
        playSound("src/resources/car_horn.wav");
    }
    public static void playTruckHorn() {
        playSound("src/resources/truck_horn.wav");
    }
    public static void playMotorbikeHorn() {
        playSound("src/resources/motorbike_horn.wav");
    }

    public static void playAmbulanceSiren() {
        playSound("src/resources/ambulance_siren.wav");
    }
    public static void playFireTruckSiren() {
        playSound("src/resources/fire_truck_siren.wav");
    }
    public static void playPoliceSiren() {
        playSound("src/resources/police_siren.wav");
    }

    public static void playCarTurnSignal() {
        playSound("src/resources/car_turn_signal.wav");
    }
    public static void playTruckTurnSignal() {
        playSound("src/resources/truck_turn_signal.wav");
    }
}