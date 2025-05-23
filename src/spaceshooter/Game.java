/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import spaceshooter.gfx.GameScreen;
import spaceshooter.gfx.GameSprites;
import spaceshooter.gfx.MenuButton;
import spaceshooter.gfx.MenuElement;
import spaceshooter.gfx.MenuScreen;
import spaceshooter.gfx.MenuSliderDisplay;
import spaceshooter.gfx.MenuSpriteButton;
import spaceshooter.gfx.MenuText;
import spaceshooter.gfx.Screen;

/**
 *
 * @author Harikesh
 */
public class Game extends Canvas implements Runnable,MouseListener,MouseMotionListener {
    public static final int HEIGHT = 720;
    public static final int WIDTH = HEIGHT*16/9;
    public static final int SCALE = 1;  // implementation incomplete, menu speed etc needs to scale with this
    public static final int NUM_STARS = WIDTH*SCALE/64;
    
    private boolean showTitleBar = true;
    public static byte targetUPS = 60;
    
    public static Point2D.Double mousePosition = new Point2D.Double();
    public static int jumpToScreen = -1;
    public static boolean newGame = true;
    
    public static boolean isRunning = false;
    public static long tickCount = 0;
    
    public static GameData Data = new GameData();
    public static GameSettings Settings = new GameSettings();
    public static GameAudio Audio = new GameAudio();
    
    public InputHandler input = new InputHandler(this);
    
    private JFrame frame;
    private BufferedImage image = new BufferedImage(WIDTH,HEIGHT,
            BufferedImage.TYPE_INT_RGB
    );
    
