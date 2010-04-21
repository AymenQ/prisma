package com.puzzletimer;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.KeyStroke;

import com.puzzletimer.geometry.Plane;
import com.puzzletimer.graphics.HSLColor;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.linearalgebra.Matrix33;
import com.puzzletimer.linearalgebra.Vector3;
import com.puzzletimer.scrambles.CubeMove;
import com.puzzletimer.scrambles.Move;
import com.puzzletimer.scrambles.ProfessorsCubeRandomScrambler;
import com.puzzletimer.scrambles.Scramble;
import com.puzzletimer.scrambles.Scrambler;

public class ProfessorsCube implements Puzzle {
    private Scrambler scrambler;

    public ProfessorsCube() {
        scrambler = new ProfessorsCubeRandomScrambler(60);
    }

    @Override
    public String getName() {
        return "5x5x5 Cube";
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_5;
    }

    @Override
    public KeyStroke getAccelerator() {
        return KeyStroke.getKeyStroke('5', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
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

    private class Twist {
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
            new HSLColor( 20, 100,  50), // L - orange
            new HSLColor(235, 100,  30), // B - blue
            new HSLColor( 55, 100,  50), // D - yellow
            new HSLColor(  0,  85,  45), // R - red
            new HSLColor(120, 100,  30), // F - green
            new HSLColor(  0,   0, 100), // U - white
        };

        Mesh mesh = Mesh.cube(colors);

        Plane planeL  = new Plane(new Vector3(-0.3, 0, 0), new Vector3(-1, 0, 0));
        Plane planeLw = new Plane(new Vector3(-0.1, 0, 0), new Vector3(-1, 0, 0));
        Plane planeR  = new Plane(new Vector3( 0.3, 0, 0), new Vector3( 1, 0, 0));
        Plane planeRw = new Plane(new Vector3( 0.1, 0, 0), new Vector3( 1, 0, 0));
        Plane planeD  = new Plane(new Vector3(0, -0.3, 0), new Vector3(0, -1, 0));
        Plane planeDw = new Plane(new Vector3(0, -0.1, 0), new Vector3(0, -1, 0));
        Plane planeU  = new Plane(new Vector3(0,  0.3, 0), new Vector3(0,  1, 0));
        Plane planeUw = new Plane(new Vector3(0,  0.1, 0), new Vector3(0,  1, 0));
        Plane planeF  = new Plane(new Vector3(0, 0, -0.3), new Vector3(0, 0, -1));
        Plane planeFw = new Plane(new Vector3(0, 0, -0.1), new Vector3(0, 0, -1));
        Plane planeB  = new Plane(new Vector3(0, 0,  0.3), new Vector3(0, 0,  1));
        Plane planeBw = new Plane(new Vector3(0, 0,  0.1), new Vector3(0, 0,  1));

        mesh = mesh
            .cut(planeL,  0)
            .cut(planeR,  0)
            .cut(planeLw, 0)
            .cut(planeRw, 0)
            .cut(planeD,  0)
            .cut(planeU,  0)
            .cut(planeDw, 0)
            .cut(planeUw, 0)
            .cut(planeF,  0)
            .cut(planeB,  0)
            .cut(planeFw, 0)
            .cut(planeBw, 0)
            .shortenFaces(0.02)
            .softenFaces(0.015)
            .softenFaces(0.005);

        HashMap<Move, Twist> twists = new HashMap<Move, Twist>();
        twists.put(CubeMove.L,   new Twist(planeL,   Math.PI / 2));
        twists.put(CubeMove.Lw,  new Twist(planeLw,  Math.PI / 2));
        twists.put(CubeMove.L2,  new Twist(planeL,   Math.PI));
        twists.put(CubeMove.Lw2, new Twist(planeLw,  Math.PI));
        twists.put(CubeMove.L3,  new Twist(planeL,  -Math.PI / 2));
        twists.put(CubeMove.Lw3, new Twist(planeLw, -Math.PI / 2));
        twists.put(CubeMove.R,   new Twist(planeR,   Math.PI / 2));
        twists.put(CubeMove.Rw,  new Twist(planeRw,  Math.PI / 2));
        twists.put(CubeMove.R2,  new Twist(planeR,   Math.PI));
        twists.put(CubeMove.Rw2, new Twist(planeRw,  Math.PI));
        twists.put(CubeMove.R3,  new Twist(planeR,  -Math.PI / 2));
        twists.put(CubeMove.Rw3, new Twist(planeRw, -Math.PI / 2));
        twists.put(CubeMove.D,   new Twist(planeD,   Math.PI / 2));
        twists.put(CubeMove.Dw,  new Twist(planeDw,  Math.PI / 2));
        twists.put(CubeMove.D2,  new Twist(planeD,   Math.PI));
        twists.put(CubeMove.Dw2, new Twist(planeDw,  Math.PI));
        twists.put(CubeMove.D3,  new Twist(planeD,  -Math.PI / 2));
        twists.put(CubeMove.Dw3, new Twist(planeDw, -Math.PI / 2));
        twists.put(CubeMove.U,   new Twist(planeU,   Math.PI / 2));
        twists.put(CubeMove.Uw,  new Twist(planeUw,  Math.PI / 2));
        twists.put(CubeMove.U2,  new Twist(planeU,   Math.PI));
        twists.put(CubeMove.Uw2, new Twist(planeUw,  Math.PI));
        twists.put(CubeMove.U3,  new Twist(planeU,  -Math.PI / 2));
        twists.put(CubeMove.Uw3, new Twist(planeUw, -Math.PI / 2));
        twists.put(CubeMove.F,   new Twist(planeF,   Math.PI / 2));
        twists.put(CubeMove.Fw,  new Twist(planeFw,  Math.PI / 2));
        twists.put(CubeMove.F2,  new Twist(planeF,   Math.PI));
        twists.put(CubeMove.Fw2, new Twist(planeFw,  Math.PI));
        twists.put(CubeMove.F3,  new Twist(planeF,  -Math.PI / 2));
        twists.put(CubeMove.Fw3, new Twist(planeFw, -Math.PI / 2));
        twists.put(CubeMove.B,   new Twist(planeB,   Math.PI / 2));
        twists.put(CubeMove.Bw,  new Twist(planeBw,  Math.PI / 2));
        twists.put(CubeMove.B2,  new Twist(planeB,   Math.PI));
        twists.put(CubeMove.Bw2, new Twist(planeBw,  Math.PI));
        twists.put(CubeMove.B3,  new Twist(planeB,  -Math.PI / 2));
        twists.put(CubeMove.Bw3, new Twist(planeBw, -Math.PI / 2));

        for (Move move : s.moves) {
            Twist t = twists.get(move);
            mesh = mesh.transformHalfspace(
                Matrix33.rotation(t.plane.n, t.angle),
                t.plane);
        }

        return mesh
            .transform(Matrix33.rotationY(-Math.PI / 6))
            .transform(Matrix33.rotationX(Math.PI / 7));
    }
}
