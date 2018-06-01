package com.laurenrmontemayor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFileChooser;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;

public class Watermark
{
    private static JFrame frame;
    private JButton findImageButton;
    private JPanel panelMain;
    private JButton exportImageButton;
    private JTextField importField;
    private JTextField exportField;
    private JTextField watermarkField;
    private JLabel exportLabel;
    private JLabel importLabel;
    private JLabel watermarkHint;
    private JLabel previewLabel;
    private JButton importImageButton;

    private static String sourceImageString;
    private static String exportImageString;
    private static String previewImageString;

    public static void main(String[] args)
    {

        frame = new JFrame("Watermark");
        // set default placeholder image in the preview
        previewImageString = "image.png";
        setUp();

    }

    public static void setUp()
    {
        frame.setContentPane(new Watermark().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Watermark()
    {

        // "choose file" button that calls chooseFile()
        findImageButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                chooseFile();
            }
        });

        // basically just updates the preview and text
        importImageButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                // update preview
                setUp();
            }
        });

        // Clear the field if the user types
        importField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent keyEvent)
            {
                super.keyReleased(keyEvent);
                importField.setText("");
            }
        });

        // Make changes to the image and save the new file
        exportImageButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                exportImageString = exportField.getText();
                String watermarkString = watermarkField.getText();

                File sourceImageFile = new File(sourceImageString);
                File exportImageFile = new File(exportImageString);

                addTextWatermark(watermarkString, sourceImageFile, exportImageFile);
            }
        });

        // updating fields after typing in watermark text field because clicking Import button does not work
        // TODO: Fix text field updates

        watermarkField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent keyEvent)
            {
                super.keyReleased(keyEvent);
                importField.setText(sourceImageString);
                String suggestedFileLoc = importField.getText();
                suggestFileLocation(suggestedFileLoc); // change text in exportField to suggested location and name
            }
        });
    }

    static void addTextWatermark(String text, File sourceImageFile, File exportImageFile)
    {
        try
        {
            BufferedImage sourceImage = ImageIO.read(sourceImageFile);

            Graphics2D g = (Graphics2D) sourceImage.getGraphics();
            AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);

            g.setComposite(alphaChannel);
            g.setColor(Color.BLUE);
            g.setFont(new Font("Arial", Font.BOLD, 64));

            FontMetrics fontMetrics = g.getFontMetrics();
            Rectangle2D rect = fontMetrics.getStringBounds(text, g);

            // calculates the center coordinates
            int centerX = (sourceImage.getWidth() - (int) rect.getWidth()) / 2;
            int centerY = sourceImage.getHeight() / 2;

            // draw text
            g.drawString(text, centerX, centerY);

            ImageIO.write(sourceImage, "png", exportImageFile);
            g.dispose();

            // update preview
            previewImageString = exportImageString;
            setUp();

            JOptionPane.showMessageDialog(null, "You've made your mark!");

        } catch (IOException ex)
        {
            System.err.println(ex);
        }
    }

    private void createUIComponents()
    {
        previewLabel = new JLabel(new ImageIcon(previewImageString));
    }

    public void chooseFile()
    {
        JFileChooser fileChooser = new JFileChooser();

        // Filter to allow only PNG
        fileChooser.setFileFilter(new FileFilter()
        {
            @Override
            public boolean accept(File file)
            {
                if (file.isDirectory())
                {
                    return true;
                } else
                {
                    String filename = file.getName().toLowerCase();
                    return filename.endsWith(".png");
                }
            }

            @Override
            public String getDescription()
            {
                return "PNG Images(*.png)";
            }
        });

        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            sourceImageString = selectedFile.getAbsolutePath();
            previewImageString = selectedFile.getAbsolutePath();
            importField.setText(selectedFile.getAbsolutePath());
        }
        else
        {
            JOptionPane.showMessageDialog(null, "There's a problem with that file");
        }
    }

    private void suggestFileLocation(String suggestedFileLoc)
    {
        exportField.setText(suggestedFileLoc.substring(0, suggestedFileLoc.length() - 4));
        String s = suggestedFileLoc.substring(0,suggestedFileLoc.length() - 4) + "-watermark.png";

        System.out.println("suggested file location: " + suggestedFileLoc);

        System.out.println("s: " + s);

        exportField.setText(s);
    }
}
