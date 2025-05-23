/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter.gfx;

import java.awt.geom.Point2D;

/**
 *
 * @author Harikesh
 */
public class Star {
    Point2D.Double pos = null;
    double angle,omega;
    int spriteNo = 0;
    int frameOffset;
    int frameIncrement;
    
    static Animation[] starAnimations;
    static {
        starAnimations = new Animation[] {
            new Animation("/sprites/stars/star_white",
                    new int[]{1000,1000,2000,2000,2000,3000},true,false
            ),
            new Animation("/sprites/stars/star_white",
                    new int[]{4000,4000,4000,4000,4000,4000},true,false
            ),
            new Animation("/sprites/stars/star_white",
                    new int[]{1000,1000,1000,1000},true,false
            ),
            new Animation(new String[]{
                        "/sprites/stars/star_white_3.png",
                        "/sprites/stars/star_white_4.png",   
                    },new int[]{1000,1000},true,false
            ),
        };
    }
    Star(Point2D.Double p,int n,double a,double o,int fOff,int fInc) {
        pos = p;
        spriteNo = n;
        angle=a;
        omega=o;
        frameOffset = fOff;
        frameIncrement = fInc;
    }
}
