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

    private boolean showInstructions;
    private boolean inputtingPoints, inputtingEdge;

    private Dijkstra dijkstra;
    private ArrayList<Point> points;
    private ArrayList<int[]> edges;
    private ArrayList<Integer> selectedPoints;

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

        showInstructions = true;
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
                        break;
                    }
                }
                if(selectedPoints.size() == 2)
                {
                    Point source = points.get(selectedPoints.get(0));
                    Point destination = points.get(selectedPoints.get(1));
                    int distance = (int) Math.sqrt((source.x - destination.x) * (source.x - destination.x) + (source.y - destination.y) * (source.y - destination.y));
                    int[] edge = new int[]{selectedPoints.get(0), selectedPoints.get(1), distance};
                    edges.add(edge);
                    detailsPanel.updateList(edges);
                    selectedPoints = new ArrayList<>();
                }
            }
            showInstructions = false;
        }
        else if(mouseManager.isRightPressed())
        {
            if(selectedPoints.size() > 0)
            {
                selectedPoints = new ArrayList<>();
            }
            showInstructions = false;
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
            showInstructions = true;
        }
        else if(keyManager.keyUp(KeyEvent.VK_R))
        {
            if(inputtingPoints || inputtingEdge)
            {
                reset();
            }
            else
            {
                // TODO restart simulation
            }
        }
        else if(keyManager.keyUp(KeyEvent.VK_Z))
        {
            if(!points.isEmpty() && inputtingPoints)
            {
                points.remove(points.size() - 1);
            }
            if(inputtingEdge)
            {
                if(!edges.isEmpty())
                {
                    edges.remove(edges.size() - 1);
                    detailsPanel.updateList(edges);
                }
                else
                {
                    inputtingEdge = false;
                    inputtingPoints = true;
                    points.remove(points.size() - 1);
                }
            }
        }
        else if(keyManager.keyUp(KeyEvent.VK_ESCAPE))
        {
            if(!(inputtingPoints || inputtingEdge))
            {
                reset();
            }
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

        graphics.setColor(Color.RED);
        for(int i = 0; i < points.size(); i++)
        {
            graphics.drawString(String.valueOf((char) (65 + i)), points.get(i).x - 3, points.get(i).y - 4);
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

        // draw instructions
        if(showInstructions)
        {
            graphics.setColor(Color.MAGENTA);
            if(inputtingPoints)
            {
                graphics.drawString("Mouse1 to add a node/point.", 0, height - 65);
                graphics.drawString("Z to undo.", 0, height - 55);
                graphics.drawString("R to reset.", 0, height - 45);
                graphics.drawString("Enter to proceed to adding edges.", 0, height - 35);
            }
            else if(inputtingEdge)
            {
                graphics.drawString("Mouse 1 to select a point/node.", 0, height - 75);
                graphics.drawString("Mouse 2 to cancel selection.", 0, height - 65);
                graphics.drawString("Z to undo.", 0, height - 55);
                graphics.drawString("R to reset.", 0, height - 45);
                graphics.drawString("Enter to confirm.", 0, height - 35);
            }
            else
            {
                graphics.drawString("Right Arrow to step forward.", 0, height - 75);
                graphics.drawString("Left Arrow to step back.", 0, height - 65);
                graphics.drawString("SPACE to play/pause.", 0, height - 55);
                graphics.drawString("R to restart.", 0, height - 45);
                graphics.drawString("ESC to reset.", 0, height - 35);
            }
        }

        // END DRAW

        bufferStrategy.show();
        graphics.dispose();
    }

    private void reset()
    {
        inputtingPoints = true;
        inputtingEdge = false;
        points = new ArrayList<>();
        edges = new ArrayList<>();
        selectedPoints = new ArrayList<>();
        detailsPanel.updateList(edges);
        showInstructions = true;
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
