import javax.swing.*;
import java.awt.*;

public class DetailsPanel
{
    public JFrame frame;
    public Font font;
    public JScrollPane scrollPane;
    public JPanel panel;

    public DetailsPanel(JFrame frame)
    {
        this.frame = frame;
        font = new Font("Consolas", Font.PLAIN, 13);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(50, 50, 100));
        scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(200, frame.getHeight()));
        frame.add(scrollPane, BorderLayout.WEST);
    }

    public void addEdgeLabel(int[] edge)
    {
        char source = (char) (65 + edge[0]);
        char destination = (char) (65 + edge[1]);

        JLabel label = new JLabel(source + " --> " + destination + " : " + edge[2]);
        label.setForeground(Color.WHITE);
        label.setFont(font);
        panel.add(label);
        scrollPane.revalidate();
        frame.revalidate();
    }

    public void resetPanel()
    {
        panel.removeAll();
        panel.revalidate();
        panel.repaint();
    }
}
