import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener
{

    private final boolean[] keys;

    public KeyManager()
    {
        keys = new boolean[256];
    }

    public void update()
    {
        keys[KeyEvent.VK_ENTER] = false;
        keys[KeyEvent.VK_R] = false;
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
