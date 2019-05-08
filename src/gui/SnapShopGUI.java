/*
 * TCSS 305 - Autumn 2016
 * Assignment 4 - SnapShop
 */

package gui; //NOPMD suppress the warning about too many imports.

import filters.EdgeDetectFilter;
import filters.EdgeHighlightFilter;
import filters.Filter;
import filters.FlipHorizontalFilter;
import filters.FlipVerticalFilter;
import filters.GrayscaleFilter;
import filters.SharpenFilter;
import filters.SoftenFilter;

import image.PixelImage;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Create and display a GUI that allows the user to apply 
 * some simple filters to an image of their choice and save those images
 * with the filtes applied.
 * 
 * @author Yaro Salo
 * @version November 2, 2016
 */
public class SnapShopGUI {
        
    /** ToolKit. */
    private static final Toolkit KIT = Toolkit.getDefaultToolkit();
    
    /** Dimensions of the screen. */
    private static final Dimension SCREEN_SIZE = KIT.getScreenSize();
    
    /** The width of the screen. */
    private static final int SCREEN_WIDTH = SCREEN_SIZE.width;
    
    /** The height of the screen. */
    private static final int SCREEN_HEIGHT = SCREEN_SIZE.height;

    /** The image the user chooses to open. */
    private PixelImage myImage;
    
    /** The Frame to display the GUI elements. */
    private final JFrame myFrame;
    
    /** A FileChooser for this application's GUI. */
    private final JFileChooser myFileChooser;
    
    /** A label to display the chosen image. */
    private final JLabel myLabel;
   
    /** A panel that contains the filter buttons. */
    private final JPanel myFilterPanel;
    
    /** The array to hold my filters. */
    private final List<Filter> myFilters;
    
    /** The button to open an image file. */
    private final JButton myOpenButton;
    
    /** The button to close an image file. */
    private final JButton myCloseButton;
    
    /** The button to save an image file. */
    private final JButton mySaveButton;
    
    /** The initial size of the frame. */
    private Dimension myIinitialSize;

    /**
     * 
     * Initialize instance fields.
     */
    public SnapShopGUI() {        
        
        myFrame = new JFrame();
        myLabel = new JLabel();
        
        // Initially open the JFileChooser in "ysalo-snapshot".
        myFileChooser = new JFileChooser(".");
        
        myFilterPanel = new JPanel(new GridLayout(0, 1));
        myFilters = new ArrayList<Filter>();
        
        myOpenButton = new JButton("Open...");      //, new ImageIcon("icons/open.gif"));
        mySaveButton = new JButton("Save As...");   //, new ImageIcon("icons/close.gif"));
        myCloseButton = new JButton("Close Image"); //, new ImageIcon("icons/save.gif"));
       
        myIinitialSize = myFrame.getSize();   
    }
    
    /**
     * 
     * Set up and display the GUI.
     */
    public void start() {
      
        // Add necessary filters to the array list. 
        addFilters();
        
        // Set the title and default close operation of the frame.
        myFrame.setTitle("TCSS 305 SnapShop");
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Center the label in the frame.
        myLabel.setHorizontalAlignment(SwingConstants.CENTER);
        myLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        // Add listeners to the buttons in the south panel.
        myOpenButton.addActionListener(new OpenButtonListener());
        mySaveButton.addActionListener(new SaveButtonListener());
        myCloseButton.addActionListener(new CloseButtonListener());
        
        // Set the icons for these buttons.
        setIcons();
        
        // Add buttons to the south panel.
        final Panel southPanel = new Panel(new FlowLayout());
        southPanel.add(myOpenButton);
        southPanel.add(mySaveButton);
        southPanel.add(myCloseButton);
        
        // Create the filter buttons along with their listeners 
        // and add them to myFilterPanel.
        createFilterButtons();
        
        // Disable all buttons except the open button.
        mySaveButton.setEnabled(false);
        myCloseButton.setEnabled(false);
        enableFilterButtons(false);
        
        // Add the panels with the buttons to the frame.
        myFrame.add(southPanel, BorderLayout.SOUTH);
        myFrame.add(myFilterPanel, BorderLayout.WEST);
        
        myFrame.pack();
        
        // Remember the size of the array after all buttons were added.
        myIinitialSize = myFrame.getSize();
        
        // Make sure the Frame is sufficiently large as to display all the buttons.
        myFrame.setMinimumSize(myFrame.getSize());
        
        // Center the frame on the screen.
        setCentered();
        
        myFrame.setVisible(true);
    }
    
    
    /**
     * 
     * Center the location of the frame on the screen.
     */
    private void setCentered() {
        
        myFrame.setLocation(SCREEN_WIDTH / 2 - myFrame.getWidth() / 2, 
                            SCREEN_HEIGHT / 2 - myFrame.getHeight() / 2);
    }
    
