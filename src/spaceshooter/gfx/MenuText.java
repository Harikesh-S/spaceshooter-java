/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter.gfx;

import java.awt.Font;
import java.awt.Graphics;

/**
 *
 * @author Harikesh
 */
public class MenuText extends MenuElement {
    public String[] textContent;
    
    public MenuText(String[] text,double xOff, double yOff,int w, int h) {
        super("",xOff,yOff,w,h);
        textContent = text;
    }
    
    @Override
    public void render(int[] imagePixels) {    }
    
    @Override
    public void renderGraphics(Graphics g,boolean selected) {
        g.setFont(MenuScreen.textFont.deriveFont(Font.ITALIC,MenuScreen.textSize));
        g.setColor(MenuScreen.textShadowColor);
        for(int i=0;i<textContent.length;i++)
            g.drawString(textContent[i], (int)posOffset.x+MenuScreen.shadowOffset, 
                    (int)(posOffset.y+(MenuScreen.textSize+5)*i)+MenuScreen.shadowOffset);
        g.setColor(MenuScreen.textColor);
        for(int i=0;i<textContent.length;i++)
            g.drawString(textContent[i], (int)posOffset.x, 
                    (int)(posOffset.y+(MenuScreen.textSize+5)*i));
    }
    
    @Override
    public boolean mouseInBounds(int x,int y) {
        return false;
    }
    
    @Override
    public void mouseReleased(int x,int y) {    }
}
