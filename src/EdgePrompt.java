/*
Filename: EdgePrompt.java
Author: Hyperrun Academy: Cavite Chapter - FEU TECH
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
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
        // Frame Setup
        this.parent = parent;
        this.parent.setEnabled(false);
        this.detailsPanel = detailsPanel;
        this.detailsPanel.frame.setEnabled(false);
        this.edge = edge;
        setSize(new Dimension(300, 100));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(parent);

        // Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        JLabel info = new JLabel("Path between nodes " + (char) (65 + edge[0]) + " and " + (char) (65 + edge[1]));
        JPanel distance = new JPanel();
        JLabel label = new JLabel("Distance: ");
        textField = new JTextField();
        textField.setText(String.valueOf(edge[2]));
        textField.setPreferredSize(new Dimension(50, 16));
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                textField.setEditable((e.getKeyChar() >= '0' && e.getKeyChar() <= '9') || e.getKeyChar() == KeyEvent.VK_BACK_SPACE);
            }
        });

        distance.add(label);
        distance.add(textField);
        centerPanel.add(info);
        centerPanel.add(distance);

        // South Panel
        JPanel southPanel = new JPanel();
        closeButton = new JButton("CLOSE");
        closeButton.setPreferredSize(new Dimension(50, 16));
        closeButton.setFont(new Font("Consolas", Font.PLAIN, 10));
        closeButton.setMargin(new Insets(4, 0, 2, 0));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(this);
        saveButton = new JButton("SAVE");
        saveButton.setPreferredSize(new Dimension(50, 16));
        saveButton.setFont(new Font("Consolas", Font.PLAIN, 10));
        saveButton.setMargin(new Insets(4, 0, 2, 0));
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(this);

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
            try {
                edge[2] = Integer.parseInt(textField.getText());
                detailsPanel.updateList();
                parent.setEnabled(true);
                detailsPanel.frame.setEnabled(true);
                dispose();
            }
            catch (Exception ae) {
                JOptionPane.showMessageDialog(this,"Invalid input! Please try again!","Alert",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
