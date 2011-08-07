package com.puzzletimer.tips;


public class TipProvider {
    private Tip[] tips;

    public TipProvider() {
        this.tips = new Tip[] {
            new RubiksCubeOptimalCross(),
            new RubiksCubeOptimalXCross(),
            new RubiksCube3OPCycles(),
            new RubiksCubeClassicPochmannEdges(),
            new RubiksCubeClassicPochmannCorners(),
            new RubiksCubeM2Edges(),
            new Square1OptimalCubeShapeTip(),
        };
    }

    public Tip[] getAll() {
        return this.tips;
    }

    public Tip get(String tipId) {
        for (Tip tip : this.tips) {
            if (tip.getTipId().equals(tipId)) {
                return tip;
            }
        }

        return null;
    }
}
