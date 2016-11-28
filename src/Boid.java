import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Omar on 11/12/2016.
 */
public class Boid extends Vec3D {
    private Print p;
    private Vec3D vel;
    private Vec3D acc;
    private Vec3D nod = null;
    private Vec3D nod2 = null;
    private Vec3D sep = null;
    private Vec3D ali = null;
    private Vec3D coh = null;
    private Vec3D stig = null;
    private Vec3D alitr = null;
    private Vec3D vert = null;
    private Vec3D noise;
    private Vec3D cra = null;

    private Boid stigboid = null;

    private meshvertices go = null;
    private meshvertices go1 = null;

    private List<List<trail>> trailpop;

    private float maxforce;
    private float maxspeed;
    private boolean reflect = true;
    private boolean print = false;
    private boolean node = false;
    private boolean node2 = false;
    private boolean stigfollow = false;
    private boolean craw = false;
    private int cracnt = 0;
    private boolean pnoise = false;

    private int printcnt;
    public int type;
    private int trno = -1;
    private int thinkTimer = 0;

    private List friends;

    Boid(Print _p, Vec3D pos, int _type) {
        super(pos);
        p = _p;
        vel = new Vec3D(p.random(-p.TWO_PI, p.TWO_PI), p.random(-p.TWO_PI, p.TWO_PI), p.random(-p.TWO_PI, p.TWO_PI));
        type = _type;
        acc = new Vec3D(0, 0, 0);
        maxspeed = 4;
        if((type==7)&&(type==8))maxspeed = 1;
        maxforce = 0.77f;
        if((type==7)&&(type==8))maxforce = 0.44f;
        trailpop = new ArrayList<>();
        friends = new ArrayList<>();
        thinkTimer = (int) (p.random(10));
    }

    public void run() {
        increment();
        rules(this);
        if (thinkTimer == 0) {
            if (!p.boidoctre) getFriends(this.type, 100);
            if (p.boidoctre) friends = p.boidoctree.getPointsWithinSphere(this.copy(), 120);
        }
        flock();
        if ((p.frameCount % 3 == 0) && (p.frameCount > 20) && (print)) trail();
        update();
        borders();
    }

    private void increment() {
        thinkTimer = (thinkTimer + 1) % 5;
    }

    private void getFriends(int var1, int var2) {
        List<Boid> var3 = new ArrayList<>();
        for (int i = 0; i < p.flock.boids.size(); i++) {
            Boid var4 = p.flock.boids.get(i);
            if (var4 == this) continue;
            if (var4.type == var1) {
                if (p.abs(var4.x - this.x) < var2 &&
                        p.abs(var4.y - this.y) < var2 &&
                        p.abs(var4.z - this.z) < var2) {
                    var3.add(var4);
                }
            }
        }
        friends = var3;
    }

    private void rules(Boid a) {

        if ((a.type == 6) && (p.frameCount < 12)) {
            trailpop.add(new ArrayList<trail>());
        }

        if (a.type == 1) {
            a.node = true;
            a.print = false;
            a.go1 = null;
            if (a.go == null) {
                a.go = seekclosestptless(0, 80);
            }
        }

        if (a.type == 2) {
            a.node2 = true;
            a.print = false;
            if (a.go == null) {
                a.go = seekclosestpt(1);
            }
        }

        if (a.type == 3) {
            a.print = true;
            a.go = null;
            if (a.go1 == null) {
                a.go1 = seekclosestptmorerange(0, 110, 700);
            }
        }

        if (a.type == 4) {
            a.print = true;
            a.stigfollow = true;
            a.go = null;
        }

        if ((a.printcnt > 40) && (a.type == 3)) {
            a.type = 1;
            a.print = false;
            a.printcnt = 0;
            if(a.cracnt>30){
                a.craw = false;
                a.cracnt = 0;
            }

        }

        if ((a.printcnt > 30) && (a.type == 4)) {
            a.type = 2;
            a.print = false;
            a.printcnt = 0;

        }

    }

