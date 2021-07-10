import javax.swing.*;
import java.awt.*;

public class Main
{
    public static void main(String[] args)
    {
        KeyManager keyManager = new KeyManager();
        MouseManager mouseManager = new MouseManager();

        JFrame statusFrame = new JFrame("Code Simulation");
        statusFrame.setSize(new Dimension(750, 636));
        statusFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        statusFrame.setResizable(false);
        statusFrame.setLocationRelativeTo(null);
        statusFrame.addKeyListener(keyManager);

        CodeSim codeSimPanel = new CodeSim("code");
        codeSimPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        codeSimPanel.setForeground(Color.WHITE);
        codeSimPanel.setBackground(new Color(40, 40, 40));
        codeSimPanel.init();

        DetailsPanel detailsPanel = new DetailsPanel(statusFrame);

        statusFrame.add(codeSimPanel);
        statusFrame.setVisible(true);

        Visualization visualization = new Visualization(codeSimPanel, detailsPanel, keyManager, mouseManager);
        visualization.run();
    }
}
