/**
 * Created by omar on 10/31/2016.
 */

import toxi.geom.ReadonlyVec3D;
import toxi.geom.Shape3D;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Mesh3D;

import java.util.List;

import static processing.core.PApplet.degrees;

public class Pathagent extends Vec3D {
    private Print p;
    private Vec3D vel;
    public Sphere a;
    public Mesh3D b;
    private float dia = 60;
    private float dia2 = 120;
    int type;


    Pathagent(Print _p, Vec3D pos, int _type) {
        super(pos);
        p = _p;
        vel = new Vec3D(0, 0, 0);
        type = _type;
        this.z += 10;
    }

    public void run() {
        if (type == 1) move();
        update();
        render();
    }

    private void update() {
        vel.limit(80.7f);
        this.addSelf(vel);
        vel.scaleSelf(0);
    }

    private void move() {
        List<Vec3D> var1 = null;

        var1 = p.meshoctree.getPointsWithinSphere(this.copy(), dia);
        if (var1 != null) {
            if (var1.size() > 0) {
                Vec3D var2 = new Vec3D();
                float var3 = 3.4028235E38F;
                for (int i = 0; i < var1.size(); i++) {
                    Vec3D var4 = var1.get(i);
                    float var5 = var4.distanceToSquared(this);
                    if (var5 < var3) {
                        var2 = var4;
                        var3 = var5;
                    }
                }
                Vec3D var6 = this.copy().subSelf(var2);

                float var7 = var2.distanceTo(this);

                Vec3D var8 = var2.copy().subSelf(this);
                Vec3D var9 = p.Normal.get(var2);

                float var10 = var9.angleBetween(var8, true);
                float var11 = degrees(var10);
                if (var11 > 90) {
                    var6 = var6.copy().scaleSelf(-1);
                }

                if (var7 < (dia + 5)) {
//                    var6.normalize();
                    var6 = var6.copy().scaleSelf(1 / var7);
                    vel.addSelf(var6);
                } else {
                    vel.scaleSelf(0);
                }
            }
        }
    }

    private void render() {
//        p.stroke(255, 0, 0);
//        p.pushMatrix();
//        p.translate(x, y, z);
//        p.sphere(dia);
//        p.popMatrix();
        if (type == 1) a = new Sphere(this, dia);
        if (type == 2) a = new Sphere(this, dia2);
        // b = a.toMesh(12);
//        p.noFill();
//        p.stroke(255, 0, 0);
//        p.gfx.sphere(a, 8);
    }


}
