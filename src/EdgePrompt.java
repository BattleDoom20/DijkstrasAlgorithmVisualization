import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EdgePrompt extends JFrame implements ActionListener
{
    private final DetailsPanel detailsPanel;
    private final int[] edge;
    private final JButton closeButton, saveButton;
    private final JTextField textField;

    public EdgePrompt(DetailsPanel detailsPanel, int[] edge)
    {
        this.detailsPanel = detailsPanel;
        this.edge = edge;
        setSize(new Dimension(300, 100));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(detailsPanel.frame);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        JLabel info = new JLabel("Path between nodes " + (char) (65 + edge[0]) + " and " + (char) (65 + edge[1]));
        JPanel distance = new JPanel();
        JLabel label = new JLabel("Distance: ");
        textField = new JTextField(String.valueOf(edge[2]));
        textField.setPreferredSize(new Dimension(50, 16));
        distance.add(label);
        distance.add(textField);
        centerPanel.add(info);
        centerPanel.add(distance);

        JPanel southPanel = new JPanel();
        closeButton = new Button("CLOSE");
        closeButton.addActionListener(this);
        saveButton = new Button("SAVE");
        saveButton.addActionListener(this);
        southPanel.add(closeButton);
        southPanel.add(saveButton);

        add(centerPanel);
        add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == closeButton)
        {
            dispose();
        }
        if(e.getSource() == saveButton)
        {
            edge[2] = Integer.parseInt(textField.getText());
            detailsPanel.updateList();
            dispose();
        }
    }
}
