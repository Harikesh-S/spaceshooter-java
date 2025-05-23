/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import spaceshooter.gfx.Effect;
import spaceshooter.gfx.GameScreen;
import spaceshooter.gfx.GameSprites;

/**
 *
 * @author Harikesh
 */
public class Player extends Ship {
    
    public static final float PLAYER_SPEED = 10*(float)(Game.HEIGHT*Game.SCALE)/1080F;
    public static final float PLAYER_ACCEL = 0.1F*(float)(Game.HEIGHT*Game.SCALE)/1080F;
    public static final int PLAYER_HEALTH = 3,PLAYER_WT=1;
    public static final float PLAYER_SHIELD = 2F, PLAYER_SHIELD_REGEN = 0.02F;
    public static final float PLAYER_OMEGA = 5;
    public static final float PLAYER_SCALE = 1F;
    
    public Point2D.Double destPos = new Point2D.Double();
    
    public static Weapon[] WEAPONS = new Weapon[]{
        new Weapon(1,20,1),
        new Weapon(2,100,2),
        new Weapon(4,200,2)
    };
    
    public boolean rapidFire = false;
    private long lastWeaponSwitch = -100;
    
    Effect[] PLAYER_EFFECTS = new Effect[]{
        new Effect("/sprites/effects/player_fire",
                new int[]{10,10,10},
                false,false,scale
        ),
    };
    
    
    public Player(Point2D.Double p,Point2D.Double v,double ang,double o,
            Weapon[] ws) {
        super(GameSprites.PLAYER[Game.Settings.playerNumber][Game.Settings.playerColor],
                p,v,PLAYER_SCALE,ang,o,PLAYER_OMEGA,
                PLAYER_HEALTH,PLAYER_SHIELD,
                PLAYER_SPEED,PLAYER_SHIELD_REGEN,
                PLAYER_ACCEL,PLAYER_WT,ws);
        radius = sprite.width/2*scale;
        center = new Point2D.Float(radius,50F*scale);
        currentWeapon=1;
    }
    
    public void updateDamage() {
        for(int i=0;i<weapons.length;i++) {
            weapons[i].damage=WEAPONS[i].damage*Game.Data.skillPoints[0];
        }
    }
    
    public void updateHealth() {
        maxHealth = PLAYER_HEALTH*Game.Data.skillPoints[1];
        health=maxHealth;
    }
    
    public void updateShield() {
        maxShield = PLAYER_SHIELD*Game.Data.skillPoints[2];
        shield=maxShield;
    }
    
