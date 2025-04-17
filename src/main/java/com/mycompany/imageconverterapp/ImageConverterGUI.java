package javaappconverter2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageConverterGUI extends JFrame {

    private DefaultListModel<File> listModel;  // Store file references instead of file paths
    private JList<File> imageList;
    private JButton selectButton, convertButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private File[] selectedFiles;

    public ImageConverterGUI() {
        setTitle("Image Converter");
        setSize(400, 400);  // Set window size to 400x400
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center the window
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        imageList = new JList<>(listModel);
        imageList.setCellRenderer(new ImageCellRenderer());  // Custom cell renderer to scale images

        JScrollPane scrollPane = new JScrollPane(imageList);

        selectButton = new JButton("Select Images");
        convertButton = new JButton("Convert to PNG");
        progressBar = new JProgressBar();
        statusLabel = new JLabel("Status: Waiting...");

        JPanel topPanel = new JPanel();
        topPanel.add(selectButton);
        topPanel.add(convertButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.PAGE_END);

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectImages();
            }
        });

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertImages();
            }
        });
    }

    private void selectImages() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setDialogTitle("Select JPG Images");
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            selectedFiles = fileChooser.getSelectedFiles();
            listModel.clear();
            for (File file : selectedFiles) {
                listModel.addElement(file);  // Add the file to the list
            }
        }
    }

    private void convertImages() {
        if (selectedFiles == null || selectedFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select images first.");
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(3);
        progressBar.setMaximum(selectedFiles.length);
        progressBar.setValue(0);

        for (File inputFile : selectedFiles) {
            File outputFile = new File(inputFile.getParent(), inputFile.getName().replace(".jpg", ".png"));
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        BufferedImage image = ImageIO.read(inputFile);
                        ImageIO.write(image, "png", outputFile);
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setValue(progressBar.getValue() + 1);
                            statusLabel.setText("Converted: " + inputFile.getName());

                            // Update list with converted file (change extension to PNG)
                            listModel.setElementAt(outputFile, listModel.indexOf(inputFile));
                        });
                    } catch (IOException ex) {
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("Error converting: " + inputFile.getName());
                        });
                    }
                }
            });
        }

        executor.shutdown();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ImageConverterGUI().setVisible(true);
        });
    }

    // Custom renderer to scale images and display them with the extension
    class ImageCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            File file = (File) value;
            String extension = getFileExtension(file);
            ImageIcon icon = new ImageIcon(file.getPath());
            Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);  // Scale to 100x100
            icon = new ImageIcon(img);

            label.setIcon(icon);
            label.setText(extension);  // Display the file extension (e.g., JPG, PNG)

            return label;
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex != -1) {
            return name.substring(dotIndex + 1).toUpperCase();  // Return extension (e.g., JPG, PNG)
        }
        return "";  // If no extension found, return empty string
    }
}