    private int[] imagePixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();    
    private Screen[] screens;
    private int currentScreen;
    private String FPSIndicator = "FPS:60",UPSIndicator = "UPS:60";
    
    
    public Game() {
        System.out.println("Creating JFrame");
        // create JFrame
        setMinimumSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
        setMaximumSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
        setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
        
        frame = new JFrame();
        
        frame.setTitle("Space Shooter Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        frame.add(this, BorderLayout.CENTER);
        if(!showTitleBar) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
        }
        frame.pack();
        
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        System.out.println("Creating Screens");
        screens = new Screen[]{
            new MenuScreen(new MenuElement[]{
                new MenuButton("New Game",200,200,250,60,new Runnable() {
                    @Override
                    public void run() {
                        Game.newGame = true;
                        Game.jumpToScreen = 2;
                    }
                }),
                new MenuButton("Options",200,300,250,60,new Runnable() {
                    @Override
                    public void run() {
                        Game.jumpToScreen = 4;
                    }
                }),
                new MenuButton("Controls",200,400,250,60,new Runnable() {
                    @Override
                    public void run() {
                        Game.jumpToScreen = 6;
                    }
                }),
                new MenuButton("Credits",200,500,250,60,new Runnable() {
                    @Override
                    public void run() {
                        Game.jumpToScreen = 5;
                    }
                }),
                new MenuButton("Exit",200,600,150,60,new Runnable() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }),
                new MenuSpriteButton(GameSprites.PLAYER[Game.Settings.playerNumber][Game.Settings.playerColor],1F,
                        1800,950,new Runnable() {
                    @Override
                    public void run() {
                        Game.Settings.playerColor++;
                        if(Game.Settings.playerColor>3) {
                            Game.Settings.playerColor=0;
                            Game.Settings.playerNumber++;
                        }
                        if(Game.Settings.playerNumber>2) {
                            Game.Settings.playerNumber=0;
                        }
                    }
                }) {
                    @Override
                    public void init() {
                        this.height = (int)(this.width*0.8);
                    }
                    @Override
                    public void render(int[] imagePixels) {
                        setModel();
                        super.render(imagePixels);
                    }                    
                }
            },"Main Menu",0,true),
            new MenuScreen(new MenuElement[]{
                new MenuButton("Resume",200,200,250,60,new Runnable() {
                    @Override
                    public void run() {
                        Game.newGame = false;
                        Game.jumpToScreen = 2;
                    }
                }),
                new MenuButton("Return to main menu",200,300,500,60,new Runnable() {
                    @Override
                    public void run() {
                        Game.jumpToScreen = 0;
                    }
                },1),
            },"Pause",0,false),            
            new GameScreen(),
            new MenuScreen(new MenuElement[]{
                new MenuButton("Return to main menu",200,200,500,60,new Runnable() {
                    @Override
                    public void run() {
                        Game.jumpToScreen = 0;
                        screens[2]=new GameScreen();
                    }
                },1),
            },"You Died",0,false),      
            new MenuScreen(new MenuElement[]{
                new MenuButton("Return to main menu",200,200,500,60,new Runnable() {
                    @Override
                    public void run() {
                        Game.jumpToScreen = 0;
                    }
                },1),
                new MenuText(new String[]{
                        "Audio Effects",
                    },200,300,0,0
                ),
                new MenuText(new String[]{
                        "Background Audio",
                    },200,400,0,0
                ),
                new MenuText(new String[]{
                        "Locked Screen",
                    },200,500,0,0
                ),
                new MenuText(new String[]{
                        "Show FPS",
                    },200,600,0,0
                ),
                // FG Audio buttons
                new MenuButton("-",700,300,40,50,new Runnable() {
                    @Override
                    public void run() {
                        int level = (int)(Game.Settings.fgAudioLevel*10);
                        level--;
                        if(level<0) level=0;
                        if(level>10)level=10;
                        Game.Settings.fgAudioLevel=(float)level/10f;
                    }
                }),
                new MenuButton("+",1350,300,40,50,new Runnable() {
                    @Override
                    public void run() {
                        int level = (int)(Game.Settings.fgAudioLevel*10);
                        level++;
                        if(level<0) level=0;
                        if(level>10)level=10;
                        Game.Settings.fgAudioLevel=(float)level/10f;
                    }
                }),
                // FG Audio level
                new MenuSliderDisplay("",780,285,40,30,5) {
                    @Override
                    public float getLevel() {
                        return Game.Settings.fgAudioLevel;
                    }
                },
                // BG Audio buttons
                new MenuButton("-",700,400,40,50,new Runnable() {
                    @Override
                    public void run() {
                        int level = (int)(Game.Settings.bgAudioLevel*10);
                        level--;
                        if(level<0) level=0;
                        if(level>10)level=10;
                        Game.Settings.bgAudioLevel=(float)level/10f;
                    }
                }),
                new MenuButton("+",1350,400,40,50,new Runnable() {
                    @Override
                    public void run() {
                        int level = (int)(Game.Settings.bgAudioLevel*10);
                        level++;
                        if(level<0) level=0;
                        if(level>10)level=10;
                        Game.Settings.bgAudioLevel=(float)level/10f;
                    }
                }),
                // BG Audio level
                new MenuSliderDisplay("",780,385,40,30,5) {
                    @Override
                    public float getLevel() {
                        return Game.Settings.bgAudioLevel;
                    }
                },
                // Locked Screen button
                new MenuButton(((Game.Settings.lockScreen)?"*":" "),700,500,40,50,new Runnable() {
                    @Override
                    public void run() {
                        Game.Settings.lockScreen = !Game.Settings.lockScreen;
                    }
                }){
                    @Override
                    public void renderGraphics(Graphics g,boolean selected){
                        this.optionText = ((Game.Settings.lockScreen)?"*":" ");
                        super.renderGraphics(g,selected);
                    }
                },
                // FPS Toggle button
                new MenuButton(((Game.Settings.showFPS)?"*":" "),700,600,40,50,new Runnable() {
                    @Override
                    public void run() {
                        Game.Settings.showFPS = !Game.Settings.showFPS;
                    }
                }){
                    @Override
                    public void renderGraphics(Graphics g,boolean selected){
                        this.optionText = ((Game.Settings.showFPS)?"*":" ");
                        super.renderGraphics(g,selected);
                    }
                },
            },"Options",0,true),       
            new MenuScreen(new MenuElement[]{
                new MenuText(new String[]{
                        "Harikesh Subramanian","",
                        "Resources : ",
                        "           www.kenney.nl",
                        "           opengameart.org"
                    },200,300,0,0
                ),
                new MenuButton("Return to main menu",200,900,500,60,new Runnable() {
                    @Override
                    public void run() {
                        Game.jumpToScreen = 0;
                    }
                },1),
            },"Credits",1,true),
            new MenuScreen(new MenuElement[]{
                new MenuText(new String[]{
                        "Move using WASD or arrow keys","",
                        "Aim using mouse","",
                        "SPACE or CLICK TO SHOOT","",
                        "Hold Q to toggle locked camera","",
                        "E to switch special","",
                        "SHIFT or right click to shoot special"
                    },200,300,0,0
                ),
                new MenuButton("Return to main menu",200,900,500,60,new Runnable() {
                    @Override
                    public void run() {
                        Game.jumpToScreen = 0;
                    }
                },1),
            },"Controls",1,true),
            new MenuScreen(new MenuElement[]{
                new MenuText(new String[]{
                        "Time : ",
                    },200,200,0,0
                ){
                    @Override
                    public void renderGraphics(Graphics g,boolean selected) {
                        this.textContent[0] = "Time :   "+(Game.Data.ticks/Game.targetUPS)+" s";
                        super.renderGraphics(g,selected);
                    }
                },
                new MenuButton("Return to main menu",200,400,500,60,new Runnable() {
                    @Override
                    public void run() {
                        Game.jumpToScreen = 0;
                        screens[2]=new GameScreen();
                    }
                },1),
            },"The End",0,false)
        };
        currentScreen=0;
        System.out.println("Adding mouse listeners");
        addMouseListener(this);
        addMouseMotionListener(this);
        System.out.println("Setting cursor");
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
            new ImageIcon(Game.class.getResource("/cursor.png")).getImage(),
            new Point(4,4),"custom cursor"));
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        mousePosition.x=e.getX();
        mousePosition.y=e.getY();
        screens[currentScreen].mousePressed(e);    
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePosition.x=e.getX();
        mousePosition.y=e.getY();
        screens[currentScreen].mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {    }

    @Override
    public void mouseExited(MouseEvent e) {    }
    
    @Override
    public void mouseClicked(MouseEvent e) {    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition.x=e.getX();
        mousePosition.y=e.getY();
        screens[currentScreen].mouseMoved(e);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        mousePosition.x=e.getX();
        mousePosition.y=e.getY();
        screens[currentScreen].mouseMoved(e);
    }
    
    @Override
    public void run() {
        // timing
        long lastNanoTime = System.nanoTime();
        double nsPerTick = 1000000000.0 / (double)targetUPS;
        
        int ticks = 0;
        int frames = 0;
        
        long lastTimer = System.currentTimeMillis();
        double delta =  0;
        
        
        while(isRunning) {
            // if the game is running calculate delta
            long currentNanoTime = System.nanoTime();
            delta += (currentNanoTime-lastNanoTime) / nsPerTick;
            lastNanoTime=currentNanoTime;
            
            boolean shouldRender = false;
            
            while(delta>=1) {
                // if delta then update the game
                ticks++;
                tick();
                delta--;    
                shouldRender = true;
            }
            
            if(shouldRender) {
                // if the game updated then render the update
                frames++;
                render();
            }
            
            if(System.currentTimeMillis()-lastTimer>1000) {
                // printing ups and fps
                lastTimer+=1000;
                UPSIndicator = "UPS:"+ticks;
                FPSIndicator = "FPS:"+frames;
                //System.out.println("Ticks : "+ticks+", Frames : "+frames);
                frames=0;
                ticks=0;
            }
        }
    }
    
    public void tick() {
        // BG
        Audio.bgAudioUpdate();
        // Screen transitions
        if(jumpToScreen!=-1) {
            if(jumpToScreen==2) { // Game screen 
                if(newGame) {
                    Data = new GameData();
                    screens[2]=new GameScreen();
                }
            }
            if(!screens[jumpToScreen].getClass().equals(GameScreen.class)) {
                screens[jumpToScreen].pos.setLocation(screens[currentScreen].pos);
            }
            currentScreen = jumpToScreen;
            jumpToScreen=-1;
        }
        tickCount++;
        screens[currentScreen].tick(input.inputs);
    }
    
    public void render() {
        BufferStrategy bufferStrat = getBufferStrategy();
        if(bufferStrat==null) {
            createBufferStrategy(2);    // number of vsync buffers
            return;
        }
        Graphics g = bufferStrat.getDrawGraphics();
        
        screens[currentScreen].render(imagePixels);
        
        g.drawImage(image,0,0,getWidth(),getHeight(),null);
        
        screens[currentScreen].renderGraphics(g);
        
        if(Settings.showFPS) {
            g.setColor(MenuScreen.textColor);
            g.setFont(MenuScreen.textFont.deriveFont(Font.ITALIC,MenuScreen.textSize));
            g.drawString(UPSIndicator, 300, (int)(HEIGHT-MenuScreen.textSize));
            g.drawString(FPSIndicator, 300, HEIGHT);
        }
        
        g.dispose();
        bufferStrat.show();
    }
    
    public static void main(String[] args) {
        System.out.println("Creating Game");
        Game game = new Game();
        game.start();
    }
    
    public synchronized  void start() {
        System.out.println("Starting Game thread");
        isRunning = true;
        new Thread(this).start();
    }
    
    public synchronized  void stop() {
        System.out.println("Stopping Game thread");
        isRunning = false;
    }
}