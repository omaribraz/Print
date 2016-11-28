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
    private Vec3D v;
    private Vec3D a;
    private Vec3D n = null;
    private Vec3D n1 = null;
    private Vec3D s1 = null;
    private Vec3D a1 = null;
    private Vec3D c1 = null;
    private Vec3D ct1 = null;
    private Vec3D at1 = null;
    private Vec3D sv = null;
    private Vec3D noi;
    private Vec3D cra = null;


    private meshvertices go = null;
    private meshvertices go1 = null;

    private List<List<trail>> tp;

    private float mf;
    private float ms;
    private boolean re = true;
    private boolean pr = false;
    private int prct;
    private boolean nd = false;
    private boolean nd1 = false;
    private boolean st = false;
    private boolean craw = false;
    private int cracnt = 0;
    private boolean noip = false;


    public int t;
    private int trno = -1;
    private int thti = 0;

    private List fr;

    Boid(Print _p, Vec3D pos, int _t) {
        super(pos);
        p = _p;
        v = new Vec3D(p.random(-p.TWO_PI, p.TWO_PI), p.random(-p.TWO_PI, p.TWO_PI), p.random(-p.TWO_PI, p.TWO_PI));
        t = _t;
        a = new Vec3D(0, 0, 0);
        ms = 4;
        if((t==7)&&(t==8))ms = 1;
        mf = 0.77f;
        if((t==7)&&(t==8))mf = 0.44f;
        tp = new ArrayList<>();
        fr = new ArrayList<>();
        thti = (int) (p.random(10));
    }

    public void r() {
        i();
        r(this);
        if (thti == 0) {
            if (!p.boidoctre) gF(this.t, 100);
            if (p.boidoctre) fr = p.boidoctree.getPointsWithinSphere(this.copy(), 120);
        }
        f();
        if ((p.frameCount % 3 == 0) && (p.frameCount > 20) && (pr)) t();
        u();
        b();
    }

    private void i() {
        thti = (thti + 1) % 5;
    }

    private void gF(int var1, int var2) {
        List<Boid> var3 = new ArrayList<>();
        for (int i = 0; i < p.flock.b.size(); i++) {
            Boid var4 = p.flock.b.get(i);
            if (var4 == this) continue;
            if (var4.t == var1) {
                if (p.abs(var4.x - this.x) < var2 &&
                        p.abs(var4.y - this.y) < var2 &&
                        p.abs(var4.z - this.z) < var2) {
                    var3.add(var4);
                }
            }
        }
        fr = var3;
    }

    private void r(Boid a) {

        if ((a.t == 6) && (p.frameCount < 12)) {
            tp.add(new ArrayList<trail>());
        }

        if (a.t == 1) {
            a.nd = true;
            a.pr = false;
            a.go1 = null;
            if (a.go == null) {
                a.go = scl(0, 80);
            }
        }

        if (a.t == 2) {
            a.nd1 = true;
            a.pr = false;
            if (a.go == null) {
                a.go = sc(1);
            }
        }

        if (a.t == 3) {
            a.pr = true;
            a.go = null;
            if (a.go1 == null) {
                a.go1 = scmr(0, 110, 700);
            }
        }

        if (a.t == 4) {
            a.pr = true;
            a.st = true;
            a.go = null;
        }

        if ((a.prct > 40) && (a.t == 3)) {
            a.t = 1;
            a.pr = false;
            a.prct = 0;
            if(a.cracnt>30){
                a.craw = false;
                a.cracnt = 0;
            }

        }

        if ((a.prct > 30) && (a.t == 4)) {
            a.t = 2;
            a.pr = false;
            a.prct = 0;

        }

    }

    private void fr(Boid a) {

        if (a.t == 2) {
            if ((a.nd1) && (p.start2)) a.n1.scaleSelf(0.7f);
            a.s1.scaleSelf(0.3f);
            a.a1.scaleSelf(0.1f);
            a.c1.scaleSelf(0.07f);
        }

        if (a.t == 4) {
            a.s1.scaleSelf(0.4f);
            a.a1.scaleSelf(0.05f);
            a.c1.scaleSelf(0.007f);
            if (st) {
                a.ct1.scaleSelf(0.7f);
                a.at1.scaleSelf(0.05f);
            }
        }

        if (a.t == 3) {
            a.s1.scaleSelf(0.15f);
            a.a1.scaleSelf(0.03f);
            a.c1.scaleSelf(0.005f);
            a.n.scaleSelf(0f);
            a.sv.scaleSelf(0.202f);
            if ((p.makecorridor) && (craw)) a.cra.scaleSelf(0.4f);
            if (noip) a.noi.scaleSelf(0.01f);
        }

        if (a.t == 1) {
            if (a.nd) a.n.scaleSelf(0.71f);
            a.s1.scaleSelf(0.3f);
            a.a1.scaleSelf(0.05f);
            a.c1.scaleSelf(0.02f);
        }

        if ((a.t == 7) && (a.t == 8)) {
            a.s1.scaleSelf(0.7f);
            a.a1.scaleSelf(0.5f);
            a.c1.scaleSelf(0.2f);
        }

    }

    private void f() {


        if ((t == 1) || (t == 3)) {
            s1 = s(fr, 90.0f);
            a1 = a(fr, 40.0f);
            c1 = c(fr, 40.0f);
        }

        if ((t == 2) || (t == 4)) {
            s1 = s(fr, 30.0f);
            a1 = a(fr, 40.0f);
            c1 = c(fr, 30.0f);
        }

        if ((nd) && (t == 1)) {
            n = vs();

        }

        if (t == 3) {
            sv = es();
            if ((p.makecorridor) && (craw)) {
                cra = cr(p.corridor, 80, 2);
                cracnt++;
            }
            if (noip) noi = new Vec3D(p.random(2) - 1, p.random(2) - 1, p.random(2) - 1);
        }

        if ((nd1) && (t == 2) && (p.start2)) {
            n1 = vs1();
        }

        if ((t == 4)) {
            ct1 = ct(p.flock.t, 50.0f);
            at1 = at(p.flock.t, 50.0f);
        }

        if ((t == 7) || (t == 8)) {
            s1 = s(fr, 90.0f);
            a1 = a(fr, 40.0f);
            c1 = c(fr, 40.0f);
        }

        fr(this);

        aF(s1);
        aF(a1);
        aF(c1);

        if ((nd) && (t == 1)) {
            aF(n);
        }

        if ((nd1) && (t == 2) && (p.start2)) {
            aF(n1);
        }

        if ((t == 4)) {
            aF(ct1);
            aF(at1);
        }

        if (t == 3) {
            aF(sv);
            if ((p.makecorridor) && (craw)) aF(cra);
            if (noip) aF(noi);
        }
    }

    private void u() {
        v.addSelf(a);
        v.limit(ms);
        this.addSelf(v);
        a.scaleSelf(0);
    }

    private void aF(Vec3D var1) {
        a.addSelf(var1);
    }

    private Vec3D se(Vec3D var1) {
        Vec3D var2 = var1.subSelf(this.copy());
        var2.normalize();
        var2.scaleSelf(ms);
        Vec3D var3 = var2.subSelf(v);
        var3.limit(mf);
        return var3;
    }

    private void t() {
        Vec3D var1 = new Vec3D((int) this.copy().x, (int) this.copy().y, (int) this.copy().z);
        Vec3D var2 = new Vec3D((int) this.v.copy().x, (int) this.v.copy().y, (int) this.v.copy().z);
        trail var3 = new trail(p, var1, var2);
        tp.get(trno).add(var3);
        if (t == 3) {
            p.flock.aT(var3);
        }
        prct++;
    }

    public void d() {
        float var1 = v.headingXY() + p.radians(90);
        p.stroke(255);
        p.pushMatrix();
        p.translate(x, y, z);
        p.rotate(var1);
        if ((t == 1) || (t == 3)) p.obj.setFill(p.color(0, 0, 255));
        if ((t == 2) || (t == 4)) p.obj.setFill(p.color(255, 255, 255));
        if (t == 7) p.obj.setFill(p.color(255, 0, 255));
        if (t == 8) p.obj.setFill(p.color(255, 0, 0));
        if (t == 6) p.obj.setFill(p.color(0, 255, 0));
        p.obj.setStroke(100);
        p.obj.scale(1);
        p.shape(p.obj);
        p.popMatrix();

        p.noFill();
        p.strokeWeight(2);

        for (int i = 0; i < tp.size(); i++) {
            List<trail> var2 = tp.get(i);
            p.beginShape();
            for (int j = 0; j < var2.size(); j++) {
                trail var3 = var2.get(j);

                if ((t == 4) || (t == 2)) {
                    float var4 = PApplet.map(j, 0, var3.trailNo, 0, 1);
                    int var5 = p.color(125, 60, 100, 255);
                    int var6 = p.color(200, 255, 50, 255);
                    int var7 = p.lerpColor(var5, var6, var4);
                    p.stroke(var7);
                }

                if ((t == 3) || (t == 1)) {
                    float var4 = PApplet.map(j, 0, var3.trailNo, 0, 1);
                    int var5 = p.color(255, 255, 255, 255);
                    int var6 = p.color(255, 255, 255, 255);
                    int var7 = p.lerpColor(var5, var6, var4);
                    p.stroke(var7);
                }

                if ((t == 6)) {
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

    private meshvertices sc(int var1) {
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

    private meshvertices scl(int var1, int var2) {
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

    private meshvertices scm(int var1, int var2) {
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

    private meshvertices scmr(int var1, int var2, int var3) {
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

    private Vec3D vs() {
        if (go == null) {
            go = scl(0, 50);
        }
        float var1 = go.distanceToSquared(this);
        if (var1 < 64 * 64) {
            t = 3;
            craw = true;
            p.start2 = true;
            nd = false;
            tp.add(new ArrayList<trail>());
            trno++;
            go.taken = 1;
        }
        return se(go.copy());
    }

    private Vec3D cr(WETriangleMesh var1, int var2, int var3) {
        Vec3D var4 = v.copy();
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

    private Vec3D es() {
        if (go1 == null) {
            go1 = scmr(0, 110, 200);
        }
        float var1 = go1.distanceToSquared(this);
        if (var1 < 64 * 64) {
            t = 1;
            nd = true;
            go1.taken = 3;
        }
        return se(go1.copy());
    }

    private Vec3D vs1() {

        Vec3D var1 = new Vec3D(0, 0, 0);

        if (go == null) {
            go = sc(1);
        }

        if (go != null) {
            float var2 = go.distanceToSquared(this);

            if (var2 < 500 * 500) {
                var1 = se(go.copy());
            } else go = null;

            if (var2 < 55 * 55) {
                t = 4;
                tp.add(new ArrayList<trail>());
                trno++;
                nd1 = false;
                go.takencnt++;
                meshvertices var3 = sc(0);
                var3.taken = 3;
            }
        }
        return var1;
    }

    private Vec3D s(List<Boid> var1, float var2) {
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
            var4.scaleSelf(ms);
            var4.subSelf(v);
            var4.limit(mf);
        }
        return var4;
    }

    private Vec3D a(List<Boid> var1, float var2) {
        float var3 = var2 * var2;
        Vec3D var4 = new Vec3D(0, 0, 0);
        int var5 = 0;
        for (Boid var6 : var1) {
            if (var6 == this) continue;
            float var7 = this.copy().distanceToSquared(var6);
            if ((var7 < var3)) {
                var4.addSelf(var6.v);
                var5++;
            }
        }
        if (var5 > 0) {
            var4.scaleSelf(1 / (float) var5);
            var4.normalize();
            var4.scaleSelf(ms);
            Vec3D var8 = var4.subSelf(v);
            var8.limit(mf);
            return var8;
        } else {
            return new Vec3D(0, 0, 0);
        }
    }

    private Vec3D c(List<Boid> var1, float var2) {
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
            return se(var4);
        } else {
            return new Vec3D(0, 0, 0);
        }
    }

    private Vec3D ct(List<trail> var1, float var2) {
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
            return se(var3);
        }

        return var3;
    }

    private Vec3D at(List<trail> var1, float var2) {
        Vec3D var3 = new Vec3D(0, 0, 0);
        int var4 = 0;
        float var5 = var2 * var2;
        for (int i = 0; i < var1.size(); i++) {
            trail var6 = var1.get(i);
            float var7 = this.distanceToSquared(var6);
            if ((var7 > 0) && (var7 < var5)) {
                var3.addSelf(var6.o);
                var4++;
            }
        }
        if (var4 > 0) {
            var3.scaleSelf(1 / (float) var4);
            var3.normalize();
            var3.scaleSelf(ms);
            return var3;
        } else {
            return new Vec3D(0, 0, 0);
        }
    }

    private boolean iV(Vec3D var1, float var2) {
        boolean var3;
        Vec3D var4 = var1.copy().subSelf(this.copy());
        float var5 = v.copy().angleBetween(var4);
        var5 = p.degrees(var5);
        if (var5 < var2) {
            var3 = true;
        } else {
            var3 = false;
        }
        return var3;
    }

    private void b() {
        List<Vec3D> var1 = null;
        var1 = p.meshoctree.getPointsWithinSphere(this.copy(), 60);

        if (var1 != null) {

            if (var1.size() > 0) {
                if (!re) {
                    v.scaleSelf(-3);
                }
                if (re) {
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
                    float var7 = v.copy().dot(var6.normalize());
                    Vec3D var8 = var6.normalize().scaleSelf(var7);
                    v = v.copy().addSelf(var6);

                    if ((t == 7) || (t == 8)) {
                        v = v.copy().subSelf(var8.scaleSelf(6.0f));
                    } else {
                        v = v.copy().subSelf(var8.scaleSelf(3.0f));
                    }


                }
            }
        }
    }

    public void cM() {

        Vec3D var1 = p.cave.getClosestVertexToPoint(this);
        float var2 = var1.distanceToSquared(this);

        Vec3D var3 = var1.copy().subSelf(this);
        Vec3D var4 = p.Normal.get(var1);


        float var5 = var4.angleBetween(var3, true);
        float var6 = p.degrees(var5);
        if (var6 > 90) {
            p.flock.rB(this);
        }

        if (var2 < 55 * 55) {
            p.flock.rB(this);
        }
    }

}