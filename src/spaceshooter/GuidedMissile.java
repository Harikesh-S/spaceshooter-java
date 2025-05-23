/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter;

import java.awt.geom.Point2D;
import spaceshooter.gfx.Effect;
import spaceshooter.gfx.GameSprites;
import spaceshooter.gfx.Sprite;

/**
 *
 * @author Harikesh
 */
public class GuidedMissile extends Bullet {
    public static float SCALE = 1F;
    public static double[] SPEED = new double[]{ 
        7D*Game.HEIGHT/1080,
        10D*Game.HEIGHT/1080,
    };
    
    private static float[] MAX_OMEGA = new float[] { 
        1F,
        2F,
    };
    private static int[] TTL = new int[] { 
        500,
        200,
    };
    
    
    int version = 0;
    public int aliveTime = TTL[version];
    
    public double reqdAngle = 0;
    public Ship target = null;
    
    GuidedMissile(Point2D.Double p,Point2D.Double v,double ang,Ship origin,int dam, int ver, Ship dest) {
        super(GameSprites.G_MISSILES[Math.min(dam/2,GameSprites.LASERS.length-1)],p,v,SCALE,ang,0D,0F,origin,dam);
        bypassShields = true;
        target = dest;
        version = ver;
        DESTROY_EFFECT = new Effect(GameSprites.SMOKE_ANIMATION_SPRITES,
                new int[]{2,2,2,2,2,2,2,2,2},
                false,true,(double)scale*(double)sprite.height/50D);
    }
    
    // constructor with given version and target as player
    GuidedMissile(Point2D.Double p,Point2D.Double v,double ang,Ship origin,int dam,int ver) {
        this(p,v,ang,origin,dam,ver,Game.Data.player);
    }
    // constructor for use with enemy ships, target is player
    GuidedMissile(Point2D.Double p,Point2D.Double v,double ang,Ship origin,int dam) {
        this(p,v,ang,origin,dam,0,Game.Data.player);
    }
    
    public static Bullet getBullet(Point2D.Double pos, Point2D.Double vel, double angle, Ship origin, int damage, int ver, Ship target) {
        return (Bullet) new GuidedMissile(pos,vel,angle,origin,damage,ver,target);
    }
    
    // get bullet for use with enemy ships, target is player
    public static Bullet getBullet(Point2D.Double pos, Point2D.Double vel, double angle, Ship origin, int damage,int ver) {
        return (Bullet) new GuidedMissile(pos,vel,angle,origin,damage,ver);
    }
    
    public static Bullet getBullet(Point2D.Double pos, Point2D.Double vel, double angle, Ship origin, int damage) {
        return (Bullet) new GuidedMissile(pos,vel,angle,origin,damage);
    }
    
    public static Sprite getSprite(int dam){
        return GameSprites.G_MISSILES[Math.min(dam/2,GameSprites.LASERS.length-1)];
    }
    
    
    @Override
    public void tick() {
        if(destroyed) {
            die();
            displaySprite = false;
            if(!effectsFG.contains(DESTROY_EFFECT))
                alive=false;
        }
        else {
            // attempt to follow the target
            // find the angle to target and attempt to follow it
            if(target!=null) {
                double xDiff =  (target.pos.x- this.pos.x);
                double yDiff =  (target.pos.y- this.pos.y);
                reqdAngle = 270 - Math.toDegrees(Math.atan2(yDiff, xDiff));
                if(reqdAngle<0) reqdAngle+=360;
                // follow require angle
                double diff = reqdAngle-angle;
                if(diff<-180) diff+=360;
                if(diff>+180) diff-=360;
                if(Math.abs(diff)<=MAX_OMEGA[version]) {
                    omega = 0;
                    angle = reqdAngle;
                }
                else {
                    if(diff>0) omega = + MAX_OMEGA[version];
                    else omega = - MAX_OMEGA[version];
                }
                this.vel.x = Math.cos(Math.toRadians(angle+90))*this.SPEED[version];
                this.vel.y = -Math.sin(Math.toRadians(angle+90))*this.SPEED[version];
            }
            super.tick();
            // reduce time to live
            aliveTime--;
            if(aliveTime<=0)
                destroyed = true;
        }
    }
}
