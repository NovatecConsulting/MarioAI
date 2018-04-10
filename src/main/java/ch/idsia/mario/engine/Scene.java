package ch.idsia.mario.engine;

import java.awt.Graphics;


public abstract class Scene
{
    public static boolean[] keys = new boolean[16];
    public static final String[] keysStr = {"LEFT  ", "RIGHT ", " DOWN ", " JUMP ", " SPEED"};    

    public abstract void init();

    public abstract void tick();

    public abstract void render(Graphics og, float alpha);
}