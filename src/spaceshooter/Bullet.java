/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter;

import java.awt.geom.Point2D;
import spaceshooter.gfx.Sprite;

/**
 *
 * @author Harikesh
 */
public abstract class Bullet extends SpaceObject {
    
    // size
    public float bWidth,bHeight;
    // is armed
    public boolean armed = false;
    // is destroyed
    public boolean destroyed = false;
    // object that shot it
    public Ship parent;
    
    Bullet(Sprite spr,Point2D.Double p,Point2D.Double v,float s,double ang,double o,float wt,Ship origin,int dam) {
        super(spr,p,v,s,ang,o,wt);
        pos = new Point2D.Double(pos.x-(int)(scale/2F*(float)sprite.width),
                pos.y-(int)(scale/2F*(float)sprite.height));
        parent = origin;
        damage = dam;
        bWidth = sprite.width*scale;
        bHeight= sprite.height*scale;        
    }
    
    public static Bullet getBullet(Point2D.Double pos, Point2D.Double vel, double angle, Ship origin, int damage) {
        return null;
    }
    
    @Override
    public void collided(SpaceObject b) {
        //System.out.println("Bullet collided");
        this.destroyed = true;
    }
    
    @Override
    public void collision(SpaceObject b) {
        if(armed) {
            // bullet is armed
            if(collisionCheck(b)){
                //System.out.println(System.nanoTime()+" Collided with "+b.sprite.path);
                b.collided((SpaceObject)this);
                this.collided(b);
            }
        }
        else {
            if(b==parent) {
                // checking for collision with parent
                if(!collisionCheck(b)) {
                    // if not colliding then arm the bullet
                    armed = true;
                    //System.out.println("Arming");
                }
            }
        }
    }
    
    boolean collisionCheck(SpaceObject b){
        // center of object b
        Point2D.Double bCenter = new Point2D.Double(b.pos.x+b.center.x,b.pos.y+b.center.y);
        // finding minimum distance from each side to the center of the circle
        Point2D.Double a1 = new Point2D.Double(pos.x, pos.y);
        Point2D.Double a2 = new Point2D.Double(pos.x, pos.y);
        a2.y = pos.y+bHeight;
        //System.out.println(b.radius+" : ");
        if(lineSegmentPtDistance(a1,a2,bCenter)<(b.radius)) {
            return true;
        }
        a1.x = a2.x;
        a2.y = a2.y;
        a2.x = pos.x+bWidth;
        if(lineSegmentPtDistance(a1,a2,bCenter)<(b.radius)) {
            return true;
        }
        a1.x = a2.x;
        a2.y = a2.y;
        a2.y = pos.y;
        if(lineSegmentPtDistance(a1,a2,bCenter)<(b.radius)) {
            return true;
        }
        a1.x = a2.x;
        a2.y = a2.y;
        a2.x = pos.x;
        if(lineSegmentPtDistance(a1,a2,bCenter)<(b.radius)) {
            return true;
        }
        return false;
    }
}
