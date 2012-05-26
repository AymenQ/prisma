package com.puzzletimer.puzzles;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.puzzletimer.graphics.Face;
import com.puzzletimer.graphics.Matrix44;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.graphics.Vector3;
import com.puzzletimer.models.ColorScheme;
import com.puzzletimer.models.PuzzleInfo;
import com.puzzletimer.solvers.RubiksClockSolver;

public class RubiksClock implements Puzzle {
    @Override
    public PuzzleInfo getPuzzleInfo() {
        return new PuzzleInfo("RUBIKS-CLOCK");
    }

    @Override
    public String toString() {
        return getPuzzleInfo().getDescription();
    }

    private RubiksClockSolver.State stateFromSequence(String[] sequence) {
        Pattern pattern2 = Pattern.compile("([Ud]{4}) ([ud])=(-?\\d),([ud])=(-?\\d)");
        Pattern pattern1 = Pattern.compile("([Ud]{4}) ([ud])=(-?\\d)");
        Pattern pattern0 = Pattern.compile("([Ud]{4})");

        RubiksClockSolver.State state = RubiksClockSolver.State.id;
        for (String move : sequence) {
            // two wheels
            Matcher matcher2 = pattern2.matcher(move.toString());
            if (matcher2.find()) {
                String pins = matcher2.group(1);
                boolean[] pinsDown = new boolean[4];
                for (int i = 0; i < 4; i++) {
                    pinsDown[i] = pins.charAt(i) != 'U';
                }

                int wheel1 = -1;
                for (int i = 0; i < 4; i++) {
                    if (matcher2.group(2).charAt(0) == (pinsDown[i] ? 'd' : 'u')) {
                        wheel1 = i;
                    }
                }

                int turns1 = Integer.parseInt(matcher2.group(3));
                state = state.rotateWheel(pinsDown, wheel1, turns1);

                int wheel2 = -1;
                for (int i = 0; i < 4; i++) {
                    if (matcher2.group(4).charAt(0) == (pinsDown[i] ? 'd' : 'u')) {
                        wheel2 = i;
                    }
                }

                int turns2 = Integer.parseInt(matcher2.group(5));
                state = state.rotateWheel(pinsDown, wheel2, turns2);

                continue;
            }

            // one wheel
            Matcher matcher1 = pattern1.matcher(move.toString());
            if (matcher1.find()) {
                String pins = matcher1.group(1);
                boolean[] pinsDown = new boolean[4];
                for (int i = 0; i < 4; i++) {
                    pinsDown[i] = pins.charAt(i) != 'U';
                }

                int wheel = -1;
                for (int i = 0; i < 4; i++) {
                    if (matcher1.group(2).charAt(0) == (pinsDown[i] ? 'd' : 'u')) {
                        wheel = i;
                    }
                }

                int turns = Integer.parseInt(matcher1.group(3));
                state = state.rotateWheel(pinsDown, wheel, turns);

                continue;
            }

            // no rotation
            Matcher matcher0 = pattern0.matcher(move.toString());
            if (matcher0.find()) {
                String pins = matcher0.group(1);
                boolean[] pinsDown = new boolean[4];
                for (int i = 0; i < 4; i++) {
                    pinsDown[i] = pins.charAt(i) != 'U';
                }

                state = state.rotateWheel(pinsDown, 0, 0);
            }
        }

        return state;
    }

    private Mesh circle(double radius, int nVertices, Color color) {
        Vector3[] vertices = new Vector3[nVertices];
        for (int i = 0; i < vertices.length; i++) {
            double x = radius * Math.cos(-2 * Math.PI * i / nVertices);
            double y = radius * Math.sin(-2 * Math.PI * i / nVertices);
            vertices[i] = new Vector3(x, y, 0);
        }

        Face[] faces = {
            new Face(vertices, color),
        };

        return new Mesh(faces);
    }

