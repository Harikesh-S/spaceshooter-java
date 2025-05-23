/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 *
 * @author Harikesh
 */
public class InputHandler implements KeyListener {
    
    public InputHandler(Game g){
        g.setFocusable(true);
        g.requestFocusInWindow();
        g.addKeyListener(this);
    }
    ArrayList<Integer> inputs = new ArrayList<>();
    
    @Override
    public void keyPressed(KeyEvent e) {
        int code = (int)e.getKeyCode();
        if ( !inputs.contains(code) ) // only add once... prevent duplicates
            inputs.add( code );
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int code = (int)e.getKeyCode();
        inputs.remove((Integer)code );
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        
    }
}
