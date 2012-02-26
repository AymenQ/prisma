package com.puzzletimer.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

public class WrapLayout implements LayoutManager {
    private final int xGap;
    private final int yGap;

    public WrapLayout(int xGap, int yGap) {
        this.xGap = xGap;
        this.yGap = yGap;
    }

    @Override
    public void addLayoutComponent(String name, Component component) {
    }

    @Override
    public void removeLayoutComponent(Component component) {
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        int minHeight = 0;

        Insets insets = target.getInsets();
        int maxWidth = target.getWidth();

        int nComponents = target.getComponentCount();
        int x = insets.left;
        int y = insets.top;

        for (int i = 0; i < nComponents; i++) {
            Component component = target.getComponent(i);
            int w = (int) component.getPreferredSize().getWidth();
            int h = (int) component.getPreferredSize().getHeight();

            minHeight = Math.max(minHeight, y + h);

            if (x + w + insets.right > maxWidth) {
                x = insets.left;
                y += h + this.yGap;
            }

            x += w + this.xGap;
        }

        return new Dimension(0, minHeight);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return minimumLayoutSize(target);
    }

    @Override
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int maxWidth = target.getWidth();

            int nComponents = target.getComponentCount();
            int firstComponent = 0;
            int x = insets.left;
            int y = insets.top;

            for (int i = 0; i < nComponents; i++) {
                Component component = target.getComponent(i);
                int w = (int) component.getPreferredSize().getWidth();
                int h = (int) component.getPreferredSize().getHeight();

                if (x + w + insets.right > maxWidth) {
                    int d = (maxWidth - (x - this.xGap) - insets.right) / 2;
                    for (int j = firstComponent; j < i; j++) {
                        Component c = target.getComponent(j);
                        c.setLocation(c.getX() + d, c.getY());
                    }

                    firstComponent = i;
                    x = insets.left;
                    y += h + this.yGap;
                }

                component.setBounds(x, y, w, h);

                x += w + this.xGap;
            }

            int d = (maxWidth - (x - this.xGap) - insets.right) / 2;
            for (int j = firstComponent; j < nComponents; j++) {
                Component c = target.getComponent(j);
                c.setLocation(c.getX() + d, c.getY());
            }
        }
    }
}
