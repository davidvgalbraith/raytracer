package raytracer;

import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		System.out.println("Okay, let's roll.");
		Sampler s = new Sampler(100, 100);
		Point eyepos = new Point(0, 0, 0);
		Vector ul = new Vector(-1, 1, -1);
		Vector ur = new Vector(1, 1, -1);
		Vector lr = new Vector(1, -1, -1);
		Vector ll = new Vector(-1, -1, -1);
		Camera c = new Camera(eyepos, null, null, ul, ur, lr, ll);
		Film f = new Film(100, 100);
		Sphere sph = new Sphere(new Point(0, 0, -2), 1.0);
		double[][] id = new double[4][4];
		id[0][0] = 1; id[0][1] = 0; id[0][2] = 0; id[0][3] = 0;
		id[1][0] = 0; id[1][1] = 1; id[1][2] = 0; id[1][3] = 0;
		id[2][0] = 0; id[2][1] = 0; id[2][2] = 1; id[2][3] = 0;
		id[3][0] = 0; id[3][1] = 0; id[3][2] = 0; id[3][3] = 1;
		Matrix idenity = new Matrix(id);
		Transformation t = new Transformation(idenity);
		Material m = new Material();
		GeometricPrimitive g = new GeometricPrimitive(t, t, sph, m);
		AggregatePrimitive agg = new AggregatePrimitive();
		agg.add(g);
		RayTracer r = new RayTracer(agg, new ArrayList<Light>());
		Scene scene = new Scene(s, c, f, r);
		scene.render();
		System.out.println("Whew.");
	}
	
}
