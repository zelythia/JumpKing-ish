package net.zelythia.jump.Utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Utils {

    public static Font font;
    public static Color WHITE = new Color(203, 203, 203);

    static{
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("scr/main/resources/EduVICWANTBeginner-Bold.ttf"));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static int lerp(int a, int b, int f){
        return a + f * (b - a);
    }

    public static float lerp(float a, float b, float f){
        return a + f * (b - a);
    }

    public static double lerp(double a, double b, double f){
        return a + f * (b - a);
    }


}