    private void flockrules(Boid a) {

        if (a.type == 2) {
            if ((a.node2) && (p.start2)) a.nod2.scaleSelf(0.7f);
            a.sep.scaleSelf(0.3f);
            a.ali.scaleSelf(0.1f);
            a.coh.scaleSelf(0.07f);
        }

        if (a.type == 4) {
            a.sep.scaleSelf(0.4f);
            a.ali.scaleSelf(0.05f);
            a.coh.scaleSelf(0.007f);
            if (stigfollow) {
                a.stig.scaleSelf(0.7f);
                a.alitr.scaleSelf(0.05f);
            }
        }

        if (a.type == 3) {
            a.sep.scaleSelf(0.15f);
            a.ali.scaleSelf(0.03f);
            a.coh.scaleSelf(0.005f);
            a.nod.scaleSelf(0f);
            a.vert.scaleSelf(0.202f);
            if ((p.makecorridor) && (craw)) a.cra.scaleSelf(0.4f);
            if (pnoise) a.noise.scaleSelf(0.01f);
        }

        if (a.type == 1) {
            if (a.node) a.nod.scaleSelf(0.71f);
            a.sep.scaleSelf(0.3f);
            a.ali.scaleSelf(0.05f);
            a.coh.scaleSelf(0.02f);
        }

        if ((a.type == 7) && (a.type == 8)) {
            a.sep.scaleSelf(0.7f);
            a.ali.scaleSelf(0.5f);
            a.coh.scaleSelf(0.2f);
        }

    }

    private void flock() {


        if ((type == 1) || (type == 3)) {
            sep = separate(friends, 90.0f);
            ali = align(friends, 40.0f);
            coh = cohesion(friends, 40.0f);
        }

        if ((type == 2) || (type == 4)) {
            sep = separate(friends, 30.0f);
            ali = align(friends, 40.0f);
            coh = cohesion(friends, 30.0f);
        }

        if ((node) && (type == 1)) {
            nod = vertexseek();

        }

        if (type == 3) {
            vert = edgeseek();
            if ((p.makecorridor) && (craw)) {
                cra = crawl(p.corridor, 80, 2);
                cracnt++;
            }
            if (pnoise) noise = new Vec3D(p.random(2) - 1, p.random(2) - 1, p.random(2) - 1);
        }

        if ((node2) && (type == 2) && (p.start2)) {
            nod2 = vertexseek1();
        }

        if ((type == 4)) {
            stig = seektrail(p.flock.trailPop, 50.0f);
            alitr = aligntrail(p.flock.trailPop, 50.0f);
        }

        if ((type == 7) || (type == 8)) {
            sep = separate(friends, 90.0f);
            ali = align(friends, 40.0f);
            coh = cohesion(friends, 40.0f);
        }

        flockrules(this);

        applyForce(sep);
        applyForce(ali);
        applyForce(coh);

        if ((node) && (type == 1)) {
            applyForce(nod);
        }

        if ((node2) && (type == 2) && (p.start2)) {
            applyForce(nod2);
        }

        if ((type == 4)) {
            applyForce(stig);
            applyForce(alitr);
        }

        if (type == 3) {
            applyForce(vert);
            if ((p.makecorridor) && (craw)) applyForce(cra);
            if (pnoise) applyForce(noise);
        }
    }

    private void update() {
        vel.addSelf(acc);
        vel.limit(maxspeed);
        this.addSelf(vel);
        acc.scaleSelf(0);
    }

    private void applyForce(Vec3D var1) {
        acc.addSelf(var1);
    }

    private Vec3D seek(Vec3D var1) {
        Vec3D var2 = var1.subSelf(this.copy());
        var2.normalize();
        var2.scaleSelf(maxspeed);
        Vec3D var3 = var2.subSelf(vel);
        var3.limit(maxforce);
        return var3;
    }

    private void trail() {
        Vec3D var1 = new Vec3D((int) this.copy().x, (int) this.copy().y, (int) this.copy().z);
        Vec3D var2 = new Vec3D((int) this.vel.copy().x, (int) this.vel.copy().y, (int) this.vel.copy().z);
        trail var3 = new trail(p, var1, var2);
        trailpop.get(trno).add(var3);
        if (type == 3) {
            p.flock.addTrail(var3);
        }
        printcnt++;
    }

