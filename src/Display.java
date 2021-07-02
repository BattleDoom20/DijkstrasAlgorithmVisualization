import javax.swing.*;
import java.awt.*;

public class Display
{
    public final int WIDTH = 500, HEIGHT = 500;

    private final JFrame frame;
    private final Canvas canvas;

    public Display()
    {
        // FRAME START
        frame = new JFrame("Dijkstra's Algorithm Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Canvas
        canvas = new Canvas();
        canvas.setBackground(new Color(175, 175, 175));
        canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        canvas.setMaximumSize(new Dimension(WIDTH, HEIGHT));
        canvas.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        canvas.setFocusable(false);

        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        // END FRAME
    }

    public JFrame getFrame() {
        return frame;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
