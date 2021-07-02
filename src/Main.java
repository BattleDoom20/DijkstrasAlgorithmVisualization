import javax.swing.*;
import java.awt.*;

public class Main
{
    public static void main(String[] args)
    {
        JFrame codeSimFrame = new JFrame("Code Simulation");
        codeSimFrame.setSize(new Dimension(700, 700));
        codeSimFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        codeSimFrame.setResizable(false);
        codeSimFrame.setLocationRelativeTo(null);
        CodeSim codeSimPanel = new CodeSim("code");
        codeSimPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        codeSimPanel.setForeground(Color.WHITE);
        codeSimPanel.setBackground(new Color(40, 40, 40));
        codeSimPanel.init();
        codeSimFrame.add(codeSimPanel);
        codeSimFrame.setVisible(true);

        Program program = new Program(codeSimPanel);
        program.run();
    }
}
