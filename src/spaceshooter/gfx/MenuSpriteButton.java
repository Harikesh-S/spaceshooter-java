/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter.gfx;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import spaceshooter.Game;

/**
 *
 * @author Harikesh
 */
public class MenuSpriteButton extends MenuElement{
    static int boxPadding = 10*Game.HEIGHT/1080;
    
    float scale;
    Sprite sprite;
    
    public MenuSpriteButton(Sprite spr,float sc,double xOff, double yOff,Runnable act) {
        super("",xOff,yOff,(int)(spr.width*sc),(int)(spr.height*sc));
        scale = sc*((float)(Game.HEIGHT)/1080F);
        sprite = spr;
        action = act;
        init();
    }
    
    public void init() {
        // used to override and change values
    }
    
    @Override
    public void render(int[] ImagePixels) {
        sprite.displaySprite(new Point2D.Double((int)posOffset.x,(int)posOffset.y), 
                new Point2D.Double(), ImagePixels, scale, 0D);
    }
    
    @Override
    public void renderGraphics(Graphics g,boolean selected) {
        g.setColor(MenuScreen.elementColor);
        if(selected)
            g.setColor(MenuScreen.elementSelectedColor);
        g.drawRoundRect((int)(posOffset.x-boxPadding),(int)(posOffset.y-boxPadding), 
                width+2*boxPadding, height+2*boxPadding,
                boxPadding,boxPadding
        );
    }
    
    @Override
    public boolean mouseInBounds(int x,int y) {
        if(x>posOffset.x-boxPadding&&y>posOffset.y-boxPadding
                &&x<posOffset.x+width-boxPadding
                &&y<posOffset.y+height-boxPadding)
            return true;
        return false;
    }
    
    @Override
    public void mouseReleased(int x,int y) {
        if(mouseInBounds(x,y))
            action.run();
    }
    
    public void setModel() {
        //System.out.println("Updating player sprite");
        sprite = GameSprites.PLAYER[Game.Settings.playerNumber][Game.Settings.playerColor];
    }
}
