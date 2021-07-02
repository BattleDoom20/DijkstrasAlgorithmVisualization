import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener
{

    private final boolean[] keys;
    public boolean enter, r;

    public KeyManager()
    {
        keys = new boolean[256];
    }

    public void update()
    {
        enter = keys[KeyEvent.VK_ENTER];
        r = keys[KeyEvent.VK_R];
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        keys[e.getKeyCode()] = false;
    }
}
