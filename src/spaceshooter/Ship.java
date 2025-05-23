/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter;

import java.awt.geom.Point2D;
import static spaceshooter.Player.PLAYER_OMEGA;
import spaceshooter.gfx.Effect;
import spaceshooter.gfx.GameSprites;
import spaceshooter.gfx.Sprite;

/**
 *
 * @author Harikesh
 */
public class Ship extends SpaceObject {
    // ship info
    public int health,maxHealth;
    public float shield,maxShield;
    public float speed;
    public float acceleration;
    public float shieldRegen;
    public float maxOmega;
    // weapons
    public Weapon[] weapons = null;
    public int currentWeapon = 0;
    // status effects
    public boolean isIoned = false;
    // denotes what the required vel and angle are and 
    // the ship attempts to reach it as fast as it can
    Point2D.Double reqdVel = new Point2D.Double();
    double reqdAngle = 0;
    // shield effect
    public Effect SHIELD_EFFECT;
    
    public Ship(String path,Point2D.Double p,Point2D.Double v,
            float sc,double a,double o,float maxO,
            int h,float sh,float sp,float reg,float accel,float wt,
            Weapon[] ws) {
        super(path,p,v,sc,a,o,wt);
        init(v,a,sp,accel,maxO,h,sh,reg,ws);
    }
    
    public Ship(Sprite spr,Point2D.Double p,Point2D.Double v,
            float sc,double a,double o,float maxO,
            int h,float sh,float sp,float reg,float accel,float wt,
            Weapon[] ws) {
        super(spr,p,v,sc,a,o,wt);
        init(v,a,sp,accel,maxO,h,sh,reg,ws);
    }
    
    void init(Point2D.Double v,double a,float sp,float accel,float maxO,int h,float sh,float reg,
            Weapon[] ws) {
        reqdVel.x=v.x;
        reqdVel.y=v.y;
        reqdAngle = a;
        
        speed = sp;
        acceleration = accel;
        maxOmega = (float)maxO;
        
        health = h;
        maxHealth = h;
        shield = sh;
        maxShield = sh;
        shieldRegen = reg;
        
        weapons = new Weapon[ws.length];
        for(int i=0;i<ws.length;i++) {
            weapons[i] = new Weapon(ws[i].weaponType,ws[i].reload,ws[i].damage);
        }
        
        SHIELD_EFFECT = new Effect(GameSprites.SHIELD_ANIMATION_SPRITES,
                new int[]{5,5,5},
                false,true,(double)scale*(double)sprite.height/100D);
        DESTROY_EFFECT = new Effect(GameSprites.SMOKE_ANIMATION_SPRITES,
                new int[]{2,2,2,2,2,2,2,2,2},
                false,true,(double)scale*(double)sprite.height/50D);
    }
    @Override
    public void tick() {
        
        if(health<=0) {
            die();
            if(!effectsFG.contains(DESTROY_EFFECT))
                alive=false;
        }
                
        // shield and health code
        if(shield<maxShield)
            shield+=shieldRegen;
        else
            shield=maxShield;
        
        // angle
        double diff = reqdAngle-angle;
        if(diff<-180) diff+=360;
        if(diff>+180) diff-=360;
        if(Math.abs(diff)<=PLAYER_OMEGA) {
            omega = 0;
            angle = reqdAngle;
        }
        else {
            if(diff>0) omega = + PLAYER_OMEGA;
            else omega = - PLAYER_OMEGA;
        }
        // movement
        if(vel.x<(int)speed*reqdVel.x) {
            if(vel.x+acceleration<(int)speed*reqdVel.x)
                vel.x+=acceleration;
            else
                vel.x=(int)speed*reqdVel.x;
        }
        if(vel.x>(int)speed*reqdVel.x) {
            if(vel.x-acceleration>(int)speed*reqdVel.x)
                vel.x-=acceleration;
            else
                vel.x=(int)speed*reqdVel.x;
        }
        if(vel.y<(int)speed*reqdVel.y) {
            if(vel.y+acceleration<(int)speed*reqdVel.y)
                vel.y+=acceleration;
            else
                vel.y=(int)speed*reqdVel.y;
        }
        if(vel.y>(int)speed*reqdVel.y) {
            if(vel.y-acceleration>(int)speed*reqdVel.y)
                vel.y-=acceleration;
            else
                vel.y=(int)speed*reqdVel.y;
        }
        //System.out.println("req : "+(int)speed*reqdVel.x+","+(int)speed*reqdVel.y+", current : "+vel.x+" "+vel.y);
        super.tick();
    }
    
    public void shoot(Point2D.Double dest,int weapon) {
        weapons[weapon].shoot(pos,center,dest,this);
    }
    
    public void die() {
        if(!deathAnimation) {
            //System.out.println("Death");
            effectsFG.add(DESTROY_EFFECT);
            displayEffects = false;
            deathAnimation = true;
        }
    }
    
    
    @Override
    public void collided(SpaceObject b){
        super.collided(b);
        if(!b.bypassShields) {
            // shield gating
            if(shield<1) {
                shield=0;
                // damage to health
                health-=b.damage;
                if(this.getClass()==Player.class)
                    Game.Audio.playSFX(7);
                //System.out.println("Health damage");
            }
            else {
                // damage to shield
                // sfx for player
                if(this.getClass()==Player.class)
                    Game.Audio.playSFX(3);
                shield=(int)shield;
                shield-=b.damage;
                if(shield<0) shield=0;
                //System.out.println("Sheild damage");
                // reset shield 
                SHIELD_EFFECT.reset();
                // set angle
                setShieldOffset();
                // add to effects
                if(!effects.contains(SHIELD_EFFECT))
                    effects.add(SHIELD_EFFECT);
            }
        }
        else {
            health-=b.damage;
            if(this.getClass()==Player.class)
                Game.Audio.playSFX(7);
        }
        if(health<0) health=0;
    }
    
    void setShieldOffset() {
        SHIELD_EFFECT.effectAngle=collideAngle;
        SHIELD_EFFECT.offset.y = - Math.sin(Math.toRadians((90+collideAngle)%360))*radius/2D*scale;
        SHIELD_EFFECT.offset.x =  Math.cos(Math.toRadians((90+collideAngle)%360))*radius/2D*scale;
    }
}
