/**
 * Created by Omar on 11/12/2016.
 */

import processing.core.PShape;
import processing.opengl.*;
import processing.core.PApplet;


import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.math.waves.AbstractWave;
import toxi.math.waves.SineWave;
import toxi.volume.*;
import toxi.processing.*;

import wblut.processing.*;
import wblut.hemesh.*;
import wblut.geom.*;

import java.util.*;
import java.util.Map;
import java.util.Iterator;

import pathfinder.*;

import peasy.*;

import com.hamoid.*;

import java.util.ArrayList;


public class Print extends PApplet {

    public PeasyCam cam;
    public PShape obj;

    float xmaxint;
    float ymaxint;
    float xminint;
    float yminint;
    float zminint;
    float zmaxint;

    boolean start2 = false;

    boolean boidoctre = false;
    boolean makecorridor = true;
    boolean makepath = false;

    public WETriangleMesh cave;
    public HE_Mesh mesh;


    public List<Vec3D> cavepts;
    public List<meshvertices> vertexpop = new ArrayList<>();
    public List<Vec3D> pts = new ArrayList<>();

    public HashMap<Vec3D, Integer> Slope = new HashMap();
    public HashMap<Vec3D, Vec3D> Normal = new HashMap();
    public HashMap<Vec3D, Integer> ptscheck = new HashMap<>();

    float DIM = 1500;


    public Octree meshoctree;
    public Octree boidoctree;

    public Flock flock;

    float ballvel = 0f;
    boolean ballmove = true;
    boolean buildmesh = false;
    boolean buildmesh1 = false;
    boolean buildvolume = false;

    public Graph gs = new Graph();
    public GraphNode[] gNodes, rNodes;
    public GraphEdge[] gEdges, exploredEdges;
    IGraphSearch pathFinder;


    public WETriangleMesh corridor;

    public Vec3D SCALE = new Vec3D(1, 1, 1).scaleSelf(100);

    int DIMX = 64;
    int DIMY = 64;
    int DIMZ = 64;

    float density = 0.5f;

    VolumetricSpace volume;
    VolumetricBrush brush;
    IsoSurface surface;

    AbstractWave brushSize;

    WB_Render render;
    ToxiclibsSupport gfx;

    VideoExport videoExport;

    Vec3D meshcentre;

    public static void main(String[] args) {
        PApplet.main("Print", args);
    }

    public void settings() {
        size(1400, 800, P3D);
        smooth();
    }

    public void setup() {

        flock = new Flock(this);

        obj = loadShape("data/" + "drone.obj");
        obj.scale(3);

        meshsetup();

        cam = new PeasyCam(this, meshcentre.x, meshcentre.y, meshcentre.z, 2200);

        meshoctree = new Octree(this, new Vec3D(-1, -1, -1).scaleSelf(meshcentre), DIM * 2);
        if (boidoctre) boidoctree = new Octree(this, new Vec3D(-1, -1, -1).scaleSelf(meshcentre), DIM * 2);

        meshrun();

        if ((makecorridor)||(makepath)) {
            volume = new VolumetricSpaceArray(SCALE.scaleSelf(20), DIMX, DIMY, DIMZ);
            brush = new RoundBrush(volume, 1000);
            surface = new ArrayIsoSurface(volume);
            if (makecorridor) corridor = new WETriangleMesh();
        }

        setpathfind();

        for (int i = 0; i < 15; i++) {
            Vec3D a = randomitem(pts);
            flock.addBoid(new Boid(this, a, 1));
        }

        for (int i = 0; i < 100; i++) {
            Vec3D a = randomitem(pts);
            flock.addBoid(new Boid(this, a, 2));
        }

        for (int i = 0; i < 10; i++) {
            Vec3D a = randomitem(pts);
            flock.addBoid(new Boid(this, a, 7));
        }

        for (int i = 0; i < 0; i++) {
            Vec3D a = randomitem(pts);
            flock.addBoid(new Boid(this, a, 8));
        }

        videoExport = new VideoExport(this, "basic.mp4");

    }

    public void draw() {
        background(0);

        if (frameCount < 10) {
            for (int i = 0; i < flock.boids.size(); i++) {
                Boid b = flock.boids.get(i);
                b.checkMesh();
            }
        }

        //        if ((frameCount%20 == 0)&&(flock.boids.size()<25)) {
//            for (int i = 0; i < 5; i++) {
//                flock.addBoid(new Boid(this, new Vec3D(random(xminint + 300, xmaxint - 300), random(yminint + 300, ymaxint - 300), random(zminint + 300, zmaxint - 300)), new Vec3D(random(-TWO_PI, TWO_PI), random(-TWO_PI, TWO_PI), random(-TWO_PI, TWO_PI)), 2));
//            }
//        }

        if (frameCount > 10) {
            if((makepath)||(makecorridor)) {
                List<Boid> pathboid = new ArrayList<>();
                List<Boid> pathroom = new ArrayList<>();

                for (Boid a : flock.boids) {
                    if (a.type == 7) {
                        pathboid.add(a);
                    }
                    if (a.type == 8) {
                        pathroom.add(a);
                    }
                }

                if (makepath) {
                    List<String> pathptsfile = new ArrayList<>();
                    runpathfind2(pathptsfile, pathboid);
                }

                if (makecorridor) {
                    List<String> pathptsfile = new ArrayList<>();
                    List<Pathagent> pathagtpts = new ArrayList<>();
                    List<Vec3D> circpts = new ArrayList<>();

                    runpathfind2(pathptsfile, pathboid);
                    readpath(circpts);
                    for (Vec3D a : circpts) {
                        Pathagent b = new Pathagent(this, a, 1);
                        pathagtpts.add(b);
                    }
                    for (Boid a : pathboid) {
                        Pathagent c = new Pathagent(this, a, 2);
                        pathagtpts.add(c);
                    }

                    for (Boid a : pathroom) {
                        Pathagent c = new Pathagent(this, a, 2);
                        pathagtpts.add(c);
                    }

                    drawcorridor(pathagtpts);
                }
            }
            flock.run();
            if (boidoctre) boidoctree.run();
        }
        //if octree.draw();

        pushMatrix();
        fill(40, 120);
        noStroke();
        lights();
        gfx.mesh(cave, false, 0);
        popMatrix();

        for (int i = 0; i < vertexpop.size(); i++) {
            meshvertices a = vertexpop.get(i);
            a.update();
        }

//       videoExport.saveFrame();
    }

    private void meshsetup() {
        cave = (WETriangleMesh) new STLReader().loadBinary(sketchPath("data/" + "cave2.stl"), STLReader.WEMESH);
        mesh = new HEC_FromBinarySTLFile(sketchPath("data/" + "cave2.stl")).create();


        ArrayList var1 = new ArrayList();
        ArrayList var2 = new ArrayList();
        ArrayList var3 = new ArrayList();

        int var4 = cave.getNumVertices();

        cavepts = (new ArrayList<Vec3D>(cave.getVertices()));

        for (int i = 0; i < var4; i++) {
            Vec3D var5 = cavepts.get(i);
            var1.add(var5.x);
            var2.add(var5.y);
            var3.add(var5.z);
        }

        xmaxint = (float) Collections.max(var1);
        ymaxint = (float) Collections.max(var2);
        zmaxint = (float) Collections.max(var3);
        xminint = (float) Collections.min(var1);
        yminint = (float) Collections.min(var2);
        zminint = (float) Collections.min(var3);

        var1.clear();
        var2.clear();
        var3.clear();

        meshcentre = cave.computeCentroid();

    }

    private void readpath(List<Vec3D> var1) {

        String var2[] = loadStrings("data/" + "path.txt");
        int var3 = 0;

        for (int i = 0; i < var2.length; i++) {
            if (var2[i].equals("!")) {
                var3++;
            }
        }

        Integer[] var4 = new Integer[var3];
        int var5 = 0;

        for (int i = 0; i < var2.length; i++) {
            if (var2[i].equals("!")) {
                var4[var5] = i;
                var5++;
            }
        }

        String[][] var6 = new String[var3][];
        for (int i = 0; i < var4.length; i++) {
            if (i == 0) {
                var6[i] = (Arrays.copyOfRange(var2, 0, var4[i] - 1));
            }
            if ((i > 0) && (i < var4.length)) {
                var6[i] = (Arrays.copyOfRange(var2, (var4[i - 1] + 1), (var4[i] - 1)));
            }
        }

        for (int i = 0; i < var6.length; i++) {
            for (int j = 0; j < var6[i].length; j++) {
                String[] var7 = split(var6[i][j], ",");
                Vec3D var8 = new Vec3D(Float.parseFloat(var7[0]), Float.parseFloat(var7[1]), Float.parseFloat(var7[2]));
                var1.add(var8);
            }
        }
    }