    /**
     * 
     * Sets the icons for the open, close and save buttons.
     */
    private void setIcons() {

        myOpenButton.setIcon(new ImageIcon("icons/open.gif"));
        myCloseButton.setIcon(new ImageIcon("icons/close.gif"));
        mySaveButton.setIcon(new ImageIcon("icons/save.gif"));
    }
    
    /**
     *
     * Adds the Filters to myFilters.
     */
    private void addFilters() {
 
        myFilters.add(new EdgeDetectFilter()); 
        myFilters.add(new EdgeHighlightFilter()); 
        myFilters.add(new FlipHorizontalFilter()); 
        myFilters.add(new FlipVerticalFilter()); 
        myFilters.add(new GrayscaleFilter()); 
        myFilters.add(new SharpenFilter()); 
        myFilters.add(new SoftenFilter()); 
        
    }
    
    
    /**
     *
     * A function that creates buttons based on the number of filters in 
     * myFilter array list and adds them to myFilterPanel.
     */
    private void createFilterButtons() {
        for (int i = 0; i < myFilters.size(); i++) {
            myFilterPanel.add(createButton(myFilters.get(i)));
        }
    }
    
    
    /**
     *
     * Create a single filter button that will call the filter() method in the 
     * respective Filter object when pressed.
     *
     * @param theFilter is the Filter object based on which a button will be created.
     * @return the button.
     */
    private JButton createButton(final Filter theFilter) {
        
        final JButton button = new JButton(theFilter.getDescription());

        /**
         *
         * An inner class that calls the filter() method on the 
         * specified image.
         */
        class AnActionListener implements ActionListener {

            /**
             * 
             * This method calls the filter method on the image and 
             * repaints the frame.
             *
             * @param theEvent An Event, ignored here.
             */
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
            
                theFilter.filter(myImage);
                myFrame.repaint();

            }
        }

        // Add an action listener to the button.
        button.addActionListener(new AnActionListener());

