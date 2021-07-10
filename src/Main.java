/*
Filename: Main.java
Author: Hyperrun Academy: Cavite Chapter - FEU TECH
 */

import javax.swing.*;
import java.awt.*;

public class Main
{
    public static void main(String[] args)
    {
        // Create Event Managers
        KeyManager keyManager = new KeyManager();
        MouseManager mouseManager = new MouseManager();

        // Status Frame
        JFrame statusFrame = new JFrame("Code Simulation");
        statusFrame.setSize(new Dimension(750, 636));
        statusFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        statusFrame.setResizable(false);
        statusFrame.setLocationRelativeTo(null);
        statusFrame.addKeyListener(keyManager);

        // Code Sim Panel
        CodeSim codeSimPanel = new CodeSim("code");
        codeSimPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        codeSimPanel.setForeground(Color.WHITE);
        codeSimPanel.setBackground(new Color(40, 40, 40));
        codeSimPanel.init();

        // Details Panel
        DetailsPanel detailsPanel = new DetailsPanel(statusFrame);

        statusFrame.add(codeSimPanel);
        statusFrame.setVisible(true);

        // Initialize Visualization
        Visualization visualization = new Visualization(codeSimPanel, detailsPanel, keyManager, mouseManager);
        // Show and Run Visualziation
        visualization.run();
    }
}
