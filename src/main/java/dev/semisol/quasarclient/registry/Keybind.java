package dev.semisol.quasarclient.registry;

public class Keybind {
    public int key = -1;
    public final Runnable runnable;
    public boolean held = false;
    public final String name;
    public Keybind(Runnable runnable, String name) {
        this.runnable = runnable;
        this.name = name;
    }
}
