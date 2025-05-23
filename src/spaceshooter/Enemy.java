/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter;

import java.awt.geom.Point2D;
import spaceshooter.gfx.GameSprites;
import spaceshooter.gfx.Sprite;

/**
 *
 * @author Harikesh
 */
public class Enemy extends Ship{
    // enemy defenitions
    static final float[] ENEMY_SCALE = new float[]{
        1F,
        1F,
        1F,
        1F,
    };
    static final int[] ENEMY_HEALTHS = new int[]{
        3,
        2,
        3,
        2,
    };
    static final float[] ENEMY_SHIELDS = new float[]{
        2F,
        1F,
        1F,
        1F,
    };
    static final float[] ENEMY_SPEEDS = new float[]{
        5F,
        5F,
        8F,
        5F,
    };
    static final float[] ENEMY_SHIELD_REGENS = new float[]{
        0.01F,
        0.01F,
        0.01F,
        0.02F,
    };
    static final float[] ENEMY_ACCELS = new float[]{
        0.1F,
        0.1F,
        0.1F,
        0.1F,
    };
    static final float[] ENEMY_MAX_OMEGAS = new float[]{
        5F,
        5F,
        5F,
        5F,
    };
    static final float[] ENEMY_WEIGHTS = new float[]{
        1F,
        1F,
        2F,
        1F,
    };
    
    static final Weapon[][] ENEMY_WEAPONS = new Weapon[][]{
        {new Weapon(1,60,1),},  // basic laser
        {new Weapon(2,100,1),}, // basic missile
        {new Weapon(1,1000,0),},// no weapon
        {new Weapon(3,200,1),}, // guided missile
    };
    
    static final int ENEMY_ACTIVATION_RANGE = 1000;
    
    static final int[] ENEMY_MIN_DIST = new int[]{
        300,
        300,
        0,
        300,
    };
    static final int[] ENEMY_MAX_DIST = new int[]{
        600,
        600,
        0,
        600,
    };
    static final int[] ENEMY_MAX_SHOOT_DIST = new int[]{
        1000,
        1200,
        0,
        1200,
    };
    static final int[] ENEMY_EXP_YIELDS = new int[]{
        20,
        30,
        40,
        30,
    };
    
    private int expYield,minRange,maxRange,shoRange;
    
    public int id = 0;
    public boolean active = false;
    
    public Enemy(int id,Point2D.Double p,Point2D.Double v,double a) {
        super(GameSprites.ENEMY[id],p,v,ENEMY_SCALE[id],a,0D,
                ENEMY_MAX_OMEGAS[id],
                ENEMY_HEALTHS[id],ENEMY_SHIELDS[id],
                ENEMY_SPEEDS[id]*(float)(Game.HEIGHT*Game.SCALE)/1080F,ENEMY_SHIELD_REGENS[id],
                ENEMY_ACCELS[id]*(float)(Game.HEIGHT*Game.SCALE)/1080F,ENEMY_WEIGHTS[id],
                ENEMY_WEAPONS[id]
        );
        this.id = id;
        this.expYield = ENEMY_EXP_YIELDS[id];
        this.minRange = ENEMY_MIN_DIST[id];
        this.maxRange = ENEMY_MAX_DIST[id];
        this.shoRange = ENEMY_MAX_SHOOT_DIST[id];
    }
    
    Enemy(Sprite spr,Point2D.Double p,Point2D.Double v,
            float sc,double a,double o,float maxO,
            int h,float sh,float sp,float reg,float accel,float wt,Weapon[] ws,
            int exp,int[] ranges){
        super(spr,p,v,sc,a,o,maxO,h,sh,sp,reg,accel,wt,ws);
        this.expYield = exp;
        this.minRange = ranges[0];
        this.maxRange = ranges[1];
        this.shoRange = ranges[2];
    }
    
