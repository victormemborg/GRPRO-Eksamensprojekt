package itumulator.display;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import itumulator.display.overlay.OverlayCanvas;
import itumulator.display.utility.DayNightHelper;
import itumulator.display.utility.ImageResourceCache;
import itumulator.display.utility.ImageUtility;
import itumulator.simulator.Simulator;

/**
 * Provides a frame for the {@link Canvas} and controls for the
 * {@link Simulator}. This is not relevant to continue the project.
 */
public class Frame extends JFrame {
    private final int UI_HEIGHT = 25;
    private JTextField textField;
    private JLayeredPane layeredPane;
    private JPanel uiPanel;
    private DayNightHelper dayNightHelper;
    private OverlayCanvas overlayCanvas;

    public Frame(Canvas canvas, Simulator simulator, int pixel_size, boolean startIso) {
        dayNightHelper = new DayNightHelper();
        overlayCanvas = new OverlayCanvas(pixel_size, startIso);

        // Setup Frame
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Itumulator");
        setSize(pixel_size+16, pixel_size+36);
        setLocationRelativeTo(null);

        // Set layered pane
        layeredPane = new JLayeredPane();
        add(layeredPane);

        // Setup Canvas/Renderer
        layeredPane.add(canvas, JLayeredPane.DEFAULT_LAYER);
        canvas.setSize(pixel_size, pixel_size);

        // Setup Overlay
        layeredPane.add(overlayCanvas, JLayeredPane.PALETTE_LAYER);

        // Setup UI
        uiPanel = new JPanel();
        FlowLayout uiLayout = new FlowLayout(FlowLayout.RIGHT, 10, 5);
        uiPanel.setLayout(uiLayout);
        layeredPane.add(uiPanel, JLayeredPane.POPUP_LAYER);

        textField = new JTextField();
        textField.setText("Steps " + simulator.getSteps());
        textField.setEditable(false);
        int preferredWidth = textField.getFontMetrics(textField.getFont()).stringWidth(textField.getText()) + 5; // Add padding
        textField.setPreferredSize(new Dimension(preferredWidth, textField.getPreferredSize().height));

        // Initialize play/pause button
        JButton runButton = new JButton("Play/Pause");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!simulator.isRunning()) {
                    simulator.run();
                } else {
                    simulator.stop();
                }
            }
        });
        
        // Initialize Step button
        JButton stepButton = new JButton("Step");
        stepButton.addActionListener((e) -> {
            if (!simulator.isRunning())
                simulator.simulate();
        });
        
        // Initialize Swap Render Button
        JButton swapButton = new JButton("Swap");
        swapButton.addActionListener((e) -> {
            canvas.setIsomorphic(!canvas.isIsomorphic());
            if (canvas.isIsomorphic()){
                overlayCanvas.startRender();
            } else {
                overlayCanvas.stopRender();
            }
            canvas.paintImage();
        });
        setButtonImage(runButton, "play");
        setButtonImage(stepButton, "step");
        setButtonImage(swapButton, "basic-display");

        // Initialize DayNightLabel
        JLabel dayNightLabel = dayNightHelper.initialize(UI_HEIGHT);

        uiPanel.setBounds(0, 0, pixel_size, UI_HEIGHT+20);
        uiPanel.add(textField);
        uiPanel.add(dayNightLabel);
        uiPanel.add(runButton);
        uiPanel.add(stepButton);
        uiPanel.add(swapButton);
        uiPanel.setOpaque(false);
    }

    public void updateDayNightLabel(boolean isDaytime) {
        dayNightHelper.update(isDaytime);
    }

    public void updateStepLabel(int steps) {
        textField.setText("Steps " + steps);
        int preferredWidth = textField.getFontMetrics(textField.getFont()).stringWidth(textField.getText()) + 5; // Add padding
        textField.setPreferredSize(new Dimension(preferredWidth, textField.getPreferredSize().height));
        textField.setSize(textField.getPreferredSize());
        textField.revalidate();
        textField.repaint();
        uiPanel.revalidate();
        uiPanel.repaint();
    }

    private void setButtonImage(JButton button, String imageKey){
        BufferedImage img = ImageResourceCache.Instance().getImage(imageKey);
        double ratio = (UI_HEIGHT * 1.0) / img.getHeight();
        BufferedImage scaledImg = ImageUtility.getScaledImage(img, (int)(ratio * img.getWidth()), UI_HEIGHT);
        ImageIcon imgIcon = new ImageIcon(scaledImg);
        button.setIcon(imgIcon);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBorder(null);
        button.setText("");
    }
}