    public void draw() {
        float var1 = vel.headingXY() + p.radians(90);
        p.stroke(255);
        p.pushMatrix();
        p.translate(x, y, z);
        p.rotate(var1);
        if ((type == 1) || (type == 3)) p.obj.setFill(p.color(0, 0, 255));
        if ((type == 2) || (type == 4)) p.obj.setFill(p.color(255, 255, 255));
        if (type == 7) p.obj.setFill(p.color(255, 0, 255));
        if (type == 8) p.obj.setFill(p.color(255, 0, 0));
        if (type == 6) p.obj.setFill(p.color(0, 255, 0));
        p.obj.setStroke(100);
        p.obj.scale(1);
        p.shape(p.obj);
        p.popMatrix();

        p.noFill();
        p.strokeWeight(2);

        for (int i = 0; i < trailpop.size(); i++) {
            List<trail> var2 = trailpop.get(i);
            p.beginShape();
            for (int j = 0; j < var2.size(); j++) {
                trail var3 = var2.get(j);

                if ((type == 4) || (type == 2)) {
                    float var4 = PApplet.map(j, 0, var3.trailNo, 0, 1);
                    int var5 = p.color(125, 60, 100, 255);
                    int var6 = p.color(200, 255, 50, 255);
                    int var7 = p.lerpColor(var5, var6, var4);
                    p.stroke(var7);
                }

                if ((type == 3) || (type == 1)) {
                    float var4 = PApplet.map(j, 0, var3.trailNo, 0, 1);
                    int var5 = p.color(255, 255, 255, 255);
                    int var6 = p.color(255, 255, 255, 255);
                    int var7 = p.lerpColor(var5, var6, var4);
                    p.stroke(var7);
                }

                if ((type == 6)) {
                    float var4 = PApplet.map(j, 0, var3.trailNo, 0, 1);
                    int var5 = p.color(255, 0, 0, 255);
                    int var6 = p.color(255, 10, 10, 255);
                    int var7 = p.lerpColor(var5, var6, var4);
                    p.stroke(var7);
                }

                p.curveVertex(var3.x, var3.y, var3.z);
            }
            p.endShape();
        }

    }

    private meshvertices seekclosestpt(int var1) {
        meshvertices var2 = null;
        float var3 = 3.4028235E38F;
        for (int i = 0; i < p.vertexpop.size(); i++) {
            meshvertices var4 = p.vertexpop.get(i);
            if (var4.taken == var1) {
                float var6 = var4.distanceTo(this.copy());
                if (var6 < var3) {
                    var2 = var4;
                    var3 = var6;
                }
            }
        }
        return var2;
    }

    private meshvertices seekclosestptless(int var1, int var2) {
        meshvertices var3 = null;
        float var4 = 3.4028235E38F;
        for (int i = 0; i < p.vertexpop.size(); i++) {
            meshvertices var5 = p.vertexpop.get(i);
            if (var5.taken == var1) {
                if (var5.slope < var2) {
                    float var6 = var5.distanceTo(this.copy());
                    if (var6 < var4) {
                        var3 = var5;
                        var4 = var6;
                    }
                }
            }
        }
        return var3;
    }

    private meshvertices seekclosestptmore(int var1, int var2) {
        meshvertices var3 = null;
        float var4 = 3.4028235E38F;
        for (int i = 0; i < p.vertexpop.size(); i++) {
            meshvertices var5 = p.vertexpop.get(i);
            if (var5.taken == var1) {
                if (var5.slope > var2) {
                    float var6 = var5.distanceTo(this.copy());
                    if (var6 < var4) {
                        var3 = var5;
                        var4 = var6;
                    }
                }
            }
        }
        return var3;
    }

