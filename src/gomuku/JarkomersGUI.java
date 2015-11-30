package gomuku;

import javax.swing.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.event.*;

public class JarkomersGUI
{
    // Bikin 20 button
    private static JButton buttons[] = new JButton[400];

    private static void gamePanel()
    {
        JFrame frame = new JFrame("Gamenya Anak Jarkom");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(20, 20));
        panel.setBorder(BorderFactory.createLineBorder(Color.gray, 3));
        panel.setBackground(Color.white);

        for (int i = 0; i <= 399; i++)
        {
            buttons[i] = new MyButton();
            panel.add(buttons[i]);
        } 
        
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new GridLayout(2, 2));
        statusPanel.add(new JButton("Button 1"));
        statusPanel.add(new JButton("Button 1"));
        statusPanel.add(new JButton("Button 1"));
        statusPanel.add(new JButton("Button 1"));
        statusPanel.setBackground(Color.darkGray);
        frame.getContentPane().add(panel, BorderLayout.WEST);
        
        frame.getContentPane().add(statusPanel, BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(1300, 700);
    }

    public static int player = 1;

    private static class MyButton extends JButton implements ActionListener
    {
        int playagain = 1000;
        boolean win = false;
        boolean end = false;
        String letter;
        public MyButton()
        {
            super();
            letter = " ";
            setFont(new Font("Dialog", 1, 10));
            setText(letter);
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e)
        {
            if (player == 1)
            {
                while (end == false)
                {
                    if ((getText().equals(" ")) && (win == false))
                    {
                        letter = "A";
                        setText(letter);
                        player = player + 1;
                    }
                    else
                    {

                    }
                    end = true;
                }
            }
            else
            if (player == 2)
            {
                while (end == false)
                {
                    if ((getText().equals(" ")) && (win == false))
                    {
                        letter = "B";
                        setText(letter);
                        player = player + 1;
                    }
                    else
                    {

                    }
                    end = true;
                }
            }
            else
            if (player == 3)
            {
                while (end == false)
                {
                    if ((getText().equals(" ")) && (win == false))
                    {
                        letter = "C";
                        setText(letter);
                        player = 1;
                    }
                    else
                    {

                    }
                    end = true;
                }
            }
            end = false;
            //setText(letter);
        }
    }

    public static void main(String[] args)
    {
        gamePanel(); // Launch the game!
    }
}
