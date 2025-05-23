/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter.gfx;

import java.awt.Font;
import java.awt.Graphics;
import spaceshooter.Game;

/**
 *
 * @author Harikesh
 */
public class MenuButton extends MenuElement {
    static int boxPadding = 10*Game.HEIGHT/1080;
    public int clickSFX=0;
    
    public MenuButton(String text,double xOff, double yOff,int w, int h,Runnable act) {
        super(text,xOff,yOff,w,h);
        action = act;
    }
    
    public MenuButton(String text,double xOff, double yOff,int w, int h,Runnable act,int sfx) {
        super(text,xOff,yOff,w,h);
        action = act;
        clickSFX = sfx;
    }
    
    @Override
    public void render(int[] ImagePixels) {
        
    }
    
    @Override
    public void renderGraphics(Graphics g,boolean selected) {
        g.setColor(MenuScreen.elementColor);
        if(selected)
            g.setColor(MenuScreen.elementSelectedColor);
        g.drawRoundRect((int)(posOffset.x-boxPadding),(int)(posOffset.y-MenuScreen.textSize-boxPadding), 
                width+2*boxPadding, height+2*boxPadding,
                boxPadding,boxPadding
        );
        g.setFont(MenuScreen.textFont.deriveFont(Font.ITALIC,MenuScreen.textSize));
        if(selected) {
            g.setColor(MenuScreen.textShadowColor);
            g.drawString(optionText, (int)posOffset.x+MenuScreen.shadowOffset, 
                    (int)(posOffset.y)+MenuScreen.shadowOffset);
            g.setColor(MenuScreen.elementSelectedColor);
        }
        g.drawString(optionText, (int)posOffset.x, (int)(posOffset.y));
    }
    
    @Override
    public boolean mouseInBounds(int x,int y) {
        if(x>posOffset.x-boxPadding&&y>posOffset.y-MenuScreen.textSize-boxPadding
                &&x<posOffset.x+width+boxPadding
                &&y<posOffset.y-MenuScreen.textSize+height+boxPadding)
            return true;
        return false;
    }
    
    @Override
    public void mouseReleased(int x,int y) {
        if(mouseInBounds(x,y)) {
            action.run();
            Game.Audio.playSFX(clickSFX);
        }
    }
}
