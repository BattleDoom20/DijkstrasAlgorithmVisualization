import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;

public class Visualization
{
    private Boolean running;
    private Display display;
    private final CodeSim codeSim;
    private final DetailsPanel detailsPanel;
    private KeyManager keyManager;
    private MouseManager mouseManager;
    private int width, height;

    private boolean showInstructions, playSimulation, endSimulation;
    private boolean inputtingPoints, inputtingEdge;

    private int sourceNode;
    private int simulationTimer;
    private int linePointer;
    private int stepPointer;
    private int[][] lines;
    private boolean linePointerChanged;
    private int numRepeatWhileLoop;
    private int numRepeatForLoop;
    private int currentNeighbor;
    private int[] currentShortestNeighbor;

    private Dijkstra dijkstra;
    private ArrayList<Point> points;
    private ArrayList<Color> pointColors;
    private ArrayList<int[]> edges;
    private ArrayList<Integer> selectedPoints;
    private ArrayList<StepData> steps;

    public Visualization(CodeSim codeSim, DetailsPanel detailsPanel)
    {
        this.codeSim = codeSim;
        this.detailsPanel = detailsPanel;
        points = new ArrayList<>();
        pointColors = new ArrayList<>();
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

        lines = new int[][]
                {
                        {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, // 0-initialize table
                        {13},                                   // 1-initialize current node
                        {14},                                   // 2-while loop
                        {16},                                   // 3-for loop
                        {18},                                   // 4-if statement
                        {20},                                   // 5-compute cost
                        {21},                                   // 6-check if lowest (if statement)
                        {23},                                   // 7-set distance
                        {24},                                   // 8-set previous
                        {28},                                   // 9-add current to visited
                        {29}                                    // 10-make current to the next node
                };

        reset();
    }

    private void update()
    {
        if(!playSimulation)
        {
            if(mouseManager.isLeftPressed())
            {
                if(inputtingPoints)
                {
                    points.add(new Point(mouseManager.getMouseX(), mouseManager.getMouseY()));
                    pointColors.add(Color.WHITE);
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
                        new EdgePrompt(display.getFrame(), detailsPanel, edge);
                        edges.add(edge);
                        detailsPanel.updateList(edges);
                        selectedPoints = new ArrayList<>();
                    }
                }
                else if(!playSimulation) // selects starting node
                {
                    for(int i = 0; i < points.size(); i++)
                    {
                        if(mouseManager.getMouseX() >= points.get(i).x - 3 && mouseManager.getMouseX() <= points.get(i).x + 3 && // checks which point is pressed
                           mouseManager.getMouseY() >= points.get(i).y - 3 && mouseManager.getMouseY() <= points.get(i).y + 3)
                        {
                            for(int j = 0; j < pointColors.size(); j++)
                            {
                                pointColors.set(j, Color.WHITE);
                            }
                            sourceNode = i;
                            pointColors.set(sourceNode, Color.BLUE);
                            break;
                        }
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
                    if(points.size() >= 2)
                    {
                        inputtingPoints = false;
                        inputtingEdge = true;
                        dijkstra = new Dijkstra(points.size());
                    }
                }
                else if(inputtingEdge)
                {
                    inputtingEdge = false;
                    for(int[] edge : edges)
                    {
                        dijkstra.addEdge(edge[0], edge[1], edge[2]);
                    }
                    linePointer++;
                    linePointerChanged = true;
                }
                else
                {
                    if(linePointer == 0)
                    {
                        initSim();
                        pointColors.set(sourceNode, Color.BLUE);
                    }
                    stepForward();
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
                    initSim();
                }
            }
            else if(keyManager.keyUp(KeyEvent.VK_Z))
            {
                if(!points.isEmpty() && inputtingPoints)
                {
                    points.remove(points.size() - 1);
                    pointColors.remove(pointColors.size() - 1);
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
                        pointColors.remove(pointColors.size() - 1);
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
        }
        if(keyManager.keyUp(KeyEvent.VK_SPACE))
        {
            if(!(inputtingPoints || inputtingEdge))
            {
                if(!playSimulation)
                {
                    initSim();
                    playSimulation = true;
                    showInstructions = false;
                }
                else
                {
                    playSimulation = false;
                }
            }
        }
        if(endSimulation)
        {
            playSimulation = false;
        }

        if(simulationTimer > 10)
        {
            if(playSimulation)
            {
                stepForward();
            }
            simulationTimer = 0;
        }
        simulationTimer++;
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

        // draw points
        if(inputtingPoints)
        {
            for(int i = 0; i < points.size(); i++)
            {
                graphics.setColor(pointColors.get(i));
                graphics.drawRect(points.get(i).x - 3, points.get(i).y - 3, 6, 6);
            }
        }
        else if(inputtingEdge)
        {
            for(Point point : points)
            {
                graphics.fillRect(point.x - 3, point.y - 3, 6, 6);
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
            for(int i = 0; i < points.size(); i++)
            {
                graphics.setColor(pointColors.get(i));
                graphics.fillRect(points.get(i).x - 3, points.get(i).y - 3, 6, 6);
            }
        }

        // draw node names
        graphics.setColor(Color.RED);
        for(int i = 0; i < points.size(); i++)
        {
            graphics.drawString(String.valueOf((char) (65 + i)), points.get(i).x - 3, points.get(i).y - 4);
        }

        // draw edges
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

        if(steps != null)
        {
            Point source;
            Point destination;

            // draw the current shortest path from start to each node
            graphics.setColor(Color.RED);
            for(int i = 0; i < points.size(); i++)
            {
                int node = i;
                while(node != sourceNode)
                {
                    int nextNode = currentShortestNeighbor[node];
                    if(node == nextNode)
                    {
                        break;
                    }
                    source = points.get(node);
                    destination = points.get(nextNode);
                    node = nextNode;
                    graphics.drawLine(source.x, source.y, destination.x, destination.y);
                }
            }

            // draw the current visiting edge
            if(!endSimulation)
            {
                graphics.setColor(Color.MAGENTA);
                for(int i = 0; i < points.size(); i++)
                {
                    if(i == currentNeighbor)
                    {
                        source = points.get(steps.get(stepPointer).currentNode);
                        destination = points.get(currentNeighbor);
                        graphics.drawLine(source.x, source.y, destination.x, destination.y);
                    }
                }
            }
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
                graphics.drawString("ENTER to step forward.", 0, height - 65);
                graphics.drawString("SPACE to play/pause.", 0, height - 55);
                graphics.drawString("R to restart simulation.", 0, height - 45);
                graphics.drawString("ESC to reset.", 0, height - 35);
            }
        }

        if(!(inputtingPoints || inputtingEdge))
        {
            if(simulationTimer == 10)
            {
                if(linePointerChanged)
                {
                    codeSim.resetLines();
                    codeSim.setLine(lines[linePointer]);
                    linePointerChanged = false;
                }
            }
        }

        // END DRAW

        bufferStrategy.show();
        graphics.dispose();
    }

    private void initSim()
    {
        showInstructions = true;
        steps = dijkstra.shortestPath(sourceNode);
        pointColors.set(steps.get(stepPointer).currentNode, Color.MAGENTA);
        numRepeatWhileLoop = steps.get(0).valuesTable.length;
        numRepeatForLoop = steps.get(stepPointer).neighborCount.length;
        simulationTimer = 0;
        linePointer = 0;
        linePointerChanged = true;
        stepPointer = 0;
        currentNeighbor = sourceNode;
        currentShortestNeighbor = new int[points.size()];
        for(int i = 0; i < pointColors.size(); i++)
        {
            pointColors.set(i, i == sourceNode ? Color.BLUE : Color.WHITE);
        }
        for(int i = 0; i < currentShortestNeighbor.length; i++)
        {
            currentShortestNeighbor[i] = i;
        }
        endSimulation = false;
    }

    private void stepForward()
    {
        for(int i = 0; i < pointColors.size(); i++)
        {
            pointColors.set(i, Color.WHITE);
        }
        for(int i : steps.get(stepPointer).visitedNodes)
        {
            pointColors.set(i, Color.RED);
        }
        pointColors.set(sourceNode, Color.BLUE);
        pointColors.set(steps.get(stepPointer).currentNode, Color.MAGENTA);

        if(linePointer < lines.length - 1)
        {
            linePointer++;
            linePointerChanged = true;

            // directs the linePointer
            if(numRepeatWhileLoop > 0)
            {
                if(numRepeatForLoop > 0)
                {
                    if(linePointer == 5)
                    {
                        if(!steps.get(stepPointer).neighborCount[steps.get(stepPointer).neighborCount.length - numRepeatForLoop])
                        {
                            linePointer = 3;
                            numRepeatForLoop--;
                        }
                    }
                    if(linePointer == 9)
                    {
                        if(--numRepeatForLoop != 0)
                        {
                            linePointer = 3;
                        }
                        currentShortestNeighbor[currentNeighbor] = steps.get(stepPointer).valuesTable[currentNeighbor][1];
                    }
                }
                if(numRepeatForLoop == 0)
                {
                    numRepeatWhileLoop--;
                    if(numRepeatWhileLoop != 0)
                    {
                        linePointer = 2;
                        stepPointer++;
                        numRepeatForLoop = steps.get(stepPointer).neighborCount.length;
                        currentNeighbor = steps.get(stepPointer).currentNode;
                    }
                    if(numRepeatWhileLoop == 0)
                    {
                        linePointer = 9;
                    }
                }
            }
            if(linePointer == 4)
            {
                currentNeighbor = steps.get(stepPointer).neighbors[steps.get(stepPointer).neighborCount.length - numRepeatForLoop];
            }
        }
        else
        {
            showInstructions = true;
            endSimulation = true;
            pointColors.set(steps.get(stepPointer).currentNode, Color.RED);
            codeSim.resetLines();
        }
    }

    private void reset()
    {
        codeSim.resetLines();
        inputtingPoints = true;
        inputtingEdge = false;
        showInstructions = true;
        playSimulation = false;
        simulationTimer = 0;
        linePointer = -1;
        stepPointer = 0;
        points = new ArrayList<>();
        pointColors = new ArrayList<>();
        edges = new ArrayList<>();
        selectedPoints = new ArrayList<>();
        detailsPanel.updateList(edges);
        showInstructions = true;
        sourceNode = 0;
        currentNeighbor = 0;
        currentShortestNeighbor = null;
        steps = null;
        dijkstra = null;
    }

    public void run()
    {
        init();

        // this block of code set's up the timing so that it is consistent
        int fps = 60;
        double timePerTick = 1000000000 / (double) fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        running = true;
        while(running)
        {
            now = System.nanoTime();
            delta += (now - lastTime) / timePerTick;
            lastTime = now;
            if(delta >= 1)
            {
                update();
                render();
                delta--;
            }
        }
    }
}