    private meshvertices seekclosestptmorerange(int var1, int var2, int var3) {
        meshvertices var4 = null;
        float var5 = 3.4028235E38F;
        for (int i = 0; i < p.vertexpop.size(); i++) {
            meshvertices var6 = p.vertexpop.get(i);
            if (var6.taken == var1) {
                if (var6.slope > var2) {
                    float var7 = var6.distanceTo(this.copy());
                    if ((var7 < var5) && (var7 > var3)) {
                        var4 = var6;
                        var5 = var7;
                    }
                }
            }
        }
        return var4;
    }

    private Vec3D vertexseek() {
        if (go == null) {
            go = seekclosestptless(0, 50);
        }
        float var1 = go.distanceToSquared(this);
        if (var1 < 64 * 64) {
            type = 3;
            craw = true;
            p.start2 = true;
            node = false;
            trailpop.add(new ArrayList<trail>());
            trno++;
            go.taken = 1;
        }
        return seek(go.copy());
    }

    private Vec3D crawl(WETriangleMesh var1, int var2, int var3) {
        Vec3D var4 = vel.copy();
        var4.normalize();
        var4.scaleSelf(var2);
        Vec3D var5 = this.copy().addSelf(var4);
        Vec3D var6 = var1.getClosestVertexToPoint(var5);
        Vec3D var7 = var6.sub(var5);
        if (var7.magSquared() < var3) {
            Vec3D var8 = new Vec3D(0, 0, 0);
            var8.scaleSelf(3.0f);
            return var8;
        } else {
            var7.normalize();
            var7.scaleSelf(3.0f);
            return var7;
        }
    }

    private Vec3D edgeseek() {
        if (go1 == null) {
            go1 = seekclosestptmorerange(0, 110, 200);
        }
        float var1 = go1.distanceToSquared(this);
        if (var1 < 64 * 64) {
            type = 1;
            node = true;
            go1.taken = 3;
        }
        return seek(go1.copy());
    }

    private Vec3D vertexseek1() {

        Vec3D var1 = new Vec3D(0, 0, 0);

        if (go == null) {
            go = seekclosestpt(1);
        }

        if (go != null) {
            float var2 = go.distanceToSquared(this);

            if (var2 < 500 * 500) {
                var1 = seek(go.copy());
            } else go = null;

            if (var2 < 55 * 55) {
                type = 4;
                trailpop.add(new ArrayList<trail>());
                trno++;
                node2 = false;
                go.takencnt++;
                stigboid = go.boid;
                meshvertices var3 = seekclosestpt(0);
                var3.taken = 3;
            }
        }
        return var1;
    }

    private Vec3D separate(List<Boid> var1, float var2) {
        float var3 = var2 * var2;
        Vec3D var4 = new Vec3D(0, 0, 0);
        int var5 = 0;
        for (Boid var6 : var1) {
            float var7 = this.copy().distanceToSquared(var6);
            if ((var7 > 0) && (var7 < var3)) {
                Vec3D var8 = this.copy().subSelf(var6);
                var8.normalize();
                var8.scaleSelf(1 / var7);
                var4.addSelf(var8);
                var5++;
            }
        }
        if (var5 > 0) {
            var4.scaleSelf(1 / (float) var5);

        }
        if (var4.magnitude() > 0) {
            var4.normalize();
            var4.scaleSelf(maxspeed);
            var4.subSelf(vel);
            var4.limit(maxforce);
        }
        return var4;
    }

    private Vec3D align(List<Boid> var1, float var2) {
        float var3 = var2 * var2;
        Vec3D var4 = new Vec3D(0, 0, 0);
        int var5 = 0;
        for (Boid var6 : var1) {
            if (var6 == this) continue;
            float var7 = this.copy().distanceToSquared(var6);
            if ((var7 < var3)) {
                var4.addSelf(var6.vel);
                var5++;
            }
        }
        if (var5 > 0) {
            var4.scaleSelf(1 / (float) var5);
            var4.normalize();
            var4.scaleSelf(maxspeed);
            Vec3D var8 = var4.subSelf(vel);
            var8.limit(maxforce);
            return var8;
        } else {
            return new Vec3D(0, 0, 0);
        }
    }

