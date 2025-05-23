/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import spaceshooter.gfx.Effect;
import spaceshooter.gfx.Sprite;

/**
 *
 * @author Harikesh
 */
public class SpaceObject {
    public Point2D.Double pos;
    public Point2D.Float center;
    public Point2D.Double vel;
    public double angle;
    public float scale;
    public double omega;    //angular velocity
    
    public float radius;
    public float mass = 1F;
    public int damage = 1;
    // bypass shields?
    public boolean bypassShields = false;
    
    public boolean alive = true;
    public boolean displaySprite = true;
    public boolean displayEffects = true;
    public boolean displayEffectsFG = true;
    public Sprite sprite = null;
    public ArrayList<Effect> effects = new ArrayList<>();
    public ArrayList<Effect> effectsFG = new ArrayList<>();
    
    double collideAngle = 0;
    long lastCollision = 0;
    private ArrayList<Effect> deadEffects = new ArrayList<>();
    
    public Effect DESTROY_EFFECT;
    
    public boolean deathAnimation = false;
    
    void init(Point2D.Double p,Point2D.Double v,float s,double ang,double o,float wt) {
        
        scale = s/1080F*(float)Game.HEIGHT;
        
        pos=p;
        vel=v;
        angle = ang;
        omega = o;
        
        mass = wt;
        if(sprite.height>sprite.width)
            radius = (float)sprite.height*scale/2F;
        else
            radius = (float)sprite.width*scale/2F;
        center = new Point2D.Float(radius, radius);
    }
    
    SpaceObject(String path,Point2D.Double p,Point2D.Double v,float s,double ang,double o,float wt) {
        System.out.println("Creating SpaceObject with sprite "+path);
        sprite = new Sprite(path);
        init(p,v,s,ang,o,wt);
    }
    
    SpaceObject(Sprite spr,Point2D.Double p,Point2D.Double v,float s,double ang,double o,float wt) {
        //System.out.println("Creating SpaceObject with sprite "+spr.path);
        sprite = spr;
        init(p,v,s,ang,o,wt);
    }
    
    public void tick() {
        pos.x+=vel.x;
        pos.y+=vel.y;
        angle+=omega;
        if(angle>=360) angle-=360;
    }
    
    public void renderEffects(Point2D.Double screenPos,int[] imagePixels) {
        //System.out.println(effects);
        if(!displayEffects) return;
        for(Effect eff:effects) {
            eff.render(imagePixels, pos, screenPos);
            eff.tick();
            //System.out.println("Printing animation : ");
            if(eff.dead==true) {
                deadEffects.add(eff);
            }
        }
        for(Effect eff:deadEffects) {
            effects.remove(eff);
        }
        deadEffects.clear();
    }
    
    public void render(Point2D.Double screenPos,int[] imagePixels) {
        if(!displaySprite) return;
        sprite.displaySprite(pos, screenPos, imagePixels,scale,angle);
    }
    
    public void renderEffectsFG(Point2D.Double screenPos,int[] imagePixels) {
        if(!displayEffectsFG) return;
        for(Effect eff:effectsFG) {
            eff.render(imagePixels, pos, screenPos);
            eff.tick();
            //System.out.println("Printing animation : ");
            if(eff.dead==true) {
                deadEffects.add(eff);
            }
        }
        for(Effect eff:deadEffects) {
            effectsFG.remove(eff);
        }
        deadEffects.clear();
    }
    
    public void collision(SpaceObject b) {
        if(inIframe())
            return;
        Point2D.Double posCenter = (Point2D.Double)pos.clone();
        posCenter.x+=center.x;
        posCenter.y+=center.y;
        if(posCenter.distance(b.pos.x+b.center.x,b.pos.y+b.center.y)<(radius+b.radius)) {
            //collision
            b.collided(this);
            this.collided(b);
            lastCollision = Game.tickCount;
        }
    }
    
    //public void collided(Bullet b) {
    //    System.out.println(sprite.path+" collided with bullet");
    //}
    
    boolean inIframe() {
        return (Game.tickCount-lastCollision<20);
    }
    
    public void collided(SpaceObject b) {
        // get angle 
        double xDiff =  ((pos.x + radius) - (b.pos.x + b.radius));
        double yDiff =  ((pos.y + radius) - (b.pos.y + b.radius));
        collideAngle =  // to get the opposite direction
                + 90 -  Math.toDegrees(Math.atan2(yDiff, xDiff));
        if(collideAngle<0) collideAngle+=360;
        else if(collideAngle>=360) collideAngle-=360;
        
        double magnitude = (5+Math.sqrt(Math.pow(vel.x+b.vel.x,2)+Math.pow(vel.y+b.vel.y, 2)))*(b.mass/(mass+b.mass));
        
        double sin = Math.sin(Math.toRadians(collideAngle)),
                cos= Math.cos(Math.toRadians(collideAngle));
        
        vel.x += sin*magnitude;
        vel.y += cos*magnitude;
        
    }
    
    public void die() {
        if(!deathAnimation) {
            //System.out.println("Death");
            effectsFG.add(DESTROY_EFFECT);
            displayEffects = false;
            deathAnimation = true;
        }
    }
    
    public void drawBounds(Graphics g,Point2D.Double sPos) {
        //g.setColor(Color.yellow);
        //g.drawRect((int)(pos.x-sPos.x), (int)(pos.y-sPos.y), (int)(center.x), (int)(center.y));
        //g.drawOval((int)(pos.x-sPos.x), (int)(pos.y-sPos.y),(int) center.x*2,(int) center.y*2);
        
    }
    
    
    
    double lineSegmentPtDistance(Point2D.Double a, Point2D.Double b,Point2D.Double pt) {
        // line segment a,b
        double l2 = a.distanceSq(b);
        if(l2==0) return a.distance(pt);
        
        
        double t = ((pt.x - a.x) * (b.x - a.x) + (pt.y - a.y) * (b.y - a.y)) / l2;
        t = Math.max(0, Math.min(1, t));
        
        return pt.distance(a.x + t * (b.x - a.x), a.y + t * (b.y - a.y));
        
        
        //System.out.println(a.x+","+a.y+" : "+b.x+","+b.y+" : "+(a.x + t * (b.x - a.x))
        //        +","+(a.y + t * (b.y - a.y))+" : "+pt.x+","+pt.y+" : "+dist);
        
    }
}
