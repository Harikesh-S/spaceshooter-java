/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import spaceshooter.Boss;
import spaceshooter.Bullet;
import spaceshooter.Enemy;
import spaceshooter.Game;
import spaceshooter.Laser;
import spaceshooter.Player;
import spaceshooter.SpaceObject;

/**
 *
 * @author Harikesh
 */
public class GameScreen extends Screen {
    public static int SCREEN_MARGIN = (int)(300F/1080F*(float)(Game.HEIGHT));
    public static Point2D.Double screenPos = new Point2D.Double();
    
    
    private ArrayList<SpaceObject> deadObjects = new ArrayList<>();
    
    private static final float uiScale = (float)Game.HEIGHT/1080F;
    private static final int uiWeaponsX = 1920-240,
            uiWeaponsY = 1080-130;
    private static final int uiHealthX = 10,
            uiHealthY = 10;
    private static final int uiCompassX = 1920-120,
            uiCompassY = 10;
    private static final int uiStatsX = 10,
            uiStatsY = 1080-120;
    private static final double statBarW = 1000D*Game.HEIGHT/1080D,
            statBarH = 20D*Game.HEIGHT/1080D,    
            statBarX = Game.WIDTH/2-statBarW/2,
            healthBarY = 100D*Game.HEIGHT/1080D,
            shieldBarY = healthBarY+statBarH+10D*Game.HEIGHT/1080D;

    
    public GameScreen() {
        super();
        System.out.println("Creating Game Screen");
        Game.Data.player = new Player(
                new Point2D.Double(),
                new Point2D.Double(),
                0D,0D,Player.WEAPONS
        );
        Game.Data.player.setModel();
        
        Game.Data.enemies.clear();
        
        centerScreen();
    }
    
    void centerScreen() {
        pos.x=Game.Data.player.pos.x-Game.WIDTH/2;
        pos.y=Game.Data.player.pos.y-Game.HEIGHT/2;
    }
    
