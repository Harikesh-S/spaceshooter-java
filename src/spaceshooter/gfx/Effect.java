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
public class Effect extends Animation {
    public double effectScale;
    public double effectAngle = 0;
    
    public Effect(String src,int[] fr,boolean inv,boolean die,double scale) {
        super(src,fr,inv,die);
        effectScale = scale;
    }
    
    public Effect(Sprite[] spriteArr,int[] fr,boolean inv,boolean die,double scale) {
        super(spriteArr,fr,inv,die);
        effectScale = scale;
    }
    
    public void tick(){
        super.tick(1);
    }
    
    public void render(int[] imagePixels,Point2D.Double pos,Point2D.Double sPos) {
        render(imagePixels,pos,sPos,(float)effectScale,effectAngle,0);
    }
}