    public int expYield(){
        return expYield;
    }
    
    
    @Override
    public void tick(){
        this.weapons[0].tick();
        if(!active) {
            this.reqdVel.x = 0;
            this.reqdVel.y = 0;
            this.vel.x = 0;
            this.vel.y = 0;
            if(this.pos.distance(Game.Data.player.pos)<ENEMY_ACTIVATION_RANGE)
                active=true;
        }
        else {
            // face the player
            
            Point2D.Double cPos = new Point2D.Double(pos.x+center.x,pos.y+center.y);
            Point2D.Double cPosPlayer = new Point2D.Double(
                    Game.Data.player.pos.x+Game.Data.player.center.x,
                    Game.Data.player.pos.y+Game.Data.player.center.y);
            
            double xDiff =  (cPos.x - cPosPlayer.x);
            double yDiff =  (cPos.y - cPosPlayer.y);
            reqdAngle = 270 - Math.toDegrees(Math.atan2(yDiff, xDiff));
            if(reqdAngle<0) reqdAngle+=360; 
            if(reqdAngle>360) reqdAngle-=360;
           
            int distFromPlayer = (int)this.pos.distance(Game.Data.player.pos);
            if(distFromPlayer>maxRange){
                // enemy too far away, move closer to player in line from current pos and player 
                // adding proportional controller to required velocity
                float reqdSpeed = (Math.min(1,
                        ((float)distFromPlayer-(float)maxRange)/100F));
                this.reqdVel.x = reqdSpeed*Math.cos(Math.toRadians((reqdAngle+270)%360));
                this.reqdVel.y = -reqdSpeed*Math.sin(Math.toRadians((reqdAngle+270)%360));
            }
            else if(distFromPlayer<minRange) {
                
                float reqdSpeed = (Math.min(1,
                        (minRange-(float)distFromPlayer)/50F));
                this.reqdVel.x = reqdSpeed*Math.cos(Math.toRadians((reqdAngle+90)%360));
                this.reqdVel.y = -reqdSpeed*Math.sin(Math.toRadians((reqdAngle+90)%360));
            }
            else {
                this.reqdVel.x = 0;
                this.reqdVel.y = 0;
            }
            // evade enemies
            for(Enemy e:Game.Data.enemies){
                if(e==this)
                    continue;
                // for each enemy if enemy is within 
                if(cPos.distance(e.pos.x+e.center.x,e.pos.y+e.center.x)<this.radius*3) {
                    // if enemy is moving away then dont dodge
                    //if(cPos.distance(e.pos)>cPos.distance(e.pos.x+e.vel.x,e.pos.y+e.vel.y)) {
                        // attempt to evade
                        //reqdVel.x*=-Math.copySign(1, vel.x);
                        //reqdVel.y*=-Math.copySign(1, vel.y);
                        double eReqdAngle = e.angle+270;
                        if(eReqdAngle>360) eReqdAngle-=360;
                        Point2D.Double enemyReqdVel = new Point2D.Double(
                                Math.cos(eReqdAngle),
                                Math.sin(eReqdAngle));
                        
                        reqdVel.x = - enemyReqdVel.y;
                        reqdVel.y = + enemyReqdVel.x;
                        if(cPos.distance(-reqdVel.x+e.pos.x, -reqdVel.y+e.pos.y)
                                <cPos.distance(e.pos)) {
                            reqdVel.x = + enemyReqdVel.y;
                            reqdVel.y = - enemyReqdVel.x;
                        }
                        
                        reqdVel.x = Math.copySign(1, reqdVel.x);
                        reqdVel.y = Math.copySign(1, reqdVel.y);
                        
                        //reqdVel.x = (reqdVel.x)/Math.abs(reqdVel.x);
                        //reqdVel.y = (reqdVel.y)/Math.abs(reqdVel.y);
                    //}
                }
            }
            // evade bullets
            for(Bullet b:Game.Data.bullets){
                // for each bullet if bullet is within 
                if(cPos.distance(b.pos.x+b.center.x,b.pos.y+b.center.x)<this.radius*3) {
                    // if bullet is moving away then dont dodge
                    if(cPos.distance(b.pos)>cPos.distance(b.pos.x+b.vel.x,b.pos.y+b.vel.y)) {
                        // attempt to evade
                        reqdVel.x = - b.vel.y;
                        reqdVel.y = + b.vel.x;
                        if(cPos.distance(-reqdVel.x+b.pos.x, -reqdVel.y+b.pos.y)
                                <cPos.distance(b.pos)) {
                            reqdVel.x = + b.vel.y;
                            reqdVel.y = - b.vel.x;
                        }

                        reqdVel.x = (reqdVel.x)/Math.abs(reqdVel.x);
                        reqdVel.y = (reqdVel.y)/Math.abs(reqdVel.y);
                    }
                }
            }
            // check if it has a line of fire if within max range
            if(distFromPlayer<shoRange) {
                boolean hasLineOfSight = true;
                Point2D.Double playerDest = new Point2D.Double();
                playerDest.x = cPosPlayer.x + 
                        Game.Data.player.vel.x*distFromPlayer/weapons[0].speed;
                playerDest.y = cPosPlayer.y + 
                        Game.Data.player.vel.y*distFromPlayer/weapons[0].speed;
                // check if there are other enemies in line of sight
                // if the shortest distance from the line between the ship and the player 
                // is less than the radius of the enemy
                for(Enemy e:Game.Data.enemies) {
                    // ignore self
                    if(e==this) continue;
                    
                    Point2D.Double ecPos = new Point2D.Double(e.pos.x+e.center.x,e.pos.y+e.center.y);
                    
                    if(lineSegmentPtDistance(cPos,playerDest,ecPos)<e.radius+ 2*this.weapons[0].findHalfWidth()){
                        hasLineOfSight = false;
                        break;
                    }
                }
                // angle too far off to shoot
//                double angleDiff = Math.abs(reqdAngle-angle);
//                if(angleDiff>180) angleDiff = Math.abs(angleDiff-360);
//                if(angleDiff>30) {
//                    System.err.println("Angle too high");
//                    hasLineOfSight=false;
//                }
//                    System.out.println(reqdAngle+" "+angle);
                if(hasLineOfSight) {
                    this.shoot(playerDest, 0);
                }
            }
        }
        super.tick();
    }
}
