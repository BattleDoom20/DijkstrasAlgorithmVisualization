import java.awt.*;
import java.awt.image.BufferStrategy;

public class Dijkstra implements Runnable
{
    private Boolean running;
    private Display display;
    private BufferStrategy bufferStrategy;
    private Graphics graphics;
    private Thread thread;
    private int width, height;

    public Dijkstra()
    {
    }

    private void init()
    {
        display = new Display();
        width = display.getFrame().getWidth();
        height = display.getFrame().getHeight();
    }

    private void update()
    {
    }

    private void render()
    {
        bufferStrategy = display.getCanvas().getBufferStrategy();
        if(bufferStrategy == null)
        {
            display.getCanvas().createBufferStrategy(2);
            return;
        }

        graphics = bufferStrategy.getDrawGraphics();
        graphics.clearRect(0, 0, width, height);
        graphics.setFont(new Font("Consolas", Font.PLAIN, 10));

        // START DRAW



        // END DRAW

        bufferStrategy.show();
        graphics.dispose();
    }

    @Override
    public void run()
    {
        init();

        // this block of code set's up the rendering timing so that it is consistent
        int fps = 5; // the simulation will run at 5 frames per second
        double timePerTick = 1000000000 / (double) fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        long timer = 0;
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

    public synchronized void start()
    {
        if(running)
        {
            return;
        }
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop()
    {
        if(!running)
        {
            return;
        }
        try
        {
            thread.join();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
