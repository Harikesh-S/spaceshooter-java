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
public class Animation {
    public Sprite[] sprites;
    public boolean dead = false;
    int[] time;
    int currentFrame;
    int timeToNextFrame;
    int dir = 1;
    boolean invertLoop = false;
    boolean dieAfterFin = false;
    public Point2D.Double offset = new Point2D.Double();
    
    private Animation(int[] fr,boolean inv,boolean die) {
        invertLoop = inv;
        dieAfterFin = die;
        time = new int[fr.length];
        sprites = new Sprite[fr.length];
        timeToNextFrame=0;
        currentFrame = 0;
    }
    public Animation(Sprite[] spriteArr,int[] fr,boolean inv,boolean die) {
        this(fr,inv,die);
        for(int i=0;i<fr.length;i++) {
            sprites[i]= spriteArr[i];
            time[i] = fr[i];
        }
        System.out.println("Copying animation "+this.sprites[0].path);
    }    
    public Animation(String src,int[] fr,boolean inv,boolean die) {
        this(fr,inv,die);
        System.out.println("Loading animation "+src);
        for(int i=0;i<fr.length;i++) {
            sprites[i]= new Sprite(src+"_"+Integer.toString(i)+".png");
            time[i] = fr[i];
        }
    }    
    public Animation(String[] srcs,int[] fr,boolean inv,boolean die) {
        this(fr,inv,die);
        for(int i=0;i<fr.length;i++) {
            sprites[i]= new Sprite(srcs[i]);
            time[i] = fr[i];
        }
    }
    public void reset() {
        dead =false;
        currentFrame =0;
        timeToNextFrame=0;
    }
    public void tick(int timeIncrement) {
        
        timeToNextFrame+=timeIncrement;
    }
    public void render(int[] imagePixels,Point2D.Double pos,Point2D.Double sPos,float scale,double angle,int frameOffset) {
        int frame = (currentFrame+frameOffset)%time.length;
        sprites[frame].displaySprite(pos,sPos,offset,imagePixels,scale,angle);
        
        
        if(timeToNextFrame>=time[frame]) {
            timeToNextFrame=0;
            currentFrame += dir;
            if(dieAfterFin&&currentFrame==time.length)
                dead=true;
            if(invertLoop) {
                if(currentFrame>=time.length) {
                    dir=-1;
                    currentFrame--;
                }
                else if(currentFrame<0) {
                    currentFrame=0;
                    dir=1;
                }
            }
            else {
                currentFrame = currentFrame%time.length;
            }
        }
    }
    public void render(int[] imagePixels,Point2D.Double pos,Point2D.Double sPos,float scale,double angle) {
        render(imagePixels,pos,sPos,scale,angle,0);
    }
}