        return button;
    }
   
    /**
    *
    * A function to enable and disable the filter buttons.
    * If the flag is true enable the buttons if false disable the buttons.
    * 
    * @param theFlag parameter to decide if the user wants to enable the buttons.  
    */
    private void enableFilterButtons(final boolean theFlag) {
       
        if (theFlag) {
           
            for (final Component cp : myFilterPanel.getComponents()) {
                cp.setEnabled(true);
            
            } 
           
        } else {
           
            for (final Component cp : myFilterPanel.getComponents()) {
                cp.setEnabled(false);
            }
        }

    }
    
    /**
     *  
     *  Create a listener for the open button.
     *  
     * @author Yaro Salo
     * @version November 2, 2016
     */
    private class OpenButtonListener implements ActionListener { 
    
        /**
         * 
         * This method opens an image a user chooses.
         * It then adds that image to a JLabel which is then added to the center region 
         * of myJFrame. This method also enables all the other buttons in myFrame.
         * If the file chosen was not an image the method will display a JOptionPane
         * error message. 
         *
         * @param theEvent An Event, ignored here.
         */
        @Override
        public void actionPerformed(final ActionEvent theEvent) {
            
            final int result = myFileChooser.showOpenDialog(myFrame.getRootPane());
            
            // If the user chose to open an image.
            if (result == JFileChooser.APPROVE_OPTION) {
                
                // Check if the selected file can be opened as an image.
                try {
                    
                    myImage = PixelImage.load(myFileChooser.getSelectedFile());
                    
                    //Set the icon of the label to the current image and add it to the frame.
                    myLabel.setIcon(new ImageIcon(myImage));
                    myFrame.add(myLabel, BorderLayout.CENTER);
                    
                    // Enable the save, close and filter buttons.
                    mySaveButton.setEnabled(true);
                    myCloseButton.setEnabled(true);
                    enableFilterButtons(true);
                    
                    // Pack the frame so it will fit the image and buttons.
                    myFrame.setMinimumSize(myIinitialSize);
                    myFrame.pack();
                    myFrame.setMinimumSize(myFrame.getSize());
                    
                    setCentered();
                   
                } catch (final IOException e) {
                    
                    // Show a message dialog if the file could not be opened as an image.
                    JOptionPane.showMessageDialog(null, 
                        "The selected file did not contain an image!",  
                        "Save Error",  JOptionPane.ERROR_MESSAGE);
                    
                    // Redisplay the FileChooser.
                    actionPerformed(theEvent);
                }
            }
        }
        
    }
    


    
    /**
     *  
     *  Create a listener for the close button.
     *  
     * @author Yaro Salo
     * @version November 2, 2016
     */
    private class CloseButtonListener implements ActionListener {
    
        
        /**
         * 
         * This method closes an image when the close button is pressed.
         * It also resizes the frame to it's original size when the buttons were added and
         * it disables all the buttons accept the open button. 
         *
         * @param theEvent An Event, ignored here.
         */
        @Override 
        public void actionPerformed(final ActionEvent theEvent) {
            
            // Remove the image and resize the frame.
            myFrame.remove(myLabel);
            myFrame.setMinimumSize(myIinitialSize);
            myFrame.setSize(myIinitialSize);
            
            setCentered();            
            
            // Disable the close, save and filter buttons
            myCloseButton.setEnabled(false);
            mySaveButton.setEnabled(false);
            enableFilterButtons(false);
            
            
        }
        
    }
   
    /**
     *  
     *  Create a listener for the save button.
     *  
     * @author Yaro Salo
     * @version November 2, 2016
     */
    private class SaveButtonListener implements ActionListener {
    
        
        /**
         * 
         * This method save an image where a user chooses when the save button is pressed. 
         * If the file could not be written the method will display 
         * a JOptionPane error message.
         * If the user attempts to overwrite an existing file a JOptionPane confirmation 
         * dialog will display warning the user, but still give them an option to overwrite. 
         *
         * @param theEvent An Event, ignored here.
         */
        @Override 
        public void actionPerformed(final ActionEvent theEvent) {
        
            
            int result = myFileChooser.showSaveDialog(myFrame.getRootPane());
            
            if (result == JFileChooser.APPROVE_OPTION) {
                
                final File file = myFileChooser.getSelectedFile();
                
                try {
                   
                    // Warn the user if they attempt to overwrite an existing file.
                    if (file.exists()) {
                        result = JOptionPane.showConfirmDialog(myFileChooser,
                        "Do you want to overwrite the existing file?");
                        
                        if (result == JOptionPane.OK_OPTION) {
                            myImage.save(file);
                        
                        } else {
                            
                            // Allow the user to save the file under a different name
                            // without them opening the save dialog again.
                            actionPerformed(theEvent);
                        }
                        
                    } else {
                        
                        myImage.save(file);
                    }
                    
                } catch (final IOException e) {
                    JOptionPane.showMessageDialog(null, 
                        "The file cannot be written!",  
                        "Error", JOptionPane.ERROR_MESSAGE);
                                              
                }
            }
           
        }
    }
}

    