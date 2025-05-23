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
public class Missile extends Bullet {
    public static float SCALE = 1F;
    public static double SPEED = 10D*Game.HEIGHT/1080;
    
    Missile(Point2D.Double p,Point2D.Double v,double ang,Ship origin,int dam) {
        super(GameSprites.MISSILES[Math.min(dam/2,GameSprites.LASERS.length-1)],p,v,SCALE,ang,0D,0F,origin,dam);
        bypassShields = true;
        DESTROY_EFFECT = new Effect(GameSprites.SMOKE_ANIMATION_SPRITES,
                new int[]{2,2,2,2,2,2,2,2,2},
                false,true,(double)scale*(double)sprite.height/50D);
    }
    
    public static Bullet getBullet(Point2D.Double pos, Point2D.Double vel, double angle, Ship origin, int damage) {
        return (Bullet) new Missile(pos,vel,angle,origin,damage);
    }
    
    public static Sprite getSprite(int dam){
        return GameSprites.MISSILES[Math.min(dam/2,GameSprites.LASERS.length-1)];
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
            super.tick();
        }
    }
}
