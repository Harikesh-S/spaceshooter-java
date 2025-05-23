/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 *
 * @author harik
 */
public class GameAudio {
    // AUDIO EFFECTS
    static String[] fgPaths = new String[]{
        "/music/select_002.wav",    //0
        "/music/select_005.wav",
        "/music/sfx_laser2.wav",    //2
        "/music/forceField_000.wav",
        "/music/sfx_shieldUp.wav",  //4
        "/music/sfx_lose.wav",
        "/music/missile.wav",       //6
        "/music/impactMetal_003.wav",
        "/music/thrusterFire_002.wav",//8
        "/music/powerUp2.wav",
    };
    // BACKGROUND
    static AudioInputStream bgAudioAIS;
    static Clip bgAudioClip;
    static FloatControl bgGainCtrl;
    // PLAYER BOOSTER (has to loop)
    static Clip playerBoost;
    GameAudio() {
        try {
            // Loading background audio
            String bgAudioPath = "/music/MyVeryOwnDeadShip.wav";
            System.out.println("Loading bg audio "+bgAudioPath);
            URL bgAudioURL = GameAudio.class.getResource(bgAudioPath);
            bgAudioAIS = AudioSystem.getAudioInputStream(bgAudioURL);
            
            bgAudioClip = AudioSystem.getClip();
            bgAudioClip.open(bgAudioAIS);
            bgAudioClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgAudioClip.start();
            // Gain control of the clip
            bgGainCtrl = (FloatControl)bgAudioClip.getControl(FloatControl.Type.MASTER_GAIN);
            bgAudioUpdate();
            
            System.out.println("Loading audio "+fgPaths[8]);
            AudioInputStream boostAIS = AudioSystem.getAudioInputStream(
                    GameAudio.class.getResource(fgPaths[8])
            );
            playerBoost = AudioSystem.getClip();
            playerBoost.open(boostAIS); 
            playerBoost.loop(Clip.LOOP_CONTINUOUSLY);
            playerBoost.stop();
            stopBoost();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void bgAudioUpdate() {
        float gain = (20f * (float) Math.log10(Game.Settings.bgAudioLevel));
        bgGainCtrl.setValue(gain);
    }
    
    public void startBoost() {
        float gain = (20f * (float) Math.log10(Game.Settings.fgAudioLevel));
        FloatControl gainCtrl = (FloatControl)playerBoost.getControl(FloatControl.Type.MASTER_GAIN);
        gainCtrl.setValue(gain);
        if(!playerBoost.isActive()) {
            playerBoost.setMicrosecondPosition(0);
            playerBoost.start();   
        }
    }
    
    public void stopBoost() {
        if(playerBoost.isActive())
            playerBoost.stop();
    }
    
    public void playSFX(int id) {
        new Thread(() -> {
            try {
                AudioInputStream fgAIS = null;
                try{
                    System.out.println("Loading audio "+fgPaths[id]);
                    fgAIS = AudioSystem.getAudioInputStream(GameAudio.class.getResource(fgPaths[id]));
                    Clip clip = AudioSystem.getClip();
                    clip.open(fgAIS);
                    float gain = (20f * (float) Math.log10(Game.Settings.fgAudioLevel));
                    FloatControl gainCtrl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                    gainCtrl.setValue(gain);
                    clip.start(); 
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
