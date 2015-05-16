package com.infotraxx.carfax.setup.ui.systemcolorwindow;

import java.awt.BorderLayout;
import java.awt.SystemColor;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

/**
 * This is the system color window.
 * @author Ed Jenkins
 */
public class SystemColorWindow extends JFrame
{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(SystemColorWindow.class);

    /**
     * Event handler for when the window is closing.
     */
    WindowClosingEvent evtWindowClosing = new WindowClosingEvent();

    /**
     * Constructor.
     */
    public SystemColorWindow()
    {
        // Set the title.
        this.setTitle("System Colors");
        // Hook up the close button.
        this.addWindowListener(evtWindowClosing);
        // activeCaption
        JLabel activeCaption = new JLabel();
        activeCaption.setText("activeCaption");
        activeCaption.setBackground(SystemColor.activeCaption);
        activeCaption.setOpaque(true);
        // activeCaptionBorder
        JLabel activeCaptionBorder = new JLabel();
        activeCaptionBorder.setText("activeCaptionBorder");
        activeCaptionBorder.setBackground(SystemColor.activeCaptionBorder);
        activeCaptionBorder.setOpaque(true);
        // activeCaptionText
        JLabel activeCaptionText = new JLabel();
        activeCaptionText.setText("activeCaptionText");
        activeCaptionText.setBackground(SystemColor.activeCaptionText);
        activeCaptionText.setOpaque(true);
        // control
        JLabel control = new JLabel();
        control.setText("control");
        control.setBackground(SystemColor.control);
        control.setOpaque(true);
        // controlDkShadow
        JLabel controlDkShadow = new JLabel();
        controlDkShadow.setText("controlDkShadow");
        controlDkShadow.setBackground(SystemColor.controlDkShadow);
        controlDkShadow.setOpaque(true);
        // controlHighlight
        JLabel controlHighlight = new JLabel();
        controlHighlight.setText("controlHighlight");
        controlHighlight.setBackground(SystemColor.controlHighlight);
        controlHighlight.setOpaque(true);
        // controlLtHighlight
        JLabel controlLtHighlight = new JLabel();
        controlLtHighlight.setText("controlLtHighlight");
        controlLtHighlight.setBackground(SystemColor.controlLtHighlight);
        controlLtHighlight.setOpaque(true);
        // controlShadow
        JLabel controlShadow = new JLabel();
        controlShadow.setText("controlShadow");
        controlShadow.setBackground(SystemColor.controlShadow);
        controlShadow.setOpaque(true);
        // controlText
        JLabel controlText = new JLabel();
        controlText.setText("controlText");
        controlText.setBackground(SystemColor.controlText);
        controlText.setOpaque(true);
        // desktop
        JLabel desktop = new JLabel();
        desktop.setText("desktop");
        desktop.setBackground(SystemColor.desktop);
        desktop.setOpaque(true);
        // inactiveCaption
        JLabel inactiveCaption = new JLabel();
        inactiveCaption.setText("inactiveCaption");
        inactiveCaption.setBackground(SystemColor.inactiveCaption);
        inactiveCaption.setOpaque(true);
        // inactiveCaptionBorder
        JLabel inactiveCaptionBorder = new JLabel();
        inactiveCaptionBorder.setText("inactiveCaptionBorder");
        inactiveCaptionBorder.setBackground(SystemColor.inactiveCaptionBorder);
        inactiveCaptionBorder.setOpaque(true);
        // inactiveCaptionText
        JLabel inactiveCaptionText = new JLabel();
        inactiveCaptionText.setText("inactiveCaptionText");
        inactiveCaptionText.setBackground(SystemColor.inactiveCaptionText);
        inactiveCaptionText.setOpaque(true);
        // info
        JLabel info = new JLabel();
        info.setText("info");
        info.setBackground(SystemColor.info);
        info.setOpaque(true);
        // infoText
        JLabel infoText = new JLabel();
        infoText.setText("infoText");
        infoText.setBackground(SystemColor.infoText);
        infoText.setOpaque(true);
        // menu
        JLabel menu = new JLabel();
        menu.setText("menu");
        menu.setBackground(SystemColor.menu);
        menu.setOpaque(true);
        // menuText
        JLabel menuText = new JLabel();
        menuText.setText("menuText");
        menuText.setBackground(SystemColor.menuText);
        menuText.setOpaque(true);
        // scrollbar
        JLabel scrollbar = new JLabel();
        scrollbar.setText("scrollbar");
        scrollbar.setBackground(SystemColor.scrollbar);
        scrollbar.setOpaque(true);
        // text
        JLabel text = new JLabel();
        text.setText("text");
        text.setBackground(SystemColor.text);
        text.setOpaque(true);
        // textHighlight
        JLabel textHighlight = new JLabel();
        textHighlight.setText("textHighlight");
        textHighlight.setBackground(SystemColor.textHighlight);
        textHighlight.setOpaque(true);
        // textHighlightText
        JLabel textHighlightText = new JLabel();
        textHighlightText.setText("textHighlightText");
        textHighlightText.setBackground(SystemColor.textHighlightText);
        textHighlightText.setOpaque(true);
        // textInactiveText
        JLabel textInactiveText = new JLabel();
        textInactiveText.setText("textInactiveText");
        textInactiveText.setBackground(SystemColor.textInactiveText);
        textInactiveText.setOpaque(true);
        // textText
        JLabel textText = new JLabel();
        textText.setText("textText");
        textText.setBackground(SystemColor.textText);
        textText.setOpaque(true);
        // window
        JLabel window = new JLabel();
        window.setText("window");
        window.setBackground(SystemColor.window);
        window.setOpaque(true);
        // windowBorder
        JLabel windowBorder = new JLabel();
        windowBorder.setText("windowBorder");
        windowBorder.setBackground(SystemColor.windowBorder);
        windowBorder.setOpaque(true);
        // windowText
        JLabel windowText = new JLabel();
        windowText.setText("windowText");
        windowText.setBackground(SystemColor.windowText);
        windowText.setOpaque(true);
        // Box it up.
        Box box = new Box(BoxLayout.PAGE_AXIS);
        box.add(activeCaption);
        box.add(activeCaptionBorder);
        box.add(activeCaptionText);
        box.add(control);
        box.add(controlDkShadow);
        box.add(controlHighlight);
        box.add(controlLtHighlight);
        box.add(controlShadow);
        box.add(controlText);
        box.add(desktop);
        box.add(inactiveCaption);
        box.add(inactiveCaptionBorder);
        box.add(inactiveCaptionText);
        box.add(info);
        box.add(infoText);
        box.add(menu);
        box.add(menuText);
        box.add(scrollbar);
        box.add(text);
        box.add(textHighlight);
        box.add(textHighlightText);
        box.add(textInactiveText);
        box.add(textText);
        box.add(window);
        box.add(windowBorder);
        box.add(windowText);
        // Add components to the panel.
        this.add(box, BorderLayout.NORTH);
        // Center the window on the screen.
        this.pack();
        this.setLocationRelativeTo(null);
    }

}
