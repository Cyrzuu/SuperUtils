package me.cyrzu.git.superutils.color.patterns.gradient;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public record GradientCalculate(@NotNull Color startColor, @NotNull Color endColor, int lowerRange, int upperRange) {

    public Color colorAt(int step) {
        return new Color(this.calculateHexPiece(step, this.startColor.getRed(), this.endColor.getRed()),
                this.calculateHexPiece(step, this.startColor.getGreen(), this.endColor.getGreen()),
                this.calculateHexPiece(step, this.startColor.getBlue(), this.endColor.getBlue()));
    }

    private int calculateHexPiece(int step, int channelStart, int channelEnd) {
        int range = this.upperRange - this.lowerRange;
        double interval = (channelEnd - channelStart) * 1d / range;
        return Math.max(0, Math.min(255, (int) Math.round(interval * (step - this.lowerRange) + channelStart)));
    }

}