    @Override
    public void tick(ArrayList<Integer> inputs) {
        // pausing 
        if(inputs.contains(KeyEvent.VK_ESCAPE)) {
            Game.jumpToScreen = 1;
            Game.Audio.playSFX(1);
            return;
        }
        
        // dont run the game if tutorial screen is up
        if(Game.Data.currentTutorial!=null) {
            if(!Game.Data.currentTutorial.alive)
                Game.Data.currentTutorial=null;
            Game.Data.player.rapidFire = false;
            return;
        }
        
        // Game state actions
        Game.Data.stateAction();
        
        // level up
        if(Game.Data.exp>(Game.Data.level+1)*50&&Game.Data.level<10) {
            Game.Data.exp-=(++Game.Data.level)*50;
        }
        
        
        // death
        if(!Game.Data.player.alive) {
            Game.jumpToScreen = 3;
            Game.Audio.playSFX(5);
            return;
        }
        // invert screen lock
        boolean lockScreen = Game.Settings.lockScreen;
        if(inputs.contains(KeyEvent.VK_Q)) {
            lockScreen = !lockScreen;
        }
        
        // spawning enemies
        if(Game.Data.enemySpawn) {
            if(Game.Data.enemies.size()<5) {
                ////System.err.println(Game.Data.dist2D(Game.Data.player.pos, Game.Data.lastEnemySpawn));
                if(Game.Data.player.pos.distance(Game.Data.lastEnemySpawn)>400) {
                    int nEnemies = (int)(Math.random()*2)+1;
                    int max = (int)(1.4*Game.WIDTH), min = (int)(0.8*Game.WIDTH);
                    Point2D.Double enemySpawn;
                    // Generate random point between min and max dist
                    double arcLen = Math.random()*(max-min) + min;
                    double playerVelAngle = Math.toDegrees(Math.atan2(Game.Data.player.vel.y,Game.Data.player.vel.x));
                    double arcAngle = Math.toRadians(playerVelAngle+Math.random()*90-45);
                    enemySpawn = new Point2D.Double(
                            Game.Data.player.pos.x+Math.cos(arcAngle)*arcLen,
                            Game.Data.player.pos.y+Math.sin(arcAngle)*arcLen
                    );
                    // if generated point is free of enemies
                    boolean free = true;
                    for(Enemy e:Game.Data.enemies) {
                        if(e.pos.distance(enemySpawn)<400) {
                            free = false;
                            break;
                        }
                    }
                    if(free) {
                        //System.err.println(Game.Data.player.pos.x+":"+enemySpawn.x);
                        //System.err.println(Game.Data.player.pos.y+":"+enemySpawn.y);
                        int[][] offsets = new int[][]{
                            {-100,-100},
                            { 100,-100},
                            { 100, 100},
                            {-100, 100},
                        };
                        for(int en=0;en<nEnemies;en++){
                            int type = (int)(Math.random()*4);
                            Game.Data.enemies.add(new Enemy(type,
                                    new Point2D.Double(
                                            enemySpawn.x+offsets[en][0],
                                            enemySpawn.y+offsets[en][1]
                                    ),
                                    new Point2D.Double(),0
                                )
                            );
                        }
                        Game.Data.lastEnemySpawn.setLocation(Game.Data.player.pos);
                    }
                }
            }
        }
        
        // updating player
        Game.Data.player.tick(inputs,pos);
        // updating enemies
        // remove dead enemeis and add exp
        for(int i=0;i<Game.Data.enemies.size();i++) {
            Enemy e = Game.Data.enemies.get(i);
            e.tick();
            if(!e.alive)
                deadObjects.add(e);
        }
        for(int i=0;i<deadObjects.size();i++) {
            Enemy e = (Enemy)deadObjects.get(i);
            Game.Data.exp += e.expYield();
            Game.Data.enemies.remove(e);
            // 50% chance to heal per enemy
            if(e.getClass()==Enemy.class||e.getClass()==Boss.class) {
                if(Math.random()>0.5) {
                    if(Game.Data.player.health<Game.Data.player.maxHealth) {
                        Game.Data.player.health++;
                        Game.Audio.playSFX(9);
                    }
                }
            }
        }
        deadObjects.clear();
        // despawn enemies out of screen
        for(int i=0;i<Game.Data.enemies.size();i++) {
            Enemy e = Game.Data.enemies.get(i);
            if((e.pos.x<(pos.x-Game.WIDTH*Game.SCALE)||e.pos.y<(pos.y-Game.HEIGHT*Game.SCALE))||
                    (e.pos.x>(pos.x+2*Game.WIDTH*Game.SCALE)||e.pos.y>(pos.y+2*Game.HEIGHT*Game.SCALE))) {
                // removing out of bounds enemies
                if(e.getClass()!=Boss.class) {    // dont remove bosses 
                    deadObjects.add(e);
                    //System.err.println("DESPAWN "+Game.Data.enemies.size());
                }
            }
        }
        for(int i=0;i<deadObjects.size();i++) {
            Enemy e = (Enemy)deadObjects.get(i);
            Game.Data.enemies.remove(e);
        }
        deadObjects.clear();
                
        for(int i=0;i<Game.Data.bullets.size();i++) {
            Bullet b = Game.Data.bullets.get(i);
            b.tick();
            if(!b.alive)
                deadObjects.add(b);
            if((b.pos.x<(pos.x-Game.WIDTH*Game.SCALE)||b.pos.y<(pos.y-Game.HEIGHT*Game.SCALE))||
                    (b.pos.x>(pos.x+2*Game.WIDTH*Game.SCALE)||b.pos.y>(pos.y+2*Game.HEIGHT*Game.SCALE))) 
                deadObjects.add(b);
        }
        for(int i=0;i<deadObjects.size();i++) {
            Bullet b = (Bullet)deadObjects.get(i);
            Game.Data.bullets.remove(b);
        }
        deadObjects.clear();
        
        // collisions
        // enemy collision : player, enemies
        for(Enemy e: Game.Data.enemies) {
            e.collision(Game.Data.player);
            for(Enemy e1:Game.Data.enemies) {
                if(e==e1) continue;
                e.collision(e1);
            }
        }
        // bullet collision : player, enemies, bullets
        for(int i=0;i<Game.Data.bullets.size();i++) {
            if(Game.Data.bullets.get(i).destroyed)
                continue;   // ignore bullets that have already collided
            Game.Data.bullets.get(i).collision(Game.Data.player);
            for(int j=0;j<Game.Data.enemies.size();j++) {
                Game.Data.bullets.get(i).collision(Game.Data.enemies.get(j));
            }
            
            for(int j=0;j<Game.Data.bullets.size();j++) {
                if(i==j)    continue;   // ignore self
                if((Game.Data.bullets.get(i).getClass()==Laser.class)&&
                        (Game.Data.bullets.get(j).getClass()==Laser.class))
                    continue;   // lasers do not collide with other lasers
                if(Game.Data.bullets.get(j).destroyed)
                    continue;   // ignore bullets that have already collided
                Game.Data.bullets.get(i).collision(Game.Data.bullets.get(j));
            }
        }
        
        // screen movement
        
        
        vel.x=0;
        vel.y=0;
        if(lockScreen)
            centerScreen();
        else {
            // move screen if player moves to margin
            if((Game.Data.player.pos.x-pos.x)<SCREEN_MARGIN) {
                vel.x = Game.Data.player.vel.x;
            }
            if((Game.Data.player.pos.x-pos.x)+Game.Data.player.sprite.width*Game.Data.player.scale>Game.WIDTH-SCREEN_MARGIN) {
                vel.x = Game.Data.player.vel.x;
            }

            // if mouse is over margin move screen till it makes player reach margin
            if(Game.mousePosition.x>Game.WIDTH-SCREEN_MARGIN) {
                vel.x = Game.Data.player.vel.x;
                if(Game.Data.player.pos.x-pos.x-vel.x-Game.Data.player.speed>SCREEN_MARGIN) 
                    vel.x += Game.Data.player.speed;
                vel.x = (int)Math.copySign(Math.min(Math.abs(vel.x), Game.Data.player.speed*1.1), vel.x);
            }
            if(Game.mousePosition.x<SCREEN_MARGIN) {
                vel.x = Game.Data.player.vel.x;
                if(Game.Data.player.pos.x-pos.x-vel.x+Game.Data.player.speed+Game.Data.player.sprite.width*Game.Data.player.scale<Game.WIDTH-SCREEN_MARGIN)
                    vel.x += -Game.Data.player.speed;
                vel.x = (int)Math.copySign(Math.min(Math.abs(vel.x), Game.Data.player.speed*1.1), vel.x);
            }

            // move screen if player moves to margin
            if((Game.Data.player.pos.y-pos.y)<SCREEN_MARGIN) {
                vel.y = Game.Data.player.vel.y;
            }
            if((Game.Data.player.pos.y-pos.y)+Game.Data.player.sprite.height*Game.Data.player.scale>Game.HEIGHT-SCREEN_MARGIN) {
                vel.y =Game.Data.player.vel.y;
            }

            // if mouse is over margin move screen till it makes player reach margin
            if(Game.mousePosition.y>Game.HEIGHT-SCREEN_MARGIN) {
                vel.y = Game.Data.player.vel.y;
                if(Game.Data.player.pos.y-pos.y-vel.y-Game.Data.player.speed>SCREEN_MARGIN) 
                    vel.y += Game.Data.player.speed;
                vel.y = (int)Math.copySign(Math.min(Math.abs(vel.y), Game.Data.player.speed*1.1), vel.y);
            }
            if(Game.mousePosition.y<SCREEN_MARGIN) {
                vel.y = Game.Data.player.vel.y;
                if(Game.Data.player.pos.y-pos.y-vel.y+Game.Data.player.speed+Game.Data.player.sprite.height*Game.Data.player.scale<Game.HEIGHT-SCREEN_MARGIN)
                    vel.y += -Game.Data.player.speed;
                vel.y = (int)Math.copySign(Math.min(Math.abs(vel.y), Game.Data.player.speed*1.1), vel.y);
            }
        }
        
        // updating screen
        super.tick(inputs);
        screenPos.setLocation(pos);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if(Game.Data.currentTutorial!=null) {
            Game.Data.currentTutorial.mouse();
            return;
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if(Game.Data.currentTutorial!=null) {
            return;
        }
        Game.Data.player.mousePressed(e);
    }
    
    private long lastLevelUp = -100;
    @Override
    public void mouseReleased(MouseEvent e) {
        if(Game.Data.currentTutorial!=null) {
            Game.Data.currentTutorial.mouse();
            return;
        }
        Game.Data.player.mouseReleased(e);
        if(Game.mousePosition.x>(uiStatsX+210)*uiScale
                &&Game.mousePosition.x<(uiStatsX+210)*uiScale+GameSprites.UI_SPRITES[9].width*uiScale) {
            if(!((Game.Data.skillPoints[0]+Game.Data.skillPoints[1]+Game.Data.skillPoints[2]-3)<Game.Data.level))
                return;
            if(Game.tickCount-lastLevelUp<Game.targetUPS)
                return;
            if(Game.mousePosition.y>(uiStatsY+37)*uiScale
                    &&Game.mousePosition.y<(uiStatsY+37)*uiScale+GameSprites.UI_SPRITES[9].height*uiScale
                    &&Game.Data.skillPoints[0]<5) {
                Game.Data.skillPoints[0]++;
                Game.Data.player.updateDamage();
                lastLevelUp=Game.tickCount;
            }
            else if(Game.mousePosition.y>(uiStatsY+61)*uiScale
                    &&Game.mousePosition.y<(uiStatsY+61)*uiScale+GameSprites.UI_SPRITES[9].height*uiScale
                    &&Game.Data.skillPoints[1]<5) {
                Game.Data.skillPoints[1]++;
                Game.Data.player.updateHealth();
                lastLevelUp=Game.tickCount;
            }
            else if(Game.mousePosition.y>(uiStatsY+84)*uiScale
                    &&Game.mousePosition.y<(uiStatsY+84)*uiScale+GameSprites.UI_SPRITES[9].height*uiScale
                    &&Game.Data.skillPoints[2]<5) {
                Game.Data.skillPoints[2]++;
                Game.Data.player.updateShield();
                lastLevelUp=Game.tickCount;
            }
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {    }
        
    @Override
    void renderfg(int[] imagePixels) {
        // drawing background effects
        Game.Data.player.renderEffects(pos, imagePixels);
        for(int i=0;i<Game.Data.enemies.size();i++) {
            Game.Data.enemies.get(i).renderEffects(pos, imagePixels);
        }
        // drawing sprites
        for(int i=0;i<Game.Data.bullets.size();i++) {
            Game.Data.bullets.get(i).render(pos, imagePixels);
        }
        Game.Data.player.render(pos, imagePixels);
        for(int i=0;i<Game.Data.enemies.size();i++) {
            Game.Data.enemies.get(i).render(pos, imagePixels);
        }
        // drawing fg effects
        Game.Data.player.renderEffectsFG(pos, imagePixels);
        for(int i=0;i<Game.Data.enemies.size();i++) {
            Game.Data.enemies.get(i).renderEffectsFG(pos, imagePixels);
        }
        for(int i=0;i<Game.Data.bullets.size();i++) {
            Game.Data.bullets.get(i).renderEffectsFG(pos, imagePixels);
        }
        
        
        renderUI(imagePixels);
        
        
        if(Game.Data.currentTutorial!=null)
            Game.Data.currentTutorial.render(imagePixels);
    }
    
    @Override
    public void renderGraphics(Graphics g) {
        Game.Data.player.drawGraphics(g,pos);
        //Game.Data.player.drawBounds(g, pos);
        //Game.Data.enemies.get(0).drawBounds(g,pos);
        if(Game.Data.currentBoss!=null) {
            if(Game.Data.currentBoss.alive) {
                // If boss exists and is alive then display health and shield bar
                double bossHealth = (double)Game.Data.currentBoss.health
                        /(double)Game.Data.currentBoss.maxHealth,
                        bossShield = Game.Data.currentBoss.shield
                        /Game.Data.currentBoss.maxShield;
                g.setColor(new Color(0x555555));
                g.fillRect((int)statBarX, (int)healthBarY, (int)statBarW, (int)statBarH);
                g.fillRect((int)statBarX, (int)shieldBarY, (int)statBarW, (int)statBarH);
                g.setColor(new Color(0xFCEC52));
                g.fillRect((int)statBarX, (int)healthBarY, (int)(statBarW*bossHealth), (int)statBarH);
                g.setColor(new Color(0xB8F3FF));
                g.fillRect((int)statBarX, (int)shieldBarY, (int)(statBarW*bossShield), (int)statBarH);
            }
        }
        // level info
        g.setFont(MenuScreen.textFont.deriveFont(Font.PLAIN,20*uiScale));
        String lvl = "Level : "+Game.Data.level+" [ "+Game.Data.exp+"/"+(50+50*Game.Data.level)+"]";
        if(Game.Data.level==10) lvl = "Level : MAX";
        g.setColor(new Color(0x333333));
        g.drawString(lvl, (int)((uiStatsX+20+2)*uiScale), (int)((uiStatsY+25+2)*uiScale));
        g.setColor(new Color(0xffd948));
        g.drawString(lvl, (int)((uiStatsX+20)*uiScale), (int)((uiStatsY+25)*uiScale));
        
        
        if(Game.Data.currentTutorial!=null)
            Game.Data.currentTutorial.renderGraphics(g);
    }
    
    void renderUI(int[] imagePixels) {
        
        // weapons panel
        GameSprites.UI_SPRITES[0].displaySprite(new Point2D.Double((uiWeaponsX)*uiScale,(uiWeaponsY)*uiScale), 
                new Point2D.Double(), imagePixels, uiScale, 0);
        // weapon sprites
        Sprite weapon;
        weapon = Game.Data.player.weapons[Game.Data.player.currentWeapon].getSprite();
        weapon.displaySprite(new Point2D.Double((1920-60)*uiScale,(1080-65)*uiScale), 
                new Point2D.Double(),new Point2D.Double(-weapon.width*uiScale/2,-weapon.height*uiScale/2) ,imagePixels, uiScale, 45);
        weapon = Game.Data.player.weapons[0].getSprite();
        weapon.displaySprite(new Point2D.Double((1920-180)*uiScale,(1080-65)*uiScale), 
                new Point2D.Double(),new Point2D.Double(-weapon.width*uiScale/2,-weapon.height*uiScale/2) ,imagePixels, uiScale, 45);
        
        // weapon reloads
        float reloadPercent;
        int base;
        reloadPercent = (float)Game.Data.player.weapons[0].reloadTime
                /(float)Game.Data.player.weapons[0].reload;
        base = (int)((1920-230)*uiScale+ ((1080-120)*uiScale)*Game.WIDTH);
        int x = (int)((1920-230)*uiScale), y=(int)((1080-120)*uiScale);
        for(int i=0;i<112*uiScale*reloadPercent;i++)
            for(int j=0;j<100*uiScale;j++) {
                if(imagePixels[base+i*Game.WIDTH+j]==-9991272)
                    imagePixels[base+i*Game.WIDTH+j]-=0x333333;
            }
        
        reloadPercent = (float)Game.Data.player.weapons[Game.Data.player.currentWeapon].reloadTime
                /(float)Game.Data.player.weapons[Game.Data.player.currentWeapon].reload;
        base = (int)((1920-110)*uiScale+ ((1080-120)*uiScale)*Game.WIDTH);
        for(int i=0;i<112*uiScale*reloadPercent;i++)
            for(int j=0;j<100*uiScale;j++) {
                if(imagePixels[base+i*Game.WIDTH+j]==-9991272)
                    imagePixels[base+i*Game.WIDTH+j]-=0x333333;
            }        
        
        // health panel
        GameSprites.UI_SPRITES[1].displaySprite(new Point2D.Double((uiHealthX)*uiScale,(uiHealthY)*uiScale), 
                new Point2D.Double(), imagePixels, uiScale, 0);
        
        // health and shield
        int i=0;
        for(i=0;i<Game.Data.player.maxHealth;i++) {
            if(i<Game.Data.player.health) {
                GameSprites.UI_SPRITES[3].displaySprite(new Point2D.Double((uiHealthX+70+(20*i))*uiScale,(uiHealthY+20)*uiScale), 
                        new Point2D.Double(), imagePixels, uiScale, 0);
            }
        }
        for(;i<15;i++) {
            GameSprites.UI_SPRITES[2].displaySprite(new Point2D.Double((uiHealthX+70+(20*i))*uiScale,(uiHealthY+20)*uiScale), 
                        new Point2D.Double(), imagePixels, uiScale, 0);
        }
        for(i=0;i<Game.Data.player.maxShield;i++) {
            if(i<(int)Game.Data.player.shield) {
                GameSprites.UI_SPRITES[4].displaySprite(new Point2D.Double((uiHealthX+130+(20*i))*uiScale,(uiHealthY+70)*uiScale), 
                        new Point2D.Double(), imagePixels, uiScale, 0);
            }
        }
        for(;i<10;i++) {
            GameSprites.UI_SPRITES[2].displaySprite(new Point2D.Double((uiHealthX+130+(20*i))*uiScale,(uiHealthY+70)*uiScale), 
                        new Point2D.Double(), imagePixels, uiScale, 0);
        }
        
        // compass panel
        GameSprites.UI_SPRITES[5].displaySprite(new Point2D.Double((uiCompassX)*uiScale,(uiCompassY)*uiScale),
                new Point2D.Double(), imagePixels, uiScale, 0);
        
        GameSprites.UI_SPRITES[6].displaySprite(new Point2D.Double((uiCompassX+70)*uiScale,(uiCompassY+65)*uiScale), 
                new Point2D.Double(),new Point2D.Double(-25,-30), imagePixels, uiScale, Game.Data.player.getAngleToDest());
        
        // stats panel
        GameSprites.UI_SPRITES[7].displaySprite(new Point2D.Double((uiStatsX)*uiScale,(uiStatsY)*uiScale), 
                new Point2D.Double(), imagePixels, uiScale, 0);
        
        boolean pointsAvailable = (Game.Data.skillPoints[0]+Game.Data.skillPoints[1]+Game.Data.skillPoints[2]-3)
                <Game.Data.level;
        
        for(i=0;i<Game.Data.skillPoints[0];i++)
            GameSprites.UI_SPRITES[8].displaySprite(new Point2D.Double((uiStatsX+110+(20*i))*uiScale,(uiStatsY+37)*uiScale), 
                        new Point2D.Double(), imagePixels, uiScale, 0);
        if(pointsAvailable&&Game.Data.skillPoints[0]<5)
            GameSprites.UI_SPRITES[9].displaySprite(new Point2D.Double((uiStatsX+210)*uiScale,(uiStatsY+37)*uiScale), 
                        new Point2D.Double(), imagePixels, uiScale, 0);
        
        for(i=0;i<Game.Data.skillPoints[1];i++)
            GameSprites.UI_SPRITES[8].displaySprite(new Point2D.Double((uiStatsX+110+(20*i))*uiScale,(uiStatsY+61)*uiScale), 
                        new Point2D.Double(), imagePixels, uiScale, 0);
        if(pointsAvailable&&Game.Data.skillPoints[1]<5)
            GameSprites.UI_SPRITES[9].displaySprite(new Point2D.Double((uiStatsX+210)*uiScale,(uiStatsY+61)*uiScale), 
                        new Point2D.Double(), imagePixels, uiScale, 0);
        
        
        for(i=0;i<Game.Data.skillPoints[2];i++)
            GameSprites.UI_SPRITES[8].displaySprite(new Point2D.Double((uiStatsX+110+(20*i))*uiScale,(uiStatsY+84)*uiScale), 
                        new Point2D.Double(), imagePixels, uiScale, 0);
        if(pointsAvailable&&Game.Data.skillPoints[2]<5)
            GameSprites.UI_SPRITES[9].displaySprite(new Point2D.Double((uiStatsX+210)*uiScale,(uiStatsY+84)*uiScale), 
                        new Point2D.Double(), imagePixels, uiScale, 0);
        
        
        if(Game.Data.currentTutorial!=null)
            Game.Data.currentTutorial.render(imagePixels);
    }
}
