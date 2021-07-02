import java.awt.*;
import java.awt.image.BufferStrategy;

public class Program
{
    private Boolean running;
    private Display display;
    private KeyManager keyManager;
    private MouseManager mouseManager;
    private int width, height;

    private final CodeSim codeSim;

    public Program(CodeSim codeSim)
    {
        this.codeSim = codeSim;
        running = false;
        display = null;
        keyManager = null;
        mouseManager = null;
        width = 0;
        height = 0;
    }

    private void init()
    {
        keyManager = new KeyManager();
        mouseManager = new MouseManager();

        display = new Display();
        width = display.getFrame().getWidth();
        height = display.getFrame().getHeight();
        display.getFrame().addKeyListener(keyManager);
        display.getFrame().addMouseListener(mouseManager);
        display.getFrame().addMouseMotionListener(mouseManager);
        display.getCanvas().addMouseMotionListener(mouseManager);
        display.getCanvas().addMouseListener(mouseManager);
    }

    private void update()
    {
        keyManager.update();
        mouseManager.update();
    }

    private void render()
    {
        BufferStrategy bufferStrategy = display.getCanvas().getBufferStrategy();
        if(bufferStrategy == null)
        {
            display.getCanvas().createBufferStrategy(2);
            return;
        }

        Graphics graphics = bufferStrategy.getDrawGraphics();
        graphics.clearRect(0, 0, width, height);
        graphics.setFont(new Font("Consolas", Font.PLAIN, 10));

        // START DRAW

        graphics.drawOval(mouseManager.getMouseX() - 3, mouseManager.getMouseY() - 3, 6, 6);

        // END DRAW

        bufferStrategy.show();
        graphics.dispose();
    }

    public void run()
    {
        init();

        // this block of code set's up the rendering timing so that it is consistent
        int fps = 30; // the simulation will run at 5 frames per second
        double timePerTick = 1000000000 / (double) fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        long timer = 0;
        running = true;
        while(running)
        {
            now = System.nanoTime();
            delta += (now - lastTime) / timePerTick;
            timer += now - lastTime;
            lastTime = now;
            if(delta >= 1)
            {
                update();
                render();
                delta--;
            }
            if(timer >= 1000000000)
            {
                timer = 0;
            }
        }
    }
}
