/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import spaceshooter.Game;

/**
 *
 * @author Harikesh
 */
public class MenuScreen extends Screen{
    public static Font titleFont, textFont;
    public static float titleSize=60F*(float)(Game.HEIGHT*Game.SCALE)/1080F;
    public static float textSize=40F*(float)(Game.HEIGHT*Game.SCALE)/1080F;
    
    
    public static Color textColor=Color.WHITE;
    public static Color textShadowColor=Color.BLACK;
    public static Color titleColor=Color.WHITE;
    public static Color titleShadowColor=Color.BLACK;
    public static Color elementColor = new Color(0xB8F3FF);
    public static Color elementSelectedColor = new Color(0xFCEC52);
    
    public static int titleOffsetX = (int)(100F*(float)(Game.HEIGHT*Game.SCALE)/1080F),
            titleOffsetY = (int) (20F*(float)(Game.HEIGHT*Game.SCALE)/1080F + titleSize),
            shadowOffset = (int) (4F*(float)(Game.HEIGHT*Game.SCALE)/1080F);
    
    public static int optionsOffsetX = (int)(200F*(float)(Game.HEIGHT*Game.SCALE)/1080F),
            optionsOffsetY=(int)(200F*(float)(Game.HEIGHT*Game.SCALE)/1080F), 
            optionsSpacing = (int)(100F*(float)(Game.HEIGHT*Game.SCALE)/1080F),
            optionsHeight = (int)(80F*(float)(Game.HEIGHT*Game.SCALE)/1080F);
    public static float optionsWidthPerChar = (35F*(float)(Game.HEIGHT*Game.SCALE)/1080F);
    
    public MenuElement[] menuElements = null;
    public String title;
    public int defaultSelectedElement,selectedElement;
    
    public MenuScreen(MenuElement[] elements,String t,int d,boolean move) {
        super();
        System.out.println("Creating Menu Screen "+t);
        defaultSelectedElement = d;
        selectedElement = d;
        title = t;
        menuElements = elements;
        
        if(move) 
            vel.setLocation((int)(2*(float)(Game.HEIGHT*Game.SCALE)/1080F),
                    (int)(2*(float)(Game.HEIGHT*Game.SCALE)/1080F));
        else vel.setLocation(0,0);
        try {
            titleFont = Font.createFont(Font.TRUETYPE_FONT, MenuScreen.class.getResourceAsStream("/fonts/kenvector_future.ttf"));
            textFont = Font.createFont(Font.TRUETYPE_FONT, MenuScreen.class.getResourceAsStream("/fonts/kenvector_future_thin.ttf"));
        } catch (FontFormatException | IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        for(int i=0;i<menuElements.length;i++)
            menuElements[i].mouseReleased(e.getX(),e.getY());
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        for(int i=0;i<menuElements.length;i++)
            if(menuElements[i].mouseInBounds(e.getX(),e.getY())) {
                selectedElement = i;
            }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {    }
    
    @Override
    public void renderGraphics(Graphics g) {
        g.setFont(titleFont.deriveFont(Font.BOLD,titleSize));
        g.setColor(titleShadowColor);
        g.drawString(title,titleOffsetX+shadowOffset,titleOffsetY+shadowOffset);
        g.setColor(titleColor);
        g.drawString(title,titleOffsetX,titleOffsetY);
        for(int i=0;i<menuElements.length;i++) {
            menuElements[i].renderGraphics(g,(i==selectedElement));
        }
    }
    
    @Override
    public void render(int[] imagePixels) {
        renderbg(imagePixels);
        
        
        //GameSprites.MENU_BG.displaySprite(pos, pos, imagePixels, (float)Game.HEIGHT/1080F, 0);
        float scalingFactor = 1.5F*(float)Game.HEIGHT/1080F;
        GameSprites.MENU_BG[0].displaySprite(pos, pos, imagePixels, scalingFactor, 0);
        GameSprites.MENU_BG[0].displaySprite(pos, pos, new Point2D.Double(84*scalingFactor,0),
                imagePixels,scalingFactor , 0);
        GameSprites.MENU_BG[0].displaySprite(pos, pos, new Point2D.Double(2*84*scalingFactor,0),
                imagePixels,scalingFactor , 0);
        GameSprites.MENU_BG[0].displaySprite(pos, pos, new Point2D.Double(3*84*scalingFactor,0),
                imagePixels,scalingFactor , 0);
        GameSprites.MENU_BG[0].displaySprite(pos, pos, new Point2D.Double(4*84*scalingFactor,0),
                imagePixels,scalingFactor , 0);
        
        GameSprites.MENU_BG[1].displaySprite(pos, pos, 
                new Point2D.Double(Game.WIDTH-100*scalingFactor-GameSprites.MENU_BG[1].width
                        ,Game.HEIGHT-GameSprites.MENU_BG[1].height),
                imagePixels,scalingFactor , 0);
        
        renderfg(imagePixels);
    }
    
    @Override
    public void renderfg(int[] imagePixels) {
        for(int i=0;i<menuElements.length;i++) {
            menuElements[i].render(imagePixels);
        }
    }
    
//    private long prevMenuOperation = -50;
//    @Override
//    public void tick(ArrayList<Integer> inputs) { 
//        if(inputs.contains(87)||inputs.contains(38)) {
//            if(Game.tickCount-prevMenuOperation<60) return;
//            prevMenuOperation = Game.tickCount;
//            do {
//                selectedElement--;
//                if(selectedElement<0) selectedElement=menuElements.length-1;
//            } while(menuElements[selectedElement].getClass()==MenuText.class);
//        }
//        else if(inputs.contains(83)||inputs.contains(40)) {
//            if(Game.tickCount-prevMenuOperation<60) return;
//            prevMenuOperation = Game.tickCount;
//            do {
//                selectedElement++;
//                if(selectedElement>=menuElements.length) selectedElement=0;
//            } while(menuElements[selectedElement].getClass()==MenuText.class);
//        }
//        else if(inputs.contains(32)||inputs.contains(13)) {
//            if(Game.tickCount-prevMenuOperation<60) return;
//            prevMenuOperation = Game.tickCount;
//            menuElements[selectedElement].action.run();
//        }
//        super.tick(inputs);
//    }
}
