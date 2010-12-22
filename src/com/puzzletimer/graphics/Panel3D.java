package com.puzzletimer.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JPanel;

import com.puzzletimer.graphics.algebra.Matrix44;
import com.puzzletimer.graphics.algebra.Vector3;

@SuppressWarnings("serial")
public class Panel3D extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    public Mesh mesh;
    public Vector3 lightDirection;
    public Vector3 viewerPosition;
    public Vector3 cameraPosition;
    public Vector3 cameraRotation;

    private int lastX;
    private int lastY;

    public Panel3D()
    {
        this.mesh = new Mesh(new ArrayList<Vector3>(), new ArrayList<Face>());
        this.lightDirection = new Vector3(0d, 0.25d, -1d).normalized();
        this.viewerPosition = new Vector3(0d, 0d, -325d);
        this.cameraPosition = new Vector3(0d, 0d, -3d);
        this.cameraRotation = new Vector3(0d, 0d, 0d);

        this.lastX = 0;
        this.lastY = 0;

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    private Vector3 toCameraCoordinates(Vector3 v) {
        return Matrix44.rotationX(-this.cameraRotation.x).mul(
               Matrix44.rotationY(-this.cameraRotation.y).mul(
               Matrix44.rotationZ(-this.cameraRotation.z).mul(
               v.sub(this.cameraPosition))));
    }

    private Vector3 perspectiveProjection(Vector3 v) {
        return new Vector3(
            (getWidth() / 2d) + (-v.x - this.viewerPosition.x) * (this.viewerPosition.z / v.z),
            (getHeight() / 2d) + (v.y - this.viewerPosition.y) * (this.viewerPosition.z / v.z),
            0d);
    }

    private Vector3 triangleNormal(Vector3 v1, Vector3 v2, Vector3 v3) {
        return v2.sub(v1).cross(v3.sub(v1)).normalized();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // projection
        ArrayList<Vector3> pVertices = new ArrayList<Vector3>();
        for (Vector3 v : this.mesh.vertices) {
            pVertices.add(perspectiveProjection(toCameraCoordinates(v)));
        }

        // painter's algorithm
        Collections.sort(this.mesh.faces, new Comparator<Face>() {
            @Override
            public int compare(Face f1, Face f2) {
                double centroidZ1 = 0d;
                for (int i : f1.vertexIndices) {
                    centroidZ1 += Panel3D.this.mesh.vertices.get(i).z;
                }
                centroidZ1 /= f1.vertexIndices.size();

                double centroidZ2 = 0d;
                for (int i : f2.vertexIndices) {
                    centroidZ2 += Panel3D.this.mesh.vertices.get(i).z;
                }
                centroidZ2 /= f2.vertexIndices.size();

                return centroidZ1 > centroidZ2 ? -1 : 1;
            }
        });

        Color backfacingColor =
            new Color(
                (4 * getBackground().getRed()   + 32) / 5,
                (4 * getBackground().getGreen() + 32) / 5,
                (4 * getBackground().getBlue()  + 32) / 5);

        for (Face f : this.mesh.faces) {
            Polygon p = new Polygon();
            for (int i : f.vertexIndices) {
                p.addPoint(
                    (int) pVertices.get(i).x,
                    (int) pVertices.get(i).y);
            }

            Vector3 n = triangleNormal(
                pVertices.get(f.vertexIndices.get(0)),
                pVertices.get(f.vertexIndices.get(1)),
                pVertices.get(f.vertexIndices.get(2)));

            // front facing
            if (n.z >= 0d) {
                // flat shading
                double light = Math.abs(this.lightDirection.dot(n));

                // draw polygon
                float[] hsbColor = Color.RGBtoHSB(
                    f.color.getRed(),
                    f.color.getGreen(),
                    f.color.getBlue(),
                    null);
                Color fillColor = new Color(
                    Color.HSBtoRGB(
                        hsbColor[0],
                        (float) (0.875 + 0.125 * light) * hsbColor[1],
                        (float) (0.875 + 0.125 * light) * hsbColor[2]));
                g2.setColor(fillColor);
                g2.fillPolygon(p);

                // draw outline
                Color outlineColor = new Color(
                    Color.HSBtoRGB(
                        hsbColor[0],
                        (float) (0.9 * (0.875 + 0.125 * light) * hsbColor[1]),
                        (float) (0.9 * (0.875 + 0.125 * light) * hsbColor[2])));
                g2.setColor(outlineColor);
                g2.drawPolygon(p);
            }

            // back facing
            else {
                g2.setColor(backfacingColor);
                g2.fillPolygon(p);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
      this.lastX = e.getX();
      this.lastY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        double angleX = (e.getY() - this.lastY) / 50d;
        double angleY = (e.getX() - this.lastX) / 50d;

        this.mesh = this.mesh.transform(
            Matrix44.rotationZ(this.cameraRotation.z).mul(
            Matrix44.rotationY(this.cameraRotation.y).mul(
            Matrix44.rotationX(this.cameraRotation.x).mul(
            Matrix44.rotationX(angleX).mul(
            Matrix44.rotationY(angleY).mul(
            Matrix44.rotationX(-this.cameraRotation.x).mul(
            Matrix44.rotationY(-this.cameraRotation.y).mul(
            Matrix44.rotationZ(-this.cameraRotation.z)))))))));

        this.lastX = e.getX();
        this.lastY = e.getY();

        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Vector3 direction = this.cameraPosition.normalized();
        Vector3 newPosition = this.cameraPosition.add(direction.mul(0.1 * e.getWheelRotation()));
        if (1.0 < newPosition.norm() && newPosition.norm() < 50.0) {
            this.cameraPosition = newPosition;
        }

        repaint();
    }
}
