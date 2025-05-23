/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter;

import java.awt.geom.Point2D;
import spaceshooter.gfx.GameSprites;

/**
 *
 * @author Harikesh
 */
public class Boss extends Enemy {
    static float[] BOSS_SCALE = new float[]{
        0.75F,
        0.75F,
    };
    static int[] BOSS_HEALTHS = new int[]{
        10,
        20,
    };
    static float[] BOSS_SHIELDS = new float[]{
        5F,
        5F,
    };
    static float[] BOSS_SPEEDS = new float[]{
        10F,
        10F,
    };
    static float[] BOSS_SHIELD_REGENS = new float[]{
        0.02F,
        0.02F,
    };
    static float[] BOSS_ACCELS = new float[]{
        0.1F,
        0.1F,
    };
    static float[] BOSS_MAX_OMEGAS = new float[]{
        5F,
        5F,
    };
    static float[] BOSS_WEIGHTS = new float[]{
        4F,
        5F,
    };
    static Weapon[][] BOSS_WEAPONS = new Weapon[][]{
        {new Weapon(2,80,2),},
        {new Weapon(3,50,1),},
    };
    static int[] BOSS_EXP_YIELDS = new int[]{
        100,
        200
    };
    static int[][] BOSS_RANGES = new int[][]{
        {200,500,1200},
        {200,500,1200},
    };
    public Boss(int id,Point2D.Double p,Point2D.Double v,double a) {
        super(GameSprites.BOSS[id],p,v,BOSS_SCALE[id],a,0D,
                BOSS_MAX_OMEGAS[id],
                BOSS_HEALTHS[id],BOSS_SHIELDS[id],
                BOSS_SPEEDS[id]*(float)(Game.HEIGHT*Game.SCALE)/1080F,BOSS_SHIELD_REGENS[id],
                BOSS_ACCELS[id]*(float)(Game.HEIGHT*Game.SCALE)/1080F,BOSS_WEIGHTS[id],
                BOSS_WEAPONS[id],
                BOSS_EXP_YIELDS[id],
                BOSS_RANGES[id]
        );
    }
    
    @Override
    void setShieldOffset() {
        SHIELD_EFFECT.effectAngle=collideAngle;
        SHIELD_EFFECT.offset.y = - Math.sin(Math.toRadians((90+collideAngle)%360))*radius/4D*scale;
        SHIELD_EFFECT.offset.x =  Math.cos(Math.toRadians((90+collideAngle)%360))*radius/4D*scale;
    }
}
