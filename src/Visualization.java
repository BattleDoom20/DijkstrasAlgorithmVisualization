import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Visualization
{
    private Boolean running;
    private Display display;
    private final CodeSim codeSim;
    private final DetailsPanel detailsPanel;
    private KeyManager keyManager;
    private MouseManager mouseManager;
    private int width, height;

    private boolean inputtingPoints, inputtingEdge;

    Dijkstra dijkstra;
    ArrayList<Point> points;
    ArrayList<int[]> edges;
    ArrayList<Integer> selectedPoints;

    public Visualization(CodeSim codeSim, DetailsPanel detailsPanel)
    {
        this.codeSim = codeSim;
        this.detailsPanel = detailsPanel;
        points = new ArrayList<>();
        edges = new ArrayList<>();
        selectedPoints = new ArrayList<>();
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

        inputtingPoints = true;
        inputtingEdge = false;
    }

    private void update()
    {

        if(mouseManager.isLeftPressed())
        {
            if(inputtingPoints)
            {
                points.add(new Point(mouseManager.getMouseX(), mouseManager.getMouseY()));
            }
            else if(inputtingEdge)
            {
                for(int i = 0; i < points.size(); i++)
                {
                    if(mouseManager.getMouseX() >= points.get(i).x - 3 && mouseManager.getMouseX() <= points.get(i).x + 3 && // checks which point is pressed
                       mouseManager.getMouseY() >= points.get(i).y - 3 && mouseManager.getMouseY() <= points.get(i).y + 3 && // checks which point is pressed
                       !selectedPoints.contains(i))  // prevents cycle
                    {
                        boolean flag = true;

                        // prevents edge duplication
                        if(selectedPoints.size() > 0)
                        {
                            for(int[] edge : edges)
                            {
                                int source = edge[0];
                                int destination = edge[1];

                                if((source == selectedPoints.get(0) && destination == i) || (source == i && destination == selectedPoints.get(0)))
                                {
                                    flag = false;
                                    break;
                                }
                            }
                        }

                        if(flag)
                        {
                            selectedPoints.add(i);
                        }
                    }
                }
                if(selectedPoints.size() == 2)
                {
                    Point source = points.get(selectedPoints.get(0));
                    Point destination = points.get(selectedPoints.get(1));
                    int distance = (int) Math.sqrt((source.x - destination.x) * (source.x - destination.x) + (source.y - destination.y) * (source.y - destination.y));
                    int[] edge = new int[]{selectedPoints.get(0), selectedPoints.get(1), distance};
                    edges.add(edge);
                    detailsPanel.addEdgeLabel(edge);
                    selectedPoints = new ArrayList<>();
                }
            }
        }

        if(keyManager.keyUp(KeyEvent.VK_ENTER))
        {
            if(inputtingPoints)
            {
                inputtingPoints = false;
                inputtingEdge = true;
                dijkstra = new Dijkstra(points.size());
            }
            else if(inputtingEdge)
            {
                inputtingEdge = false;
                for(int[] edge : edges)
                {
                    dijkstra.addEdge(edge[0], edge[1], edge[2]);
                }
                //System.out.println(Arrays.deepToString(dijkstra.shortestPath(0)));
            }
        }
        if(keyManager.keyUp(KeyEvent.VK_R))
        {
            inputtingPoints = true;
            inputtingEdge = false;
            points = new ArrayList<>();
            edges = new ArrayList<>();
            selectedPoints = new ArrayList<>();
            detailsPanel.resetPanel();
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
        graphics.setFont(new Font("Consolas", Font.PLAIN, 12));
        graphics.setColor(Color.WHITE);

        // START DRAW
        if(inputtingPoints)
        {
            graphics.setColor(Color.WHITE);
            for(Point point : points)
            {
                graphics.drawRect(point.x - 3, point.y - 3, 6, 6);
            }
        }
        else if(inputtingEdge)
        {
            for(int i = 0 ; i < points.size(); i++)
            {
                graphics.setColor(selectedPoints.contains(i) ? Color.RED : Color.WHITE);
                graphics.fillRect(points.get(i).x - 3, points.get(i).y - 3, 6, 6);
            }

            if(selectedPoints.size() > 0)
            {
                graphics.setColor(Color.RED);
                for(int i : selectedPoints)
                {
                    graphics.drawLine(points.get(i).x, points.get(i).y, mouseManager.getMouseX(), mouseManager.getMouseY());
                }
            }
        }
        else
        {
            graphics.setColor(Color.GREEN);
            for(Point point : points)
            {
                graphics.fillRect(point.x - 3, point.y - 3, 6, 6);
            }
        }

        for(int[] edge : edges)
        {
            graphics.setColor(Color.WHITE);
            Point source = points.get(edge[0]);
            Point destination = points.get(edge[1]);
            graphics.drawLine(source.x, source.y, destination.x, destination.y);

            Point mid = new Point(Math.abs(points.get(edge[0]).x - points.get(edge[1]).x) / 2, Math.abs(points.get(edge[0]).y - points.get(edge[1]).y) / 2);
            Point leftPoint = source.x < destination.x ? source : destination;
            Point topPoint = source.y < destination.y ? source : destination;
            graphics.setColor(Color.BLUE);
            graphics.drawString(String.valueOf(edge[2]), leftPoint.x + mid.x, topPoint.y + mid.y);
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
