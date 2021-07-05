import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class EdgePrompt extends JFrame implements ActionListener
{
    private final JFrame parent;
    private final DetailsPanel detailsPanel;
    private final int[] edge;
    private final JButton closeButton, saveButton;
    private final JTextField textField;

    public EdgePrompt(JFrame parent, DetailsPanel detailsPanel, int[] edge)
    {
        this.parent = parent;
        this.parent.setEnabled(false);
        this.detailsPanel = detailsPanel;
        this.detailsPanel.frame.setEnabled(false);
        this.edge = edge;
        setSize(new Dimension(300, 100));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(parent);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        JLabel info = new JLabel("Path between nodes " + (char) (65 + edge[0]) + " and " + (char) (65 + edge[1]));
        JPanel distance = new JPanel();
        JLabel label = new JLabel("Distance: ");
        textField = new JTextField();
        textField.setText(String.valueOf(edge[2]));
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
            parent.setEnabled(true);
            detailsPanel.frame.setEnabled(true);
            dispose();
        }
        if(e.getSource() == saveButton)
        {
            edge[2] = Integer.parseInt(textField.getText());
            detailsPanel.updateList();
            parent.setEnabled(true);
            detailsPanel.frame.setEnabled(true);
            dispose();
        }
    }
}
