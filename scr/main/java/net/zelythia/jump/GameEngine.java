package net.zelythia.jump;



import net.zelythia.jump.Collision.CollisionData;
import net.zelythia.jump.Collision.CollisionType;
import net.zelythia.jump.Collision.Side;
import net.zelythia.jump.Events.CollisionListener;
import net.zelythia.jump.Events.UpdateListener;
import net.zelythia.jump.GameObjects.GameObject;
import net.zelythia.jump.GameObjects.Player;
import net.zelythia.jump.GameObjects.Solid;
import net.zelythia.jump.Utils.List.List;
import net.zelythia.jump.Utils.Utils;
import net.zelythia.jump.Utils.Vector2D;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;


//Game controller = Game engine = Control
public class GameEngine implements KeyListener {

    private final Renderer renderer;

    public Player player;
    public List<Solid> solids;


    private boolean isStanding;

    //Listeners
    public static List<CollisionListener> collisionListeners;
    public static List<UpdateListener> updateListeners;


    static {
        collisionListeners = new List<CollisionListener>();
        updateListeners = new List<UpdateListener>();
    }

    public GameEngine(Renderer renderer){
        this.renderer = renderer;
        this.solids = new List<Solid>();

        initializeGameObjects();
    }

    public void initializeGameObjects()
    {
        player = new Player(new Rectangle2D.Double(200, 600, 50, 50), "scr/main/resources/player.png", 1, 20);
        collisionListeners.add(player);

        solids.add(new Solid(0, 700,480, 20, "scr/main/resources/wall.png"));

        solids.add(new Solid(300, 500, 200,200, "scr/main/resources/wall.png"));

        //Rendering the gameObjects
        renderer.addGameObject(solids.get(0));
        renderer.addGameObject(solids.get(1));
        renderer.addGameObject(player);

    }


    public void update(float deltaTime){

        for(int i = 0; i < collisionListeners.size; i++){
            updateCollisionListeners(collisionListeners.get(i));
        }

        for(int i = 0; i < updateListeners.size; i++){
            updateListeners.get(i).update(deltaTime);
        }


//=====Player movement======================================

        //Gravity
        if(player.vel.y < 10){
            player.vel.y += 1;
        }
        //Limiting the players speed
        player.vel.clamp(-player.maxSpeed, player.maxSpeed);



        checkBoundaries();

        movePlayer();

        checkBoundaries();

        //renderer.setCameraPosition(0, (int) player.getY() - 650);


        //System.out.println(player.getY());
    }


    public void checkBoundaries(){

    }

    public Shape getShapeAtPoint(double x, double y){
        for(int i = 0; i < solids.size; i++){
            if(solids.get(i).getShape().contains(x, y)) return solids.get(i).getShape();
        }
        return null;
    }


    public void movePlayer(){

        //True if the player would collide when moving in a direction
        boolean collides = false;
        isStanding = false;

        for(int i = 0; i < solids.size; i++){
            if(solids.get(i).getCollisionType() == CollisionType.COLLIDE){
                Rectangle2D bounds = solids.get(i).getShape().getBounds2D();

                RectangularShape gameObjectShape = player.getShape();

                //Extending the bounds of the Rectangle on the side to calculate collision when gamObjects are touching and not when overlapping
                if(new Rectangle2D.Double(gameObjectShape.getX(), gameObjectShape.getY(), gameObjectShape.getWidth(), gameObjectShape.getHeight()+player.vel.y).intersects(bounds)){
                    //Side = BOTTOM
                    if(player.vel.y > 0){
                        player.setPos(player.getX() + player.vel.x, bounds.getY()-player.getShape().getHeight());
                        player.vel.y = 0;
                        collides = true;
                    }

                    isStanding = true;
                }
                if(new Rectangle2D.Double(gameObjectShape.getX(), gameObjectShape.getY()+player.vel.y, gameObjectShape.getWidth(), gameObjectShape.getHeight()).intersects(bounds)){
                    //Side = TOP
                    if(player.vel.y < 0){
                        player.setPos(player.getX() + player.vel.x, bounds.getY()+bounds.getHeight());
                        player.vel.y = 0;
                        collides = true;
                    }
                }

                if(new Rectangle2D.Double(gameObjectShape.getX(), gameObjectShape.getY(), gameObjectShape.getWidth()+player.vel.x, gameObjectShape.getHeight()).intersects(bounds)){
                    //Side = RIGHT
                    if(player.vel.x > 0) {
                        player.setPos(bounds.getX()-player.getShape().getWidth(), player.getY());
                        player.vel.x = 0;
                        collides = true;
                    }
                }
                if(new Rectangle2D.Double(gameObjectShape.getX()+player.vel.x, gameObjectShape.getY(), gameObjectShape.getWidth(), gameObjectShape.getHeight()).intersects(bounds)){
                    //SIDE = LEFT
                    if(player.vel.x < 0) {
                        player.setPos(bounds.getX()+bounds.getWidth(), player.getY());
                        player.vel.x = 0;
                        collides = true;
                    }
                }
            }
        }

        if(collides){
            //Simulating friction:
            player.vel.x = Utils.lerp(player.vel.x, 0, .5f);
            player.vel.y = Utils.lerp(player.vel.y, 0, .1f);
        }
        else{
            player.setPos(player.getX() + player.vel.x, player.getY() + player.vel.y);
        }

    }

    public void updateCollisionListeners(CollisionListener listener){
        //TODO make new list and add all future game Objects
        for(int i = 0; i < solids.size; i++){
            Rectangle2D bounds = solids.get(i).getShape().getBounds2D();

            if(listener instanceof GameObject gameObject){
                RectangularShape gameObjectShape = gameObject.getShape();

                //Extending the bounds of the Rectangle on the side to calculate collision when gamObjects are touching and not when overlapping
                if(gameObjectShape.intersects(bounds)){
                    listener.onCollision(new CollisionData(solids.get(i), Side.BOTTOM));
                }
                if(gameObjectShape.intersects(bounds)){
                    listener.onCollision(new CollisionData(solids.get(i), Side.RIGHT));
                }
                if(gameObjectShape.intersects(bounds)){
                    listener.onCollision(new CollisionData(solids.get(i), Side.TOP));
                }
                if(gameObjectShape.intersects(bounds)){
                    listener.onCollision(new CollisionData(solids.get(i), Side.LEFT));
                }
            }
        }
    }

    public void render(Graphics graphics){
        renderer.render(graphics);
    }



//=======EVENTS=========================================================================================================

    Vector2D storedVel = new Vector2D(0,0);

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(isStanding){
            if(e.getKeyCode() == KeyEvent.VK_W){
                storedVel.y -= 10;
            }

            if(e.getKeyCode() == KeyEvent.VK_A){
                storedVel.x -= 5;
            }

            if(e.getKeyCode() == KeyEvent.VK_D){
                storedVel.x += 5;
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(isStanding){
            player.vel.add(storedVel);
            storedVel = new Vector2D(0,0);
        }
    }
}