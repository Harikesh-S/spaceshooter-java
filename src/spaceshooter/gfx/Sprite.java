/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter.gfx;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import spaceshooter.Game;

/**
 *
 * @author Harikesh
 */
public class Sprite {
    public String path;
    public int width;
    public int height;
    
    public int[] spritePixels;
    
    public Sprite(String p) {
        path = p;
        
        BufferedImage image = null;
        
        try {
            image = ImageIO.read(Sprite.class.getResourceAsStream(path));
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
        
        if(image==null) {
            return;
        }
        
        width=(int) (image.getWidth());
        height=(int) (image.getHeight());
        System.out.println("Loading sprite "+path+" : "+width+"x"+height);
        
        spritePixels = image.getRGB(0, 0, width, height, null, 0, width);
    }
    
    
    public void displaySprite(Point2D.Double pos,Point2D.Double sPos,Point2D.Double off,int[] imagePixels,float scalingFactor,double angle) {
        // scaling
        //int scale = (int) (Game.SCALE*scalingFactor);
        double scale = scalingFactor;
        
        double cos = Math.cos(Math.toRadians(angle)),
                sin = Math.sin(Math.toRadians(angle));
        
        int dispX = (int)(pos.x-sPos.x+off.x), dispY = (int)(pos.y-sPos.y+off.y);
        
        if((dispX+(int)(width*scale)<=0||dispY+(int)(height*scale)<=0)||(pos.x>=sPos.x+Game.WIDTH||pos.y>=sPos.y+Game.HEIGHT)) {
            return;
        }
                
        int i = (int)(dispX +dispY*Game.WIDTH);
                
        for(int j=0;j<spritePixels.length;j++) {
            // transparent = 0
//            if(j%width==0||j/width<1||j/width==height-1||j%width==width-1) {
//                // Display sprite borders for DEBUG
//                double spriteX = j%width - (double)width/2D;
//                double spriteY = j/width - (double)height/2D;
//
//                double newSpriteX =  (cos*spriteX + sin*spriteY + (double)width/2D);
//                double newSpriteY =  (-sin*spriteX + cos*spriteY+ (double)height/2D);
//                int n = i + (int)(((int)(newSpriteY*scale)*Game.WIDTH) + (newSpriteX*scale));
//                try{
//                    imagePixels[n]=0x6611ff;
//                } catch (Exception ex) {}
//                continue;
//            }
            
            if(spritePixels[j]==0) {
                continue;
            }
            // rotating 
            // get x and y of sprite from j
            
            double spriteX = j%width - (double)width/2D;
            double spriteY = j/width - (double)height/2D;
            
            
            double newSpriteX =  (cos*spriteX + sin*spriteY + (double)width/2D);
            double newSpriteY =  (-sin*spriteX + cos*spriteY+ (double)height/2D);
            // prevent wrapping
            if((dispX+(newSpriteX*scale)<1&&dispX<0)||(dispX+(newSpriteX*scale))>=Game.WIDTH-scale) {
                continue;
            }
            
            int n = i + (int)(((int)(newSpriteY*scale)*Game.WIDTH) + (newSpriteX*scale));
            try {
                for(int a=0;a<=Game.SCALE*scale;a++) {
                    for(int b=0;b<=Game.SCALE*scale;b++) {
                        imagePixels[n+b+(a*Game.WIDTH)]=spritePixels[j];
                    }
                }    
            } catch(Exception ex) {   } // current pixel out of screen 
        }
    }
    
    public void displaySprite(Point2D.Double pos,Point2D.Double sPos,int[] imagePixels,float scalingFactor,double angle) {
        displaySprite(pos,sPos,
                new Point2D.Double(),
                imagePixels,scalingFactor,angle);
    }
}
