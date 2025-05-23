/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import spaceshooter.gfx.Sprite;
import spaceshooter.gfx.TutorialWindow;

/**
 *
 * @author Harikesh
 */
public class GameData {
    
    public int state = 0;
    public boolean enemySpawn = false;
    
    public TutorialWindow currentTutorial = null;
    
    public ArrayList<Enemy> enemies = new ArrayList<>();
    public ArrayList<Bullet> bullets = new ArrayList<>();
    public Boss currentBoss = null;
    
    public Point2D.Double lastEnemySpawn = new Point2D.Double();
    
    public Player player;
    public byte unlockedWeapons = 1;
    public byte[] skillPoints = new byte[]{1,1,1};
    public byte level = 0;
    public int exp = 0;
    public long ticks = 0;
    
    public static final Point2D.Double[] gameStatePos = new Point2D.Double[]{
        new Point2D.Double(3000, 0),
        new Point2D.Double(),
        new Point2D.Double()
    };
    
    public GameData() {
        // generate random boss locations
        // +- 5K -> 15K in x and y
        gameStatePos[1].setLocation(
                gameStatePos[0].x+((Math.random()>=0.5)?1:-1)*(10000*Math.random()+5000),
                gameStatePos[0].y+((Math.random()>=0.5)?1:-1)*(10000*Math.random()+5000)
        );
        gameStatePos[2].setLocation(
                gameStatePos[1].x+((Math.random()>=0.5)?1:-1)*(10000*Math.random()+5000),
                gameStatePos[1].y+((Math.random()>=0.5)?1:-1)*(10000*Math.random()+5000)
        );
    }
    
    public void stateAction() {
        this.ticks++;
        switch(state) {
            // Game start
            case 0: // Set destination to tutorial enemy
                enemies.clear();
                Game.Data.player.destPos.setLocation(gameStatePos[0]);
                state = 1;
                break;
            case 1: // Movement tutorial
                if(currentTutorial==null)
                    currentTutorial = new TutorialWindow("Movement",
                            new String[]{"Move using WASD or arrow keys.Aim the ship using mouse.",
                                "Moving in the direction you're facing gives a boost to top speed and acceleration.",
                                "Hold Q to toggle locked cam or set locked cam in settings."
                            },
                        2,new Sprite("/images/movement.png"));
                break;
            case 2: // Wait for player to move
                if(player.pos.distance(new Point2D.Double())>500)
                    state = 3;
                break;
            case 3: // Compass tutorial
                if(currentTutorial==null)
                    currentTutorial = new TutorialWindow("Pausing and the Compass",
                            new String[]{"The compass leads you to your next objective.",
                                "",
                                "Press ESC to pause and save the game",
                            },
                        4,new Sprite("/images/compass.png"));
                break;
            case 4: // Wait for player to reach tutorial enemy
                if(player.pos.distance(gameStatePos[0])<1500) {
                    enemies.add(new Enemy(0,new Point2D.Double(gameStatePos[0].x,gameStatePos[0].y),
                            new Point2D.Double(),0D));
                    state = 5;
                }
                break;
            case 5: // Combat tutorial
                if(currentTutorial==null)
                    currentTutorial = new TutorialWindow("Combat",
                            new String[]{"Aim your weapon using your cursor.",
                                "Press left click or SPACE to shoot LASERs.",
                                "Press right click or SHIFT to shoot MISSILEs.",
                            },
                        18,new Sprite("/images/weapons.png"));
                break;
            case 18: // Health tutorial
                if(currentTutorial==null)
                    currentTutorial = new TutorialWindow("Health and Shields",
                            new String[]{"Every ship has HEALTH and SHIELDs",
                                "SHIELDs can block LASERs and regenerate over time.",
                                "Each enemy defeated has a 50% chance to restore 1 HEALTH.",
                                "Missiles bypass SHIELDs and damage HEALTH."
                            },
                        6,new Sprite("/images/health.png"));
                break;                
            case 6: // Wait for player to kill enemy
                if(enemies.size()==0)
                    state = 7;
                break;
            case 7: // Xp tutorial
                if(currentTutorial==null)
                    currentTutorial = new TutorialWindow("Experience and Levels",
                            new String[]{"Defeating enemies gives EXP.",
                                "Collect enough EXP to LEVEL up.",
                                "Allocate skill points gained from level ups to",
                                "HEALTH, SHIELD or DAMAGE.",
                            },
                        8,new Sprite("/images/levels.png"));                
                break;
            case 8: // Enable enemy spawn, direct player to boss 1
                enemySpawn = true;
                Game.Data.player.destPos.setLocation(gameStatePos[1]);
                state = 9;
                break;
            case 9: // Wait for player to reach boss 1
                if(player.pos.distance(gameStatePos[1])<2500)
                    state = 10;
                break;
            case 10: // Spawn boss, disable enemy spawn
                currentBoss = new Boss(0,new Point2D.Double(gameStatePos[1].x,gameStatePos[1].y),
                        new Point2D.Double(),0);
                Game.Data.player.destPos = currentBoss.pos;
                enemySpawn = false;
                enemies.add(currentBoss);
                state = 11;
                break;
            case 11: // Wait for player to defeat boss
                if(currentBoss.alive==false)
                    state = 12;
                break;
            case 12: // Enable enemy spawn, direct player to bboss 2
                enemySpawn = true;
                Game.Data.player.destPos.setLocation(gameStatePos[2]);
                // Enable guided missiles
                unlockedWeapons = 2;
                state = 13;
                break;
            case 13: // Weapon switch tutorial
                if(currentTutorial==null)
                    currentTutorial = new TutorialWindow("New Weapon!",
                            new String[]{"You have unlocked the guided missile.",
                                "Guided missiles track the nearest enemy.",
                                "Press E to swap between secondary weapons.",
                            },
                        14,new Sprite("/images/weapon_switch.png"));
                break;
            case 14: // Wait for player to reach boss 2
                if(player.pos.distance(gameStatePos[2])<2500)
                    state = 15;
                break;
            case 15: // Spawn boss, disable enemy spawn
                currentBoss = new Boss(1,new Point2D.Double(gameStatePos[2].x,gameStatePos[2].y),
                        new Point2D.Double(),0);
                Game.Data.player.destPos = currentBoss.pos;
                enemies.add(currentBoss);
                enemySpawn = false;
                state = 16;
                break;
            case 16: // Wait for player to defeat boss
                if(currentBoss.alive==false)
                    state = 17;
                break;
            case 17: // Game END
                Game.jumpToScreen = 7;
                Game.newGame = false;
                break;
        }
    }
//    
//    public double dist2D(Point2D.Double a,Point2D.Double b) {
//        return Math.sqrt(Math.pow((a.x-b.x),2)+Math.pow((a.y-b.y),2));
//    }
}
