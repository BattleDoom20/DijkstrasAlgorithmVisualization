import javax.swing.*;
import java.awt.*;

public class Button extends JButton
{
    public Button(String text)
    {
        setText(text);
        setPreferredSize(new Dimension(50, 16));
        setFont(new Font("Consolas", Font.PLAIN, 10));
        setMargin(new Insets(4, 0, 2, 0));
        setFocusPainted(false);
    }
}