    private void meshrun() {

        int var1 = mesh.getNumberOfVertices();

        for (int i = 0; i < var1; i++) {
            WB_Coord var2 = mesh.getVertexNormal(i);
            Vec3D var3 = cave.getVertexForID(i);
            float var4 = var2.xf();
            float var5 = var2.yf();
            float var6 = var2.zf();
            Vec3D var7 = new Vec3D(var4, var5, var6);
            Vec3D var8 = new Vec3D(0, 0, 1);
            float var9 = var7.angleBetween(var8);
            var9 = degrees(var9);
            var9 = 180 - var9;
            int var10 = (int) var9;
            Slope.put(var3, var10);
            Normal.put(var3, var7);
            meshvertices var11 = new meshvertices(this, var3, var10, var7);
            if(i%5==0)vertexpop.add(var11);
            meshoctree.addPts(var11);
        }


        gfx = new ToxiclibsSupport(this);
        // render = new WB_Render(this);
    }

    private void setpathfind() {

        readText();

        gs = new Graph();

        Collections.reverse(pts);

        float var1;
        float var2;

        ArrayList<Float> var3 = new ArrayList<>();

        for (int i = 0; i < pts.size(); i++) {
            Vec3D var4 = pts.get(i);
            ptscheck.put(var4, i);
            Vec3D var5 = cave.getClosestVertexToPoint(var4);
            float var6 = var4.distanceTo(var5);
            int var7 = Slope.get(var5);
            float var8 = var7 / var6 * var6;
            var3.add(var8);
        }

        var2 = Collections.max(var3);
        var1 = Collections.min(var3);


        HashMap<Vec3D, Float> var9 = new HashMap<>();


        for (int i = 0; i < pts.size(); i++) {
            Vec3D var10 = pts.get(i);
            float var11 = var3.get(i);
            float var12 = 0.00f;
            float var13 = 0.4f;
            float var14 = map(var11, var1, var2, var12, var13);
            float var15 = map(var14, 0.00f, 0.2f, 0.00f, 100.0f);
            var9.put(var10, var15);
        }

        for (int i = 0; i < pts.size(); i++) {
            Vec3D var16 = pts.get(i);
            gs.addNode(new GraphNode(i, var16.x, var16.y, var16.z));
            for (int j = 0; j < pts.size(); j++) {
                Vec3D var17 = pts.get(j);
                if (var17 != var16) {
                    if (var17.distanceToSquared(var16) < 80 * 80) {
                        float var18 = var9.get(var16);
                        gs.addEdge(i, j, var18);
                    }
                }
            }
        }

        gNodes = gs.getNodeArray();
        gEdges = gs.getAllEdgeArray();
        gs.compact();

    }

    private void drawcorridor(List<Pathagent> var1) {
        if ((makecorridor)) {

            ballvel = 0;

            if (ballmove) {
                for (Pathagent var2 : var1) {
                    var2.run();
                }

            }

            if (!buildmesh) {

                corridor.clear();
                surface.reset();
                volume.clear();

                brush.setSize(new SineWave(0, 0.1f, 140f, 35f).update());
                for (Pathagent var3 : var1) {
                    if (var3.type == 1)
                        brush.drawAtAbsolutePos(new Vec3D(var3.x - meshcentre.x, var3.y - meshcentre.y, var3.z - meshcentre.z), density);
                }
                brush.setSize(new SineWave(0, 0.1f, 140f, 120f).update());
                for (Pathagent var3 : var1) {
                    if (var3.type == 2)
                        brush.drawAtAbsolutePos(new Vec3D(var3.x - meshcentre.x, var3.y - meshcentre.y, var3.z - meshcentre.z), density);
                }
                volume.closeSides();
                surface.reset();
                surface.computeSurfaceMesh(corridor, 0.1f);
                for (int i = 0; i < 1; i++) {
                    new LaplacianSmooth().filter(corridor, 1);
                }

                corridor = corridor.getTranslated(new Vec3D(meshcentre.x, meshcentre.y, meshcentre.z));



//                translate(meshcentre.x, meshcentre.y, meshcentre.z);
                //                   buildmesh = true;


//                corridor.computeFaceNormals();
//                corridor.computeVertexNormals();


            }

//            if (!buildmesh) {
//                corridor.clear();
//
//                for (Pathagent a : pathagtpts) {
//                    corridor.addMesh(a.b);
//                }
//
//                corridcntr = corridor.computeCentroid();
//                MeshVoxelizer voxelizer = new MeshVoxelizer(RES);
//                voxelizer.setWallThickness(0);
//                VolumetricSpace vol = voxelizer.voxelizeMesh(corridor);
//                vol.closeSides();
//                IsoSurface surface = new HashIsoSurface(vol);
//                corridor = new WETriangleMesh();
//                surface.computeSurfaceMesh(corridor, 0.2f);
//                corridor.computeVertexNormals();
//
//                for (int i = 0; i < 1; i++) {
//                    new LaplacianSmooth().filter(corridor, 1);
//                }
////                    buildmesh = true;
//            }

            //           corridor.saveAsOBJ(sketchPath("data/" + "corridor.obj"));

//            pushMatrix();
//            strokeWeight(1f);
//            stroke(255, 0, 0);
//            noFill();
//            gfx.mesh(corridor);
//            popMatrix();

//            }
        }
    }

