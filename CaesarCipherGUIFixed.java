import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaesarCipherGUIFixed extends JFrame {

    private final JTextField messageField = new JTextField(25);
    private final JTextField shiftField = new JTextField(6);
    private final JTextArea resultArea = new JTextArea(6, 40);

    public CaesarCipherGUIFixed() {
        setTitle("Caesar Cipher Encryption & Decryption");
        setSize(560, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Title
        JLabel titleLabel = new JLabel("Caesar Cipher", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(new Color(30, 144, 255));

        // Input panel using GridBag for good spacing
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel messageLabel = new JLabel("Enter Message:");
        JLabel shiftLabel = new JLabel("Shift Value:");

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        inputPanel.add(messageLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        inputPanel.add(messageField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        inputPanel.add(shiftLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.2;
        inputPanel.add(shiftField, gbc);

        // Buttons
        JButton encryptButton = new JButton("Encrypt");
        JButton decryptButton = new JButton("Decrypt");
        JButton saveButton = new JButton("Save Result");
        JButton clearButton = new JButton("Clear");

        encryptButton.setBackground(new Color(60, 179, 113)); encryptButton.setForeground(Color.WHITE);
        decryptButton.setBackground(new Color(65, 105, 225)); decryptButton.setForeground(Color.WHITE);
        saveButton.setBackground(new Color(128, 0, 128)); saveButton.setForeground(Color.WHITE);
        clearButton.setBackground(new Color(220, 20, 60)); clearButton.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);

        // Result area
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Result"));

        // Build main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        // Frame layout
        setLayout(new BorderLayout(8, 8));
        add(titleLabel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Action listeners
        encryptButton.addActionListener(e -> handleCipher(true));
        decryptButton.addActionListener(e -> handleCipher(false));
        clearButton.addActionListener(e -> {
            messageField.setText("");
            shiftField.setText("");
            resultArea.setText("");
        });
        saveButton.addActionListener(e -> saveResult());

        // allow Enter key in shift field to trigger encryption for convenience
        shiftField.addActionListener(e -> handleCipher(true));
    }

    // Common entry point for encrypt/decrypt
    private void handleCipher(boolean encrypt) {
        String message = messageField.getText();
        String shiftText = shiftField.getText();

        if (message == null || message.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a message to process.", "Missing Message", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int shift;
        try {
            shift = Integer.parseInt(shiftText.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer for shift value.", "Invalid Shift", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Normalize shift to [0,25]
        shift = ((shift % 26) + 26) % 26;

        String output = caesarCipher(message, shift, encrypt);
        String header = encrypt ? "Encrypted Message:" : "Decrypted Message:";
        resultArea.setText(header + "\n" + output);
    }

    // Caesar cipher implementation (robust)
    private String caesarCipher(String text, int shift, boolean encrypt) {
        if (!encrypt) { // for decryption invert the shift
            shift = (26 - shift) % 26;
        }
        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                int base = 'A';
                char nc = (char) ((c - base + shift) % 26 + base);
                sb.append(nc);
            } else if (Character.isLowerCase(c)) {
                int base = 'a';
                char nc = (char) ((c - base + shift) % 26 + base);
                sb.append(nc);
            } else {
                sb.append(c); // keep digits, spaces, punctuation unchanged
            }
        }
        return sb.toString();
    }

    // Save current result to file with timestamp
    private void saveResult() {
        String result = resultArea.getText();
        if (result == null || result.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No result to save. Please perform encryption or decryption first.", "Nothing to Save", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        String entry = "[" + timeStamp + "] " + result.replace("\n", " | ");

        try (FileWriter fw = new FileWriter("cipher_output.txt", true)) {
            fw.write(entry + System.lineSeparator());
            fw.write("---------------------------------------" + System.lineSeparator());
            JOptionPane.showMessageDialog(this, "Result saved to cipher_output.txt", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save file: " + ex.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CaesarCipherGUIFixed().setVisible(true));
    }
}
