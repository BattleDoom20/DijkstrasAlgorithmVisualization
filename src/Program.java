import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Program
{
    private Boolean running;
    private Display display;
    private final CodeSim codeSim;
    private KeyManager keyManager;
    private MouseManager mouseManager;
    private int width, height;

    private boolean confirmed;

    ArrayList<Point> points;

    public Program(CodeSim codeSim)
    {
        this.codeSim = codeSim;
        points = new ArrayList<>();
        running = false;
    }

    private void init()
    {
        keyManager = new KeyManager();
        mouseManager = new MouseManager();

        display = new Display();
        width = display.getFrame().getWidth();
        height = display.getFrame().getHeight();
        display.getFrame().addKeyListener(keyManager);
        display.getCanvas().addMouseMotionListener(mouseManager);
        display.getCanvas().addMouseListener(mouseManager);
    }

    private void update()
    {

        if(!confirmed && mouseManager.isLeftPressed())
        {
            points.add(new Point(mouseManager.getMouseX(), mouseManager.getMouseY()));
        }

        if(keyManager.enter)
        {
            confirmed = true;
        }
        if(keyManager.r)
        {
            confirmed = false;
            points = new ArrayList<>();
        }

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

        graphics.setColor(confirmed ? Color.GREEN : Color.BLACK);
        for(Point point : points)
        {
            graphics.drawOval(point.x - 2, point.y -2, 4, 4);
        }

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
