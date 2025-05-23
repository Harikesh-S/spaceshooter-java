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
public class Laser extends Bullet{
    
    public static float SCALE = 0.5F;
    public static double SPEED = 15D*Game.HEIGHT/1080;
    
    Laser(Point2D.Double p,Point2D.Double v,double ang,Ship origin,int dam) {
        super(GameSprites.LASERS[Math.min(dam/2,GameSprites.LASERS.length-1)],p,v,SCALE,ang,0D,0F,origin,dam);
    }
    
    public static Bullet getBullet(Point2D.Double pos, Point2D.Double vel, double angle, Ship origin, int damage) {
        return (Bullet) new Laser(pos,vel,angle,origin,damage);
    }
    
    public static Sprite getSprite(int dam){
        return GameSprites.LASERS[Math.min(dam/2,GameSprites.LASERS.length-1)];
    }
    
    @Override
    public void tick(){
        if(destroyed)
            alive=false;
        super.tick();
    }
}
