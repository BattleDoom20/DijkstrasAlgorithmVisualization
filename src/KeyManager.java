import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

public class KeyManager implements KeyListener
{

    private final boolean[] keys;

    public KeyManager()
    {
        keys = new boolean[256];
    }

    public void update()
    {
        Arrays.fill(keys, false);
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
        keys[e.getKeyCode()] = true;
    }
}
