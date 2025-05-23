/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter.gfx;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import spaceshooter.Game;
import spaceshooter.SpaceObject;

/**
 *
 * @author Harikesh
 */
public abstract class Screen {   
    public Point2D.Double pos = new Point2D.Double();
    public Point2D.Double vel = new Point2D.Double();
    ArrayList<SpaceObject> spaceObjects = new ArrayList<>();
    ArrayList<Star> stars = new ArrayList<>();
    boolean shouldRender;
    boolean starsSpawned;
    Point2D.Double starsSpawnedSector = new Point2D.Double();
    
    public Screen() {
        System.out.println("Creating screen");
        shouldRender = true;
        starsSpawned = false;
    }
    
    public void tick(ArrayList<Integer> inputs) {   
        pos.x+=vel.x;
        pos.y+=vel.y;
        
        for(Star s: stars) {
            s.angle+=s.omega;
            if(s.angle>=360) s.angle=0;
        }
    }
    
    public void render(int[] imagePixels) {
        renderbg(imagePixels);
        renderfg(imagePixels);
    }
    
    abstract public void renderGraphics(Graphics g);
    abstract void renderfg(int[] imagePixels);
    abstract public void mouseClicked(MouseEvent e);
    abstract public void mousePressed(MouseEvent e);
    abstract public void mouseReleased(MouseEvent e);
    abstract public void mouseMoved(MouseEvent e);
    
       
    void renderbg(int[] imagePixels) {
        // black background
        for(int i=0; i<imagePixels.length;i++) imagePixels[i]=0x333333;
        
        // sector changed 
        if(starsSpawnedSector.x!=Math.floor(pos.x/(Game.WIDTH*Game.SCALE))||
                starsSpawnedSector.y!=Math.floor(pos.y/(Game.HEIGHT*Game.SCALE))) {
            stars.clear();
            starsSpawned = false;
        }
        // stars 
        if(!starsSpawned) {
            starsSpawned = true;
            starsSpawnedSector.x=Math.floor(pos.x/(Game.WIDTH*Game.SCALE));
            starsSpawnedSector.y=Math.floor(pos.y/(Game.HEIGHT*Game.SCALE));
            for(int sectorX=(int) starsSpawnedSector.x;sectorX<starsSpawnedSector.x+2;sectorX++)
                for(int sectorY=(int) starsSpawnedSector.y;sectorY<starsSpawnedSector.y+2;sectorY++) {
                    // generate stars for current sector and sector above, below and diag
                    // seed is generated from sector coordinates
                    Random rand = new Random(sectorX+sectorY*1);
                    int numStars = Game.NUM_STARS + rand.nextInt(Game.NUM_STARS);
                    for(int i=0;i<numStars;i++) {
                        int version = rand.nextInt(Star.starAnimations.length);
                        stars.add(new Star(
                                new Point2D.Double(
                                        rand.nextInt(Game.WIDTH*Game.SCALE) +(Game.WIDTH*Game.SCALE)*sectorX,
                                        rand.nextInt(Game.HEIGHT*Game.SCALE)+(Game.HEIGHT*Game.SCALE)*sectorY
                                ),version,
                                rand.nextInt(360),rand.nextDouble()*2-1,
                                rand.nextInt(Star.starAnimations[version].sprites.length),
                                rand.nextInt(2)+1
                        ));
                    }
            }
            //stars.add(new Star(new Point2D.Double(100,100),0));
        }
        
        // draw each star
        for(Star s: stars) {
            Star.starAnimations[s.spriteNo].render(imagePixels, s.pos, pos,(float)Game.HEIGHT/1080F,s.angle,s.frameOffset);
            Star.starAnimations[s.spriteNo].tick(s.frameIncrement);
        }
    }
}
