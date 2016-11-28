import java.util.ArrayList;
import java.util.List;

/**
 * Created by Omar on 11/12/2016.
 */
public class Flock {
    private Print p;
    public List<Boid> b;
    public List<trail> t;

    Flock(Print _p) {
        p = _p;
        b = new ArrayList<>();
        t = new ArrayList<>();
    }

    public void r() {
        for (Boid var1 : b) {
            var1.r();
            var1.d();
        }
    }

    public void aB( Boid var1) {
        b.add(var1);
    }

    public void rB( Boid var1) {
        b.remove(var1);
    }

    public void aT( trail var1) {
        t.add(var1);
    }

    public void rt( trail var1) {
        t.remove(var1);
    }
}
