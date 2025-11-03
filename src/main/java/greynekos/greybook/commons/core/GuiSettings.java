package greynekos.greybook.commons.core;

import java.io.Serializable;
import java.util.Objects;

import greynekos.greybook.commons.util.ToStringBuilder;

/**
 * A Serializable class that contains the GUI settings. Guarantees: immutable.
 */
public class GuiSettings implements Serializable {

    public static final double DEFAULT_HEIGHT = 600;
    public static final double DEFAULT_WIDTH = 800;
    public static final double DEFAULT_X = 50;
    public static final double DEFAULT_Y = 50;
    public static final boolean DEFAULT_MAXIMIZED = false;

    private final double windowWidth;
    private final double windowHeight;
    private final double windowX;
    private final double windowY;
    private final boolean isMaximized;

    /**
     * Constructs a {@code GuiSettings} with the default height, width and position.
     */
    public GuiSettings() {
        windowWidth = DEFAULT_WIDTH;
        windowHeight = DEFAULT_HEIGHT;
        windowX = DEFAULT_X;
        windowY = DEFAULT_Y;
        isMaximized = DEFAULT_MAXIMIZED;
    }

    /**
     * Constructs a {@code GuiSettings} with the specified height, width and
     * position.
     */
    public GuiSettings(double windowWidth, double windowHeight, double xPosition, double yPosition,
            boolean isMaximized) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.windowX = xPosition;
        this.windowY = yPosition;
        this.isMaximized = isMaximized;
    }

    public double getWindowWidth() {
        return windowWidth;
    }

    public double getWindowHeight() {
        return windowHeight;
    }

    public double getWindowX() {
        return windowX;
    }

    public double getWindowY() {
        return windowY;
    }

    public boolean getIsMaximized() {
        return isMaximized;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof GuiSettings)) {
            return false;
        }

        GuiSettings otherGuiSettings = (GuiSettings) other;
        return windowWidth == otherGuiSettings.windowWidth && windowHeight == otherGuiSettings.windowHeight
                && windowX == otherGuiSettings.windowX && windowY == otherGuiSettings.windowY
                && isMaximized == otherGuiSettings.isMaximized;
    }

    @Override
    public int hashCode() {
        return Objects.hash(windowWidth, windowHeight, windowX, windowY);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("windowWidth", windowWidth).add("windowHeight", windowHeight)
                .add("windowX", windowX).add("windowY", windowY).add("isMaximized", isMaximized).toString();
    }
}
