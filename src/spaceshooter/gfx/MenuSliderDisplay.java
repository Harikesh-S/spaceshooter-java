/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter.gfx;

import java.awt.Graphics;

/**
 *
 * @author Harikesh
 */
public class MenuSliderDisplay extends MenuElement {
    
    int widthOffset = 10;
    
    public MenuSliderDisplay(String text,double xOff, double yOff,int w, int h,int wOff) {
        super(text,xOff,yOff,w,h);
        widthOffset = wOff;
    }

    @Override
    public void render(int[] imagePixels) {
        
    }

    @Override
    public void renderGraphics(Graphics g, boolean selected) {
        // draw 10 boxes
        // yoffset has to account for convertion from bottom left coordinate to top left
        // each box is 80x80 centered in 100x100
        int boxY = (int)(posOffset.y)-(height/2);
        g.setColor(MenuScreen.elementColor);
        int level = (int)(10*getLevel());
        for(int i=0;i<10;i++) {
            int boxX = (int)(posOffset.x)+i*(int)(widthOffset*2+width)+widthOffset;
            if(i<level) 
                g.fillRoundRect(boxX, boxY, width, height, 5, 5);
            else
                g.drawRoundRect(boxX, boxY, width, height, 5, 5);
        }
    }
    
    public float getLevel() {
        return 0.5f;    // to be overriden for getting data
    }

    @Override
    public void mouseReleased(int x, int y) {
        // do nothing
    }
    
    @Override
    public boolean mouseInBounds(int x,int y) {
        // do nothing, not interactable
        return false;
    }
}