    private Vec3D cohesion(List<Boid> var1, float var2) {
        float var3 = var2 * var2;
        Vec3D var4 = new Vec3D(0, 0, 0);
        int var5 = 0;
        for (Boid var6 : var1) {
            if (var6 == this) continue;
            float var7 = this.copy().distanceToSquared(var6);
            if ((var7 < var3)) {
                var4.addSelf(var6);
                var5++;
            }
        }
        if (var5 > 0) {
            var4.scaleSelf(1 / (float) var5);
            return seek(var4);
        } else {
            return new Vec3D(0, 0, 0);
        }
    }

    private Vec3D seektrail(List<trail> var1, float var2) {
        Vec3D var3 = new Vec3D(0, 0, 0);
        int var4 = 0;

        for (int i = 0; i < var1.size(); i++) {
            trail var5 = var1.get(i);
            if (p.abs(var5.x - this.x) < var2 && p.abs(var5.y - this.y) < var2 && p.abs(var5.x - this.x) < var2) {
                var3.addSelf(var5.copy());
                var4++;
            }
        }

        if (var4 > 0) {
            var3.scaleSelf(1 / (float) var4);
            return seek(var3);
        }

        return var3;
    }

    private Vec3D aligntrail(List<trail> var1, float var2) {
        Vec3D var3 = new Vec3D(0, 0, 0);
        int var4 = 0;
        float var5 = var2 * var2;
        for (int i = 0; i < var1.size(); i++) {
            trail var6 = var1.get(i);
            float var7 = this.distanceToSquared(var6);
            if ((var7 > 0) && (var7 < var5)) {
                var3.addSelf(var6.orientation);
                var4++;
            }
        }
        if (var4 > 0) {
            var3.scaleSelf(1 / (float) var4);
            var3.normalize();
            var3.scaleSelf(maxspeed);
            return var3;
        } else {
            return new Vec3D(0, 0, 0);
        }
    }

    private boolean inView(Vec3D var1, float var2) {
        boolean var3;
        Vec3D var4 = var1.copy().subSelf(this.copy());
        float var5 = vel.copy().angleBetween(var4);
        var5 = p.degrees(var5);
        if (var5 < var2) {
            var3 = true;
        } else {
            var3 = false;
        }
        return var3;
    }

    private void borders() {
        List<Vec3D> var1 = null;
        var1 = p.meshoctree.getPointsWithinSphere(this.copy(), 60);

        if (var1 != null) {

            if (var1.size() > 0) {
                if (!reflect) {
                    vel.scaleSelf(-3);
                }
                if (reflect) {
                    Vec3D var2 = null;
                    float var3 = 3.4028235E38F;
                    for (int i = 0; i < var1.size(); i++) {
                        Vec3D var4 = var1.get(i);
                        float var5 = var4.distanceToSquared(this.copy());
                        if (var5 < var3) {
                            var2 = var4;
                            var3 = var5;
                        }
                    }

                    Vec3D var6 = p.Normal.get(var2);
                    var6.scaleSelf(-1);
                    float var7 = vel.copy().dot(var6.normalize());
                    Vec3D var8 = var6.normalize().scaleSelf(var7);
                    vel = vel.copy().addSelf(var6);

                    if ((type == 7) || (type == 8)) {
                        vel = vel.copy().subSelf(var8.scaleSelf(6.0f));
                    } else {
                        vel = vel.copy().subSelf(var8.scaleSelf(3.0f));
                    }


                }
            }
        }
    }

    public void checkMesh() {

        Vec3D var1 = p.cave.getClosestVertexToPoint(this);
        float var2 = var1.distanceToSquared(this);

        Vec3D var3 = var1.copy().subSelf(this);
        Vec3D var4 = p.Normal.get(var1);


        float var5 = var4.angleBetween(var3, true);
        float var6 = p.degrees(var5);
        if (var6 > 90) {
            p.flock.removeBoid(this);
        }

        if (var2 < 55 * 55) {
            p.flock.removeBoid(this);
        }
    }

}