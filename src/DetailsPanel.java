import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class DetailsPanel implements MouseListener
{
    private ArrayList<int[]> edges;
    public final JFrame frame;
    private final Font font;
    private final JScrollPane scrollPane;
    private final JPanel panel;
    private ArrayList<JLabel> entries;
    private JButton addButton;

    public DetailsPanel(JFrame frame)
    {
        this.frame = frame;
        entries = new ArrayList<>();
        font = new Font("Consolas", Font.PLAIN, 13);
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(50, 50, 100));
        scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(200, frame.getHeight()));
        updateList(null);
        frame.add(scrollPane, BorderLayout.WEST);
    }

    public void updateList()
    {
        panel.removeAll();

        JLabel headerLabel = new JLabel("Edge List");
        headerLabel.setPreferredSize(new Dimension(125, 16));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(font);
        panel.add(headerLabel);

        entries = new ArrayList<>();

        if(edges != null)
        {
            for(int[] edge : edges)
            {
                char source = (char) (65 + edge[0]);
                char destination = (char) (65 + edge[1]);
                JLabel label = new JLabel(source + " <--> " + destination + " : " + edge[2]);
                label.setLayout(new FlowLayout());
                label.setForeground(Color.WHITE);
                label.setFont(font);
                label.addMouseListener(this);
                entries.add(label);
                panel.add(label);
            }
        }

        frame.revalidate();
        frame.repaint();
    }

    public void updateList(ArrayList<int[]> edges)
    {
        this.edges = edges;
        updateList();
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        for(int i = 0; i < entries.size(); i++)
        {
            if(e.getSource() == entries.get(i))
            {
                new EdgePrompt(this, edges.get(i));
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {

    }

    @Override
    public void mouseReleased(MouseEvent e)
    {

    }

    @Override
    public void mouseEntered(MouseEvent e)
    {

    }

    @Override
    public void mouseExited(MouseEvent e)
    {

    }
}
