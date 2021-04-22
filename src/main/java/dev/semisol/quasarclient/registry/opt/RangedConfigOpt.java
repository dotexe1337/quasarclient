package dev.semisol.quasarclient.registry.opt;

import dev.semisol.quasarclient.registry.ConfigOpt;

public abstract class RangedConfigOpt<T> extends ConfigOpt<T> {
    private final boolean bounded;
    private final T low;
    private final T high;
    public RangedConfigOpt(String name, T v, T low, T high, boolean b) {
        super(name, v);
        this.bounded = b;
        this.low = low;
        this.high = high;
    }
    public T getLowBound(){
        return low;
    };
    public T getHighBound(){
        return high;
    }
    public boolean bounded(){
        return bounded;
    }
}
