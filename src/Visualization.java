/*
Filename: Visualization.java
Author: Hyperrun Academy: Cavite Chapter - FEU TECH
 */

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
    private final KeyManager keyManager;
    private final MouseManager mouseManager;
    private int width, height;

    private boolean playSimulation, endSimulation;
    private boolean inputtingPoints, inputtingEdge;

    private int sourceNode;
    private int simulationTimer;
    private int linePointer;
    private int stepPointer;
    private boolean linePointerChanged;
    private int numRepeatWhileLoop;
    private int numRepeatForLoop;
    private int currentNeighbor;
    private int[] currentShortestNeighbor;
    boolean drawCurEdge, drawCurNode;
    private final int[][]
            lines = new int[][]
            {
                    {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 ,13}, // 0-initialize table
                    {15},                                           // 1-initialize current node
                    {16},                                           // 2-while loop
                    {18},                                           // 3-remove the current from unvisited
                    {19},                                           // 4-for loop
                    {21},                                           // 5-if statement
                    {23, 24, 25, 26, 27},                           // 6-check if lowest (if statement)
                    {30},                                           // 7-add current to visited
                    {31, 32, 33, 34, 35}                            // 8-choose next node
            };

    private Dijkstra dijkstra;
    private ArrayList<Point> points;
    private ArrayList<Color> pointColors;
    private ArrayList<int[]> edges;
    private ArrayList<Integer> selectedPoints;
    private ArrayList<StepData> steps;

    public Visualization(CodeSim codeSim, DetailsPanel detailsPanel, KeyManager keyManager, MouseManager mouseManager)
    {
        this.codeSim = codeSim;
        this.detailsPanel = detailsPanel;
        this.keyManager = keyManager;
        this.mouseManager = mouseManager;
        points = new ArrayList<>();
        pointColors = new ArrayList<>();
        edges = new ArrayList<>();
        selectedPoints = new ArrayList<>();
        running = false;
    }

    private void init()
    {
        // Create display
        display = new Display();
        width = display.getFrame().getWidth();
        height = display.getFrame().getHeight();

        // Add event listeners to the frame and canvas
        display.getFrame().addKeyListener(keyManager);
        display.getCanvas().addMouseMotionListener(mouseManager);
        display.getCanvas().addMouseListener(mouseManager);

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
                    // Add point
                    points.add(new Point(mouseManager.getMouseX(), mouseManager.getMouseY()));
                    pointColors.add(Color.WHITE);
                }
                else if(inputtingEdge)
                {
                    for(int i = 0; i < points.size(); i++)
                    {
                        if(mouseManager.getMouseX() >= points.get(i).x - 4 && mouseManager.getMouseX() <= points.get(i).x + 4 && // checks which point is pressed
                           mouseManager.getMouseY() >= points.get(i).y - 4 && mouseManager.getMouseY() <= points.get(i).y + 4 && // checks which point is pressed
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
                            if(flag) // select point
                            {
                                selectedPoints.add(i);
                            }
                            break;
                        }
                    }

                    // Add edge
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
                else if(!playSimulation && linePointer == 0) // selects starting node
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
            }
            else if(mouseManager.isRightPressed()) // cancel selection
            {
                if(selectedPoints.size() > 0)
                {
                    selectedPoints = new ArrayList<>();
                }
            }

            if(keyManager.keyUp(KeyEvent.VK_ENTER))
            {
                if(inputtingPoints) // proceed to enter edge
                {
                    if(points.size() >= 2)
                    {
                        inputtingPoints = false;
                        inputtingEdge = true;
                        dijkstra = new Dijkstra(points.size());
                    }
                }
                else if(inputtingEdge) // confirm input
                {
                    inputtingEdge = false;
                    for(int[] edge : edges)
                    {
                        dijkstra.addEdge(edge[0], edge[1], edge[2]);
                    }
                    linePointer++;
                    linePointerChanged = true;
                    pointColors.set(sourceNode, Color.BLUE);
                    detailsPanel.setDisableEdit(true);
                }
                else
                {
                    if(!playSimulation) // start or step forward simulation
                    {
                        if(!endSimulation)
                        {
                            if(linePointer == 0)
                            {
                                initSim();
                                pointColors.set(sourceNode, Color.BLUE);
                            }
                            stepForward();
                        }
                        else
                        {
                            initSim();
                        }
                    }
                }
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
            else if(keyManager.keyUp(KeyEvent.VK_Z)) // undo
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
        if(keyManager.keyUp(KeyEvent.VK_SPACE)) // play simulation
        {
            if(!(inputtingPoints || inputtingEdge))
            {
                if(!playSimulation)
                {
                    if(linePointer == 0)
                    {
                        initSim();
                    }
                    playSimulation = true;
                    simulationTimer = 0;
                }
                else
                {
                    playSimulation = false;
                }
            }
        }

        if(simulationTimer > 10) // simulation timing loop
        {
            if(playSimulation)
            {
                stepForward();
            }
            if(endSimulation)
            {
                playSimulation = false;
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
                graphics.drawRect(points.get(i).x - 3, points.get(i).y - 3, 8, 8);
            }
        }
        else if(inputtingEdge)
        {
            for(Point point : points)
            {
                graphics.fillRect(point.x - 3, point.y - 3, 8, 8);
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
            if(!endSimulation && drawCurEdge)
            {
                graphics.setColor(Color.MAGENTA);
                for(int i = 0; i < points.size(); i++)
                {
                    int sourceIndex = steps.get(stepPointer).currentNode;
                    if(i == currentNeighbor && sourceIndex != -1)
                    {
                        source = points.get(sourceIndex);
                        destination = points.get(currentNeighbor);
                        graphics.drawLine(source.x, source.y, destination.x, destination.y);
                    }
                }
            }
        }

        // draw instructions
        graphics.setColor(Color.MAGENTA);
        if(inputtingPoints)
        {
            graphics.drawString("ADDING POINTS", 0, height - 85);
            graphics.drawString("Mouse1 to add a node/point.", 0, height - 75);
            graphics.drawString("Z to undo.", 0, height - 65);
            graphics.drawString("R to reset.", 0, height - 55);
            graphics.drawString("Enter to proceed to adding edges.", 0, height - 45);
        }
        else if(inputtingEdge)
        {
            graphics.drawString("ADDING EDGES", 0, height - 95);
            graphics.drawString("Mouse 1 to select a point/node.", 0, height - 85);
            graphics.drawString("Mouse 2 to cancel selection.", 0, height - 75);
            graphics.drawString("Z to undo.", 0, height - 65);
            graphics.drawString("R to reset.", 0, height - 55);
            graphics.drawString("Enter to confirm.", 0, height - 45);
        }
        else if(endSimulation)
        {
            graphics.drawString("FINISHED", 0, height - 75);
            graphics.drawString("Press ENTER or R to Continue.", 0, height - 65);
            graphics.drawString("SPACE to play again.", 0, height - 55);
            graphics.drawString("ESC to reset.", 0, height - 45);
        }
        else if(playSimulation)
        {
            graphics.drawString("PLAYING", 0, height - 55);
            graphics.drawString("SPACE to pause.", 0, height - 45);
        }
        else
        {
            if(linePointer == 0)
            {
                graphics.drawString("SIMULATION READY", 0, height - 85);
                graphics.drawString("Mouse1 to select starting node.", 0, height - 75);
                graphics.drawString("ENTER to step forward.", 0, height - 65);
                graphics.drawString("SPACE to play.", 0, height - 55);
                graphics.drawString("ESC to reset.", 0, height - 45);
            }
            else
            {
                graphics.drawString("PAUSED", 0, height - 85);
                graphics.drawString("ENTER to step forward.", 0, height - 75);
                graphics.drawString("SPACE to play.", 0, height - 65);
                graphics.drawString("R to restart simulation.", 0, height - 55);
                graphics.drawString("ESC to reset.", 0, height - 45);
            }
        }

        if(!(inputtingPoints || inputtingEdge)) // synchonizes rendering to simulation timing
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

    private void initSim() // resets all data to start the simulation again
    {
        drawCurEdge = true;
        drawCurNode = true;
        steps = dijkstra.shortestPath(sourceNode);
        pointColors.set(steps.get(stepPointer).currentNode, Color.MAGENTA);
        numRepeatWhileLoop = steps.get(0).valuesTable.length;
        numRepeatForLoop = steps.get(stepPointer).neighborsToVisit.length;
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

        for(StepData stepData : steps)
        {
            System.out.println(Arrays.toString(stepData.neighbors));
        }
    }

    private void stepForward()
    {
        drawCurEdge = true;
        drawCurNode = true;

        // directs the linePointer
        // god knows what happens here
        if(linePointer < lines.length)
        {
            if(numRepeatWhileLoop > 0)
            {
                linePointer++;
                linePointerChanged = true;
                if(numRepeatForLoop > 0)
                {
                    if(linePointer == 7 || (linePointer == 6 && !steps.get(stepPointer).neighborsToVisit[steps.get(stepPointer).neighborsToVisit.length - numRepeatForLoop]))
                    {
                        linePointer = 4;
                        numRepeatForLoop--;
                        drawCurEdge = false;
                        currentShortestNeighbor[currentNeighbor] = steps.get(stepPointer).valuesTable[currentNeighbor][1];
                    }
                }
                else if(numRepeatForLoop == 0)
                {
                    if(linePointer == 5 || linePointer == 7 || linePointer == 8)
                    {
                        drawCurEdge = false;
                        if(linePointer == 5)
                        {
                            linePointer = 7;
                        }
                    }
                    else if(linePointer == 9)
                    {
                        linePointer = 2;
                        drawCurEdge = false;
                        if(--numRepeatWhileLoop != 0)
                        {
                            stepPointer++;
                            numRepeatForLoop = steps.get(stepPointer).neighborsToVisit.length;
                        }
                    }
                }
            }
            else
            {
                if(linePointer == 2)
                {
                    drawCurEdge = false;
                    linePointer = 0;
                    endSimulation = true;
                    pointColors.set(steps.get(stepPointer).currentNode, Color.RED);
                    codeSim.resetLines();
                }
            }
            if(linePointer == 4 && numRepeatForLoop != 0)
            {
                currentNeighbor = steps.get(stepPointer).neighbors[steps.get(stepPointer).neighborsToVisit.length - numRepeatForLoop];
            }
            if(linePointer == 3 || linePointer == 4)
            {
                drawCurEdge = false;
            }
        }

        for(int i = 0; i < pointColors.size(); i++)
        {
            pointColors.set(i, Color.WHITE);
        }
        for(int i : steps.get(stepPointer).visitedNodes)
        {
            pointColors.set(i, Color.RED);
        }
        pointColors.set(sourceNode, Color.BLUE);
        if(linePointer > 1 && drawCurNode)
        {
            pointColors.set(steps.get(stepPointer).currentNode, Color.MAGENTA);
        }
    }

    private void reset() // resets everything including inputted points and edges
    {
        drawCurEdge = true;
        drawCurNode = true;
        codeSim.resetLines();
        inputtingPoints = true;
        inputtingEdge = false;
        playSimulation = false;
        simulationTimer = 0;
        linePointer = -1;
        stepPointer = 0;
        points = new ArrayList<>();
        pointColors = new ArrayList<>();
        edges = new ArrayList<>();
        selectedPoints = new ArrayList<>();
        detailsPanel.updateList(edges);
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