    private void runpathfind2(List<String> var1,List<Boid> var2) {

        for (Boid b : var2) {
            pathFinder = makePathFinder(3);
            usePathFinder(pathFinder, findclosestnode(b), 0);
            if(!buildmesh)drawRoute(rNodes, color(200, 0, 0), 5.0f);
            for (int i = 0; i < rNodes.length; i++) {
                String var3 = (Float.toString(rNodes[i].xf()) + "," + Float.toString(rNodes[i].yf()) + "," + Float.toString(rNodes[i].zf()));
                var1.add(var3);
            }
            var1.add("!");
        }

        String[] var4 = new String[var1.size()];

        for (int i = 0; i < var1.size(); i++) {
            String var5 = var1.get(i);
            var4[i] = var5;
        }

        saveStrings("data/" + "path.txt", var4);

    }

    private void readText() {
        String[] var1 = loadStrings("data/" + "points.txt");
        for (int i = var1.length - 1; i >= 0; i--) {
            String var2[] = (split(var1[i], ','));
            if (var2.length == 3) {
                Vec3D var3 = new Vec3D(Float.parseFloat(var2[0]), Float.parseFloat(var2[1]), Float.parseFloat(var2[2]));
                pts.add(var3);
            }
        }
    }

    private Vec3D randomitem(List <Vec3D> var1){
        int var2 = (int) random(var1.size());
        Vec3D var3 = var1.get(var2);
        return var3;
    }

    IGraphSearch makePathFinder(int pathFinder) {
        IGraphSearch pf = null;
        float f = 1.0f;
        switch (pathFinder) {
            case 0:
                pf = new GraphSearch_DFS(gs);
                break;
            case 1:
                pf = new GraphSearch_BFS(gs);
                break;
            case 2:
                pf = new GraphSearch_Dijkstra(gs);
                break;
            case 3:
                pf = new GraphSearch_Astar(gs, new AshCrowFlight(f));
                break;
            case 4:
                pf = new GraphSearch_Astar(gs, new AshManhattan(f));
                break;
        }
        return pf;
    }

    void drawRoute(GraphNode[] r, int lineCol, float sWeight) {
        if (r.length >= 2) {
            pushStyle();
            stroke(lineCol);
            strokeWeight(sWeight);
            noFill();
            for (int i = 1; i < r.length; i++) {
                line(r[i - 1].xf(), r[i - 1].yf(), r[i - 1].zf(), r[i].xf(), r[i].yf(), r[i].zf());
            }
            popStyle();
        }
    }

    void usePathFinder(IGraphSearch pf, int start1, int end1) {
        pf.search(start1, end1, true);
        rNodes = pf.getRoute();
        exploredEdges = pf.getExaminedEdges();
    }

    private int findclosestnode(ReadonlyVec3D var1) {
        float var2 = 3.4028235E38F;
        int var3 = 0;

        for (int i = 0; i < pts.size(); i++) {
            Vec3D var4 = pts.get(i);
            float var5 = var4.distanceToSquared(var1);
            if (var5 < var2) {
                var2 = var5;
                var3 = i;
            }
        }
        return var3;
    }

}
