package com.infotraxx.carfax.setup.ui.mainwindow;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

/**
 * A panel with a gradient background.
 * @author Ed Jenkins
 */
class GradientPanel extends JPanel
{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(GradientPanel.class);

    /**
     * Constructor.
     */
    public GradientPanel()
    {
    }

    /**
     * Override to show a gradient.
     * @param g {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Get dimensions.
        int x = this.getX();
        int y = this.getY();
        int w = this.getWidth();
        int h = this.getHeight();
        // Compensate for border insets.
        int dx = x;
        int dy = y;
        x -= dx;
        y -= dy;
        w += dy;
        h += dx;
        // Set dimensions and colours.
        Point p1 = new Point(x, y);
        Point p2 = new Point(w, h);
        // Color c1 = new Color(0, 128, 192);
        // Color c2 = new Color(192, 192, 192);
        // Color c1 = SystemColor.activeCaption;
        // Color c2 = SystemColor.inactiveCaption;
        Color c1 = SystemColor.controlDkShadow;
        Color c2 = SystemColor.controlShadow;
        Rectangle r = new Rectangle(x, y, w, h);
        // Paint the gradient.
        GradientPaint gp = new GradientPaint(p1, c1, p2, c2);
        g2.setPaint(gp);
        g2.fill(r);
    }

}
