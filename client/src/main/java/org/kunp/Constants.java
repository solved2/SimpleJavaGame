package org.kunp;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Constants {
    public static final int MAP_SIZE = 500;
    public static final int CELL_SIZE = 10;

    public static final int ROCK_SIZE_X = 45;
    public static final int ROCK_SIZE_Y = 30;

    public static final int PLAYER_SIZE_X = 30;
    public static final int PLAYER_SIZE_Y = 50;

    public static final int NUM_ROCKS = 8;
    public static final int ROCK_START = 1;
    public static final int ROCK = 2;

    private static final ClassLoader CLASS_LOADER = Constants.class.getClassLoader();
    public static final Image taggerImage = new ImageIcon(Objects.requireNonNull(CLASS_LOADER.getResource("tagger.png"))).getImage();
    public static final Image runawayImage = new ImageIcon(Objects.requireNonNull(CLASS_LOADER.getResource("normal.png"))).getImage();
    public static final Image rockImage = new ImageIcon(Objects.requireNonNull(CLASS_LOADER.getResource("rock.png"))).getImage();
    public static final Image fenceImage = new ImageIcon(Objects.requireNonNull(CLASS_LOADER.getResource("fence.png"))).getImage();
    public static final Image portalImage = new ImageIcon(Objects.requireNonNull(CLASS_LOADER.getResource("portal.png"))).getImage();
    public static final Image buttonImage = new ImageIcon(Objects.requireNonNull(CLASS_LOADER.getResource("button.png"))).getImage();
}
