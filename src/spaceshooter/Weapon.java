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
public class Weapon {
    public int weaponType;
    public int reload;
    public int damage;
    
    
    public long reloadTime = 0;
    public double speed =0;
    
    public Weapon(int type,int r,int dam) {
        weaponType = type;
        reload = r;
        damage = dam;
        if(type==1) speed=Laser.SPEED;
        if(type==2) speed=Missile.SPEED;
        if(type==3||type==4) speed=GuidedMissile.SPEED[type-3];
    }
    
    
    public void shoot(Point2D.Double pos,Point2D.Float center, Point2D.Double dest,Ship parent) {
        if(reloadTime>0)
            return;
        reloadTime = reload;
        
        double xDiff =  pos.x+center.x - dest.x;
        double yDiff =  pos.y+center.y - dest.y;
        double shootAngle = 180 - Math.toDegrees(Math.atan2(yDiff, xDiff));
        //if(shootAngle<0) shootAngle+=360;
        
        if(weaponType==1) { // laser
            if(parent.getClass()==Player.class)
                Game.Audio.playSFX(2);
            Game.Data.bullets.add(Laser.getBullet(
                    new Point2D.Double(pos.x+center.x,pos.y+(int)center.y),
                    new Point2D.Double(speed*Math.cos(Math.toRadians(shootAngle)),
                            -speed*Math.sin(Math.toRadians(shootAngle))),
                    shootAngle-90,parent,damage
            ));
        }
        else if(weaponType==2) {    // missile
            if(parent.getClass()==Player.class)
                Game.Audio.playSFX(6);
            Game.Data.bullets.add(Missile.getBullet(
                    new Point2D.Double(pos.x+center.x,pos.y+(int)center.y),
                    new Point2D.Double(speed*Math.cos(Math.toRadians(shootAngle)),
                            -speed*Math.sin(Math.toRadians(shootAngle))),
                    shootAngle-90,parent,damage
            ));
        }
        else if(weaponType==3) {    // guided missile - enemy
            if(parent.getClass()==Player.class)
                Game.Audio.playSFX(6);
            Game.Data.bullets.add(GuidedMissile.getBullet(
                    new Point2D.Double(pos.x+center.x,pos.y+(int)center.y),
                    new Point2D.Double(speed*Math.cos(Math.toRadians(shootAngle)),
                            -speed*Math.sin(Math.toRadians(shootAngle))),
                    shootAngle-90,parent,damage
            ));
        }
        else if(weaponType==4) {    // guided missile - target at closes enemy
            if(parent.getClass()==Player.class)
                Game.Audio.playSFX(6);
            Ship closestEnemy = null;
            for(int i=0;i<Game.Data.enemies.size();i++) {
                if(closestEnemy==null) {
                    closestEnemy=Game.Data.enemies.get(i);
                    continue;
                }
                if(Game.Data.player.pos.distance(closestEnemy.pos)>
                        Game.Data.player.pos.distance(Game.Data.enemies.get(i).pos))
                    closestEnemy=Game.Data.enemies.get(i);
            }
            Game.Data.bullets.add(GuidedMissile.getBullet(
                    new Point2D.Double(pos.x+center.x,pos.y+(int)center.y),
                    new Point2D.Double(speed*Math.cos(Math.toRadians(shootAngle)),
                            -speed*Math.sin(Math.toRadians(shootAngle))),
                    shootAngle-90,parent,damage,1,closestEnemy
            ));
        }
        else {
            System.err.println("Invalid weapon type "+weaponType);
            System.exit(0);
        }
    }
    
    public void tick() {
        if(reloadTime>0)    reloadTime--;
    }
    
    public Sprite getSprite() {
        Sprite ret = null;
        if(weaponType==1) {
            ret = Laser.getSprite(damage);
        }
        else if(weaponType==2) {
            ret = Missile.getSprite(damage);
        }
        else if(weaponType==3||weaponType==4) {
            ret = GuidedMissile.getSprite(damage);
        }
        else {
            System.err.println("Invalid weapon type");
            System.exit(0);
        }
        return ret;
    }
    
    public float findHalfWidth() {
        if(weaponType==1){
            return (float)Laser.SCALE*getSprite().width/2.0F;
        }
        if(weaponType==2){
            return (float)Missile.SCALE*getSprite().width/2.0F;
        }
        return 0F;
    }
}
