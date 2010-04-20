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

import com.puzzletimer.linearalgebra.Matrix33;
import com.puzzletimer.linearalgebra.Vector3;

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
        mesh = new Mesh(new ArrayList<Vector3>(), new ArrayList<Face>());
        lightDirection = new Vector3(0d, 0.25d, -1d).normalized();
        viewerPosition = new Vector3(0d, 0d, -325d);
        cameraPosition = new Vector3(0d, 0d, -3d);
        cameraRotation = new Vector3(0d, 0d, 0d);

        lastX = 0;
        lastY = 0;

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    private Vector3 toCameraCoordinates(Vector3 v) {
        return Matrix33.rotationX(-cameraRotation.x).mul(
               Matrix33.rotationY(-cameraRotation.y).mul(
               Matrix33.rotationZ(-cameraRotation.z).mul(
               v.sub(cameraPosition))));
    }

    private Vector3 perspectiveProjection(Vector3 v) {
        return new Vector3(
            (getWidth() / 2d) + (-v.x - viewerPosition.x) * (viewerPosition.z / v.z),
            (getHeight() / 2d) + (v.y - viewerPosition.y) * (viewerPosition.z / v.z),
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
        final ArrayList<Vector3> pVertices = new ArrayList<Vector3>();
        for (Vector3 v : mesh.vertices) {
            pVertices.add(perspectiveProjection(toCameraCoordinates(v)));
        }

        // separate front from back facing polygons
        ArrayList<Face> frontFaces = new ArrayList<Face>();
        ArrayList<Face> backFaces = new ArrayList<Face>();
        for (Face f : mesh.faces) {
            Vector3 n = triangleNormal(
                pVertices.get(f.vertexIndices.get(0)),
                pVertices.get(f.vertexIndices.get(1)),
                pVertices.get(f.vertexIndices.get(2)));

            if (n.z >= 0d) {
                frontFaces.add(f);
            } else {
                backFaces.add(f);
            }
        }

        // draw backfacing polygons
        g2.setColor(new Color(0.1f, 0.1f, 0.1f, 0.2f));
        for (Face f : backFaces) {
            Polygon p = new Polygon();
            for (int i : f.vertexIndices) {
                p.addPoint(
                        (int) pVertices.get(i).x,
                        (int) pVertices.get(i).y);
            }

            g2.fillPolygon(p);
        }

        // draw frontfacing polygons

        // painter's algorithm
        Collections.sort(frontFaces, new Comparator<Face>() {
            @Override
            public int compare(Face f1, Face f2) {
                double centroidZ1 = 0d;
                for (int i : f1.vertexIndices) {
                    centroidZ1 += mesh.vertices.get(i).z;
                }
                centroidZ1 /= f1.vertexIndices.size();

                double centroidZ2 = 0d;
                for (int i : f2.vertexIndices) {
                    centroidZ2 += mesh.vertices.get(i).z;
                }
                centroidZ2 /= f2.vertexIndices.size();

                return centroidZ1 > centroidZ2 ? -1 : 1;
            }
        });

        for (Face f : frontFaces) {
            Polygon p = new Polygon();
            for (int i : f.vertexIndices) {
                p.addPoint(
                        (int) pVertices.get(i).x,
                        (int) pVertices.get(i).y);
            }

            // flat shading
            Vector3 n = triangleNormal(
                    pVertices.get(f.vertexIndices.get(0)),
                    pVertices.get(f.vertexIndices.get(1)),
                    pVertices.get(f.vertexIndices.get(2)));
            double light = Math.abs(lightDirection.dot(n));

            // draw polygon
            HSLColor fillColor = new HSLColor(
                f.color.hue,
                (int) ((0.9 + 0.1 * light) * (int) f.color.saturation),
                (int) ((0.8 + 0.2 * light) * (int) f.color.luminance));
            g2.setColor(fillColor.toColor());
            g2.fillPolygon(p);

            // draw outline
            HSLColor outlineColor = new HSLColor(
                fillColor.hue,
                (int) (0.9 * fillColor.saturation),
                (int) (0.9 * fillColor.luminance));
            g2.setColor(outlineColor.toColor());
            g2.drawPolygon(p);
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
      lastX = e.getX();
      lastY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        double angleX = (e.getY() - lastY) / 50d;
        double angleY = (e.getX() - lastX) / 50d;

        mesh = mesh.transform(
            Matrix33.rotationZ(cameraRotation.z).mul(
            Matrix33.rotationY(cameraRotation.y).mul(
            Matrix33.rotationX(cameraRotation.x).mul(
            Matrix33.rotationX(angleX).mul(
            Matrix33.rotationY(angleY).mul(
            Matrix33.rotationX(-cameraRotation.x).mul(
            Matrix33.rotationY(-cameraRotation.y).mul(
            Matrix33.rotationZ(-cameraRotation.z)))))))));

        lastX = e.getX();
        lastY = e.getY();

        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Vector3 direction = cameraPosition.normalized();
        Vector3 newPosition = cameraPosition.add(direction.mul(0.1 * e.getWheelRotation()));
        if (1.0 < newPosition.norm() && newPosition.norm() < 50.0) {
          cameraPosition = newPosition;
        }

        repaint();
    }
}
