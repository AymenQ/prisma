package com.puzzletimer;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.KeyStroke;

import com.puzzletimer.geometry.Plane;
import com.puzzletimer.graphics.HSLColor;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.linearalgebra.Matrix33;
import com.puzzletimer.scrambles.MegaminxMove;
import com.puzzletimer.scrambles.MegaminxRandomScrambler;
import com.puzzletimer.scrambles.Move;
import com.puzzletimer.scrambles.Scramble;
import com.puzzletimer.scrambles.Scrambler;

public class Megaminx implements Puzzle {
    private Scrambler scrambler;

    public Megaminx() {
        scrambler = new MegaminxRandomScrambler();
    }

    @Override
    public String getName() {
        return "Megaminx";
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_M;
    }

    @Override
    public KeyStroke getAccelerator() {
        return KeyStroke.getKeyStroke('M', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    }

    @Override
    public boolean isDefaultPuzzle()
    {
        return false;
    }

    @Override
    public Scrambler getScrambler()
    {
        return scrambler;
    }

    private static class Twist {
        public Plane plane;
        public double angle;

        public Twist(Plane plane, double angle) {
            this.plane = plane;
            this.angle = angle;
        }
    }

    @Override
    public Mesh getMesh(Scramble s)
    {
        HSLColor[] colors = {
            new HSLColor(200,  90,  50), // light blue
            new HSLColor( 20, 100,  50), // orange
            new HSLColor(  0,  85,  45), // red
            new HSLColor( 30, 100,  30), // brown
            new HSLColor(120, 100,  30), // green
            new HSLColor(330,  90,  70), // pink
            new HSLColor(275,  90,  50), // purple
            new HSLColor(  0,   0, 100), // white
            new HSLColor(130, 100,  50), // light green
            new HSLColor( 55, 100,  50), // yellow
            new HSLColor(180,  90,  50), // cyan
            new HSLColor(235, 100,  30), // blue
        };

        Mesh mesh = Mesh.dodecahedron(colors)
            .shortenFaces(0.025);

        Plane[] planes = new Plane[mesh.faces.size()];
        for (int i = 0; i < mesh.faces.size(); i++) {
            Plane p = Plane.fromVectors(
                    mesh.vertices.get(mesh.faces.get(i).vertexIndices.get(0)),
                    mesh.vertices.get(mesh.faces.get(i).vertexIndices.get(1)),
                    mesh.vertices.get(mesh.faces.get(i).vertexIndices.get(2)));
            planes[i] = new Plane(p.p.sub(p.n.mul(0.25)), p.n);
        }

        for (Plane plane : planes) {
            mesh = mesh.cut(plane, 0.03);
        }

        mesh = mesh
            .softenFaces(0.01)
            .softenFaces(0.005);

        Plane planeR = new Plane(planes[3].p, planes[3].n.neg());
        Plane planeD = new Plane(planes[7].p, planes[7].n.neg());
        Plane planeU = planes[7];

        HashMap<Move, Twist> twists = new HashMap<Move, Twist>();
        twists.put(MegaminxMove.R2, new Twist(planeR,  4 * Math.PI / 5));
        twists.put(MegaminxMove.R7, new Twist(planeR, -4 * Math.PI / 5));
        twists.put(MegaminxMove.D2, new Twist(planeD,  4 * Math.PI / 5));
        twists.put(MegaminxMove.D7, new Twist(planeD, -4 * Math.PI / 5));
        twists.put(MegaminxMove.U,  new Twist(planeU,  2 * Math.PI / 5));
        twists.put(MegaminxMove.U6, new Twist(planeU, -2 * Math.PI / 5));

        for (Move move : s.moves) {
            Twist t = twists.get(move);
            mesh = mesh.transformHalfspace(
                Matrix33.rotation(t.plane.n, t.angle),
                t.plane);
        }

        return mesh
            .transform(Matrix33.rotationY(Math.PI / 16));
    }
}
