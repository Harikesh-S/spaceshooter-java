/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import spaceshooter.Game;

/**
 *
 * @author Harikesh
 */
public class TutorialWindow {
    String title;
    String[] text;
    boolean hasImage;
    boolean alive = true;
    Sprite image;
    int stateTo;
    
    int tutorialTitleSize = (int)(60F*(float)Game.HEIGHT/1080F);
    int tutorialTextSize = (int)(30F*(float)Game.HEIGHT/1080F);
    
    private static final int BOX_WIDTH = (int)(1520F*(float)Game.HEIGHT/1080F),
            BOX_HEIGHT = (int)(680F*(float)Game.HEIGHT/1080F);
    
    //private static final int BOX_X = (int)(200F*(float)Game.HEIGHT/1080F),
    //        BOX_Y = (int)(200F*(float)Game.HEIGHT/1080F);
    
    private static final int BOX_X = (int)(Game.WIDTH/2 - BOX_WIDTH/2),
            BOX_Y = (int)(Game.HEIGHT/2 - BOX_HEIGHT/2);
    
    private static final int BOX_ARC = (int)(20F*(float)Game.HEIGHT/1080F);
    
    private static final int IMAGE_Y_OFFSET = (int)(100F*(float)Game.HEIGHT/1080F),
            TITLE_Y_OFFSET = (int)(80F*(float)Game.HEIGHT/1080F),
            TEXT_Y_OFFSET = (int)(50F*(float)Game.HEIGHT/1080F),
            TEXT_X_OFFSET = (int)(120F*(float)Game.HEIGHT/1080F),
            TEXT_Y_SPACING = (int)(40F*(float)Game.HEIGHT/1080F);
    
    private static final int BUTTON_X = 260,
            BUTTON_Y = 830;
    
    private static final MenuButton button = new MenuButton("OK",BUTTON_X,BUTTON_Y,60,40,new Runnable() {
        @Override
        public void run() {
            
        }
    });
    
    public TutorialWindow(String title,String[] text,int to) {
        this.title = title;
        this.text = text;
        hasImage = false;
        this.stateTo = to;
    }
    
    public TutorialWindow(String title,String[] text,int to,Sprite img) {
        this.title = title;
        this.text = text;
        hasImage = true;
        image = img;
        this.stateTo = to;
    }
    
    
    public void renderGraphics(Graphics g){
//        g.setColor(new Color(0x333333));
//        g.fillRoundRect(BOX_X, BOX_Y, BOX_WIDTH, BOX_HEIGHT, BOX_ARC, BOX_ARC);
            
        g.setColor(Color.WHITE);
        g.drawRoundRect(BOX_X, BOX_Y, BOX_WIDTH, BOX_HEIGHT, BOX_ARC, BOX_ARC);
        
        // draw title
        g.setFont(MenuScreen.titleFont.deriveFont(Font.PLAIN,tutorialTitleSize));
        g.setColor(MenuScreen.textShadowColor);
        g.drawString(title, BOX_X+BOX_WIDTH/2-(title.length()-1)*tutorialTitleSize/2+MenuScreen.shadowOffset,
                BOX_Y+TITLE_Y_OFFSET+MenuScreen.shadowOffset);
        g.setColor(MenuScreen.elementSelectedColor);
        g.drawString(title, BOX_X+BOX_WIDTH/2-(title.length()-1)*tutorialTitleSize/2, BOX_Y+TITLE_Y_OFFSET);
        
        // draw text
        int yBase = BOX_Y+IMAGE_Y_OFFSET+TEXT_Y_OFFSET;
        int xBase = BOX_X+TEXT_X_OFFSET;
        g.setFont(MenuScreen.textFont.deriveFont(Font.PLAIN,tutorialTextSize));
        if(hasImage) {
            yBase+=image.height;
        }
        for(int i=0;i<text.length;i++) {
            g.setColor(MenuScreen.textShadowColor);
            g.drawString(text[i], xBase+MenuScreen.shadowOffset, 
                    yBase+i*TEXT_Y_SPACING+MenuScreen.shadowOffset);
            g.setColor(MenuScreen.textColor);
            g.drawString(text[i], xBase, yBase+i*TEXT_Y_SPACING);
        }
        // draw confirmation button
        button.renderGraphics(g, button.mouseInBounds((int)Game.mousePosition.x,(int)Game.mousePosition.y));
    }
    
    public void render(int[] imagePixels) {
        for(int i=BOX_X;i<BOX_X+BOX_WIDTH;i++)
            for(int j=BOX_Y;j<BOX_Y+BOX_HEIGHT;j++)
                imagePixels[Game.WIDTH*j+i]=0x333333;
        if(hasImage) {
            image.displaySprite(new Point2D.Double(BOX_X+TEXT_X_OFFSET+BOX_WIDTH/4,BOX_Y+IMAGE_Y_OFFSET), 
                    new Point2D.Double(),
                    imagePixels, 1, 0);
        }
    }
    
    public void mouse(){
        if(button.mouseInBounds((int)Game.mousePosition.x,(int)Game.mousePosition.y)) {
            this.alive=false;
            Game.Data.state=stateTo;
            Game.Audio.playSFX(0);
        }
    }
}
