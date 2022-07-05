package net.zelythia.GameObjects;

import net.zelythia.Collision.CollisionType;

import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

public class Solid extends GameObject{

    public Solid(RectangularShape shape, String sprite) {
        super(shape, sprite);
        super.setCollisionType(CollisionType.COLLIDE);
    }

    public Solid(double x, double y, double width, double height, String sprite){
        super(new Rectangle2D.Double(x, y, width, height), sprite);
        super.setCollisionType(CollisionType.COLLIDE);
    }


}
