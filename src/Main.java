import javax.swing.*;
import java.awt.*;

/*
TODO
    1. Draw phase indicator on bottom right
    2. Draw playing/pause indicator on top left
 */

public class Main
{
    public static void main(String[] args)
    {
        JFrame statusFrame = new JFrame("Code Simulation");
        statusFrame.setSize(new Dimension(600, 530));
        statusFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        statusFrame.setResizable(false);
        statusFrame.setLocationRelativeTo(null);

        CodeSim codeSimPanel = new CodeSim("code");
        codeSimPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        codeSimPanel.setForeground(Color.WHITE);
        codeSimPanel.setBackground(new Color(40, 40, 40));
        codeSimPanel.init();

        DetailsPanel detailsPanel = new DetailsPanel(statusFrame);

        statusFrame.add(codeSimPanel);
        statusFrame.setVisible(true);

        Visualization visualization = new Visualization(codeSimPanel, detailsPanel);
        visualization.run();
    }
}
