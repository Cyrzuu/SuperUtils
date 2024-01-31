package me.cyrzu.git.superutils.color.patterns.gradient;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GradientBuilder {

    @NotNull
    private final List<Color> colors;

    @NotNull
    private final List<GradientCalculate> gradients;

    private final int steps;

    private int step;

    public GradientBuilder(List<Color> colors, int numSteps) {
        this.colors = colors;
        this.gradients = new ArrayList<>();
        this.steps = numSteps - 1;
        this.step = 0;
        int increment = this.steps / (colors.size() - 1);
        for (int i = 0; i < colors.size() - 1; i++)
            this.gradients.add(new GradientCalculate(colors.get(i), colors.get(i + 1), increment * i, increment * (i + 1)));
    }

    @NotNull
    public Color next() {
        if (this.steps <= 1)
            return this.colors.get(0);
        int adjustedStep = (int) Math.round(Math.abs(((2 * Math.asin(Math.sin(this.step * (Math.PI / (2 * this.steps))))) / Math.PI) * this.steps));
        Color color;
        if (this.gradients.size() < 2) {
            color = this.gradients.get(0).colorAt(adjustedStep);
        } else {
            double segment = this.steps * 1d / this.gradients.size();
            int index = (int) Math.min(Math.floor(adjustedStep / segment), this.gradients.size() - 1);
            color = this.gradients.get(index).colorAt(adjustedStep);
        }

        this.step++;
        return color;
    }

}