    public void drawGraphics(Graphics g,Point2D.Double sPos) {
        // FOR DEBUGGING
        
//        g.setColor(Color.blue);
//        g.drawLine(
//                (int) ((pos.x - sPos.x + scale*sprite.width/2) *Game.SCALE), 
//                (int) ((pos.y - sPos.y + scale*sprite.height/2)*Game.SCALE),
//                (int) Game.mousePosition.x, (int) Game.mousePosition.y);
        
    }
    public void tick(ArrayList<Integer> inputs,Point2D.Double sPos) {
        // weapon reloads
        weapons[0].tick();
        weapons[currentWeapon].tick();
        
        // player movement
        if(inputs.contains(87)||inputs.contains(38)) {
            reqdVel.y= -1;
        }
        else if(inputs.contains(83)||inputs.contains(40)) {
            reqdVel.y= 1;
        }
        else {
            reqdVel.y= 0;
        }
        if(inputs.contains(65)||inputs.contains(37)) {
            reqdVel.x= -1;
        }
        else if(inputs.contains(68)||inputs.contains(39)) {
            reqdVel.x= 1;
        }
        else {
            reqdVel.x= 0;
        }  
        
        // weapons
        if(inputs.contains(69)) {
            if(Game.tickCount-lastWeaponSwitch>60) {
                currentWeapon = (currentWeapon+1)%(Game.Data.unlockedWeapons+1);
                if(currentWeapon==0) currentWeapon=1;   // skip primary
                lastWeaponSwitch = Game.tickCount;
            }
        }
        
        if(inputs.contains(32)||rapidFire) {
            shoot();
        }
        
        if(inputs.contains(16)) {
            shootSpecial();
        }
        
        
        
        // mouse : player angle
        
        double xDiff =  ((pos.x - sPos.x + scale*sprite.width/2) *Game.SCALE - Game.mousePosition.x);
        double yDiff =  ((pos.y - sPos.y + scale*sprite.height/2)*Game.SCALE - Game.mousePosition.y);
        reqdAngle = 90 -  Math.toDegrees(Math.atan2(yDiff, xDiff));
        if(reqdAngle<0) reqdAngle+=360;
        
        
        // booster if player is pointed at the direction of required motion
        
        xDiff =  reqdVel.x;
        yDiff =  reqdVel.y;
        
        // testing for deceleration in dir oppposite to current motion
        if((int)reqdVel.x ==0&&(int)reqdVel.y ==0) {
            xDiff = -vel.x;
            yDiff = -vel.y;
        }
        
        float reqdMotionAngle = 360 -90 - (float) Math.toDegrees(Math.atan2(yDiff, xDiff));
        if(reqdMotionAngle>=360) reqdMotionAngle-=360;
        float angleDiff = (float) Math.abs(reqdMotionAngle-angle)%360;
        if(angleDiff>180) angleDiff = Math.abs(angleDiff-360);
        
        
        if((angleDiff<=45)&&(vel.x!=0||vel.y!=0)) {
            acceleration = Player.PLAYER_ACCEL*1.2F;
            speed = Player.PLAYER_SPEED*1.2F;
            PLAYER_EFFECTS[0].effectAngle = angle;
            Game.Audio.startBoost();
            if(!effects.contains(PLAYER_EFFECTS[0]))
                effects.add(PLAYER_EFFECTS[0]);
        }
        else {
            acceleration = Player.PLAYER_ACCEL;
            speed = Player.PLAYER_SPEED;
            Game.Audio.stopBoost();
            if(effects.contains(PLAYER_EFFECTS[0]))
                effects.remove(PLAYER_EFFECTS[0]);
        }
        
        if(shield<maxShield) {
            if((int)shield!=(int)(shield+shieldRegen))
                Game.Audio.playSFX(4);
        }
        super.tick();
    }
    
    
    @Override
    public void render(Point2D.Double screenPos,int[] imagePixels) {
        setModel();
        super.render(screenPos,imagePixels);
    }   
    
    
    public void mousePressed(MouseEvent e) {
        if(SwingUtilities.isRightMouseButton(e))
            shootSpecial();
        else
            rapidFire = true;
    }
    
    public void mouseReleased(MouseEvent e) {
        rapidFire = false;
    }
    
    
    void shoot(){
        Point2D.Double dest = new Point2D.Double(GameScreen.screenPos.x+Game.mousePosition.x,
            GameScreen.screenPos.y+Game.mousePosition.y);
        super.shoot(dest,0);
    }
    
    void shootSpecial(){
        Point2D.Double dest = new Point2D.Double(GameScreen.screenPos.x+Game.mousePosition.x,
            GameScreen.screenPos.y+Game.mousePosition.y);
        super.shoot(dest, currentWeapon);
    }
    
    public void setModel() {
        //System.out.println("Updating player sprite");
        sprite = GameSprites.PLAYER[Game.Settings.playerNumber][Game.Settings.playerColor];
    }
    
    public double getAngleToDest() {
        double xDiff =  (this.pos.x - this.destPos.x);
        double yDiff =  (this.pos.y - this.destPos.y);
        double angleToDest = 90 -  Math.toDegrees(Math.atan2(yDiff, xDiff));
        if(angleToDest<0) angleToDest+=360;
        return angleToDest;
    }
}
