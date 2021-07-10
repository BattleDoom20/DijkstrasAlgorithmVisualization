/*
Filename: KeyManager.java
Author: Hyperrun Academy: Cavite Chapter - FEU TECH
 */

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

public class KeyManager implements KeyListener
{
    public boolean disable;
    private final boolean[] keys;

    public KeyManager()
    {
        disable = false;
        keys = new boolean[256];
    }

    public void update()
    {
        if(!disable)
        {
            Arrays.fill(keys, false);
        }
    }

    public boolean keyUp(int key)
    {
        return keys[key];
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        try
        {
            keys[e.getKeyCode()] = true;
        }
        catch(ArrayIndexOutOfBoundsException ex)
        {
            System.err.println("Unknown key was pressed.");
        }
    }
}
