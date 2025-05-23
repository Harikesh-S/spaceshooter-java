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
public abstract class MenuElement {
    public Point2D.Double posOffset = new Point2D.Double();
    public int width;
    public int height;
    public String optionText;
    Runnable action;
    public MenuElement(String text,double xOff, double yOff,int w, int h) {
        System.out.println("Creating menu element "+text);
        optionText = text;
        posOffset.x=xOff*(double)Game.HEIGHT/1080D;
        posOffset.y=yOff*(double)Game.HEIGHT/1080D;
        width = (int)((double)w*(double)Game.HEIGHT/1080D);
        height= (int)((double)h*(double)Game.HEIGHT/1080D);
    }
    abstract public void render(int[] imagePixels);
    abstract public void renderGraphics(Graphics g,boolean selected);
    abstract public void mouseReleased(int x,int y);
    abstract public boolean mouseInBounds(int x,int y);
}
