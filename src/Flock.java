import java.util.ArrayList;
import java.util.List;

/**
 * Created by Omar on 11/12/2016.
 */
public class Flock {
    private Print p;
    public List<Boid> boids;
    public List<trail> trailPop;

    Flock(Print _p) {
        p = _p;
        boids = new ArrayList<>();
        trailPop = new ArrayList<>();
    }

    public void run() {
        for (Boid var1 : boids) {
            var1.run();
            var1.draw();
        }
    }

    public void addBoid( Boid var1) {
        boids.add(var1);
    }

    public void removeBoid( Boid var1) {
        boids.remove(var1);
    }

    public void addTrail( trail var1) {
        trailPop.add(var1);
    }

    public void removeTrail( trail var1) {
        trailPop.remove(var1);
    }
}
