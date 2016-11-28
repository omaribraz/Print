/**
 * Created by Vikram on 11/12/2016.
 */

import toxi.geom.PointOctree;
import toxi.geom.Vec3D;


public class Octree extends PointOctree {
    private Print p;

    Octree(Print _p, Vec3D o, float d) {
        super(o, d);
        p = _p;
    }

    private void addBoid(Boid var1) {
        addPoint(var1);
    }

    public void addPts(meshvertices var1){addPoint(var1);}

    public void run() {
        updateTree();
    }

    private void updateTree() {
        empty();
        for (Boid var1 : p.flock.boids) {
            addBoid(var1);
        }
    }

    public void draw() {
        drawNode(this);
        System.out.println("p = " + this.getNumChildren());
    }

    private void drawNode(PointOctree n) {
        if (n.getNumChildren() > 0) {
            p.noFill();
            p.stroke(255);
            p.strokeWeight(1);
            p.pushMatrix();
            p.translate(n.x, n.y, n.z);
            p.popMatrix();
            PointOctree[] childNodes = n.getChildren();
            for (int i = 0; i < 8; i++) {
                if (childNodes[i] != null) drawNode(childNodes[i]);
            }
        }
    }


}