    private Mesh hand(double radius1, int nVertices1, double radius2, int nVertices2, double height, Color color) {
        Vector3[] vertices = new Vector3[nVertices1 + nVertices2];
        int next = 0;

        for (int i = 0; i < nVertices1; i++) {
            double x = radius1 * Math.cos(-Math.PI * i / (nVertices1 - 1));
            double y = radius1 * Math.sin(-Math.PI * i / (nVertices1 - 1));
            vertices[next] = new Vector3(x, y, 0);
            next++;
        }

        for (int i = 0; i < nVertices2; i++) {
            double x = radius2 * Math.cos(Math.PI - Math.PI * i / (nVertices2 - 1));
            double y = radius2 * Math.sin(Math.PI - Math.PI * i / (nVertices2 - 1));
            vertices[next] = new Vector3(x, y + height, 0);
            next++;
        }

        Face[] faces = {
            new Face(vertices, color),
        };

        return new Mesh(faces);
    }

    @Override
    public Mesh getScrambledPuzzleMesh(ColorScheme colorScheme, String[] sequence) {
        RubiksClockSolver.State state = stateFromSequence(sequence);

        Mesh handBackground =
            hand(0.04, 8, 0.001, 3, 0.16, colorScheme.getFaceColor("HAND-BACKGROUND").getColor()).transform(
                Matrix44.translation(new Vector3(0, 0, -0.025)));

        Mesh handForeground =
            hand(0.025, 8, 0.001, 3, 0.11, colorScheme.getFaceColor("HAND-FOREGROUND").getColor()).transform(
                Matrix44.translation(new Vector3(0, 0, -0.05)));

        Mesh hands =
            handBackground.union(handForeground);

        // front
        Mesh front = new Mesh(new Face[0]);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Matrix44 transformation =
                    Matrix44.translation(new Vector3(0.5 * (j - 1), 0.5 * (1 - i), 0)).mul(
                    Matrix44.rotationZ(Math.PI / 6 * state.clocks[3 * i + j]));

                front = front.union(
                    circle(0.225, 32, colorScheme.getFaceColor("FRONT").getColor()).union(hands).transform(transformation));
            }
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                boolean pinDown = state.pinsDown[2 * i + j];

                Matrix44 transformation =
                    Matrix44.translation(
                        new Vector3(
                            0.5 * (j - 0.5),
                            0.5 * (0.5 - i),
                            pinDown ? 0.0 : -0.1));

                Color pinColor = colorScheme.getFaceColor(pinDown ? "PIN-DOWN" : "PIN-UP").getColor();
                front = front.union(
                    circle(0.05, 16, pinColor).transform(transformation));
            }
        }

        // back
        Mesh back = new Mesh(new Face[0]);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Matrix44 transformation =
                    Matrix44.translation(new Vector3(0.5 * (j - 1), 0.5 * (1 - i), 0)).mul(
                    Matrix44.rotationZ(Math.PI / 6 * state.clocks[9 + 3 * i + j]));

                back = back.union(
                    circle(0.225, 32, colorScheme.getFaceColor("BACK").getColor()).union(hands).transform(transformation));
            }
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                boolean pinDown = !state.pinsDown[2 * i + (1 - j)];

                Matrix44 transformation =
                    Matrix44.translation(
                        new Vector3(
                            0.5 * (j - 0.5),
                            0.5 * (0.5 - i),
                            pinDown ? 0.0 : -0.1));

                Color pinColor = colorScheme.getFaceColor(pinDown ? "PIN-DOWN" : "PIN-UP").getColor();
                back = back.union(
                    circle(0.05, 16, pinColor).transform(transformation));
            }
        }

        back = back.transform(Matrix44.rotationY(Math.PI));

        return front.transform(Matrix44.translation(new Vector3(0, 0, -0.1))).union(
            back.transform(Matrix44.translation(new Vector3(0, 0, 0.1))));
    }
}
