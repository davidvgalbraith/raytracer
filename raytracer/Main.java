package raytracer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		System.out.println("Okay, let's roll.");
		Scanner scan = null;
		Point eyepos = new Point(0, 0, 0);
		int xpic = 100;
		int ypic = 100;
		Vector ul = new Vector(-1, 1, -1);
		Vector ur = new Vector(1, 1, -1);
		Vector lr = new Vector(1, -1, -1);
		Vector ll = new Vector(-1, -1, -1);
		double[][] id = new double[4][4];
		id[0][0] = 1;
		id[0][1] = 0;
		id[0][2] = 0;
		id[0][3] = 0;
		id[1][0] = 0;
		id[1][1] = 1;
		id[1][2] = 0;
		id[1][3] = 0;
		id[2][0] = 0;
		id[2][1] = 0;
		id[2][2] = 1;
		id[2][3] = 0;
		id[3][0] = 0;
		id[3][1] = 0;
		id[3][2] = 0;
		id[3][3] = 1;
		Matrix idenity = new Matrix(id);
		Transformation t = new Transformation(idenity);
		AggregatePrimitive agg = new AggregatePrimitive();
		ArrayList<Light> lights = new ArrayList<Light>();

		try {
			scan = new Scanner(new File(args[0]));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(2);
		}
		while (scan.hasNext()) {
			String next = scan.nextLine().trim();
			if (next.startsWith("eye")) {
				eyepos = pointify(next.substring(next.indexOf('=') + 2,
						next.length() - 1));
			}
			if (next.startsWith("UL")) {
				ul = vectify(next.substring(next.indexOf('=') + 2,
						next.length() - 1));
			}
			if (next.startsWith("UR")) {
				ur = vectify(next.substring(next.indexOf('=') + 2,
						next.length() - 1));
			}
			if (next.startsWith("LL")) {
				ll = vectify(next.substring(next.indexOf('=') + 2,
						next.length() - 1));
			}
			if (next.startsWith("LR")) {
				lr = vectify(next.substring(next.indexOf('=') + 2,
						next.length() - 1));
			}
			if (next.startsWith("DL")) {
				lights.add(new DirectionalLight(vectify(next.substring(
						next.indexOf(',') + 2, next.length())), colorfy(next
						.substring(5, next.indexOf(',') - 2))));
			}
			if (next.startsWith("pixelsX")) {
				xpic = Integer.parseInt(next.substring(next.indexOf('=') + 1, next.length()));
			}	
			if (next.startsWith("pixelsY")) {
				ypic = Integer.parseInt(next.substring(next.indexOf('=') + 1, next.length()));
			}
			if (next.startsWith("Sphere")) {
				Point center = pointify(next.substring(next.indexOf('=') + 2, next.indexOf(']')));
				double radius = Double.parseDouble(next.substring(next.indexOf("radius=")+7, next.length()));
				Sphere sphere = new Sphere(center, radius);
				Color ka = colorfy(scan);
				Color kd = colorfy(scan);
				Color ks = colorfy(scan);
				Color kr = colorfy(scan);
				BRDF barf = new BRDF(kd, ks, ka, kr);
				agg.add(new GeometricPrimitive(t, t, sphere, new Material(barf)));
			}
			if (next.startsWith("Triangle")) {
				System.out.println(next);
				Vector v1 = vectify(next.substring(next.indexOf('=') + 2, next.indexOf(']')));
				Vector v2 = vectify(next.substring(next.indexOf("v2=") + 4, next.indexOf("] v3")));
				Vector v3 = vectify(next.substring(next.indexOf("v3=")  + 4, next.length()-1));
				Triangle tri = new Triangle(v1, v2, v3);
				Color ka = colorfy(scan);
				Color kd = colorfy(scan);
				Color ks = colorfy(scan);
				Color kr = colorfy(scan);
				BRDF barfy = new BRDF(kd, ks, ka, kr);
				agg.add(new GeometricPrimitive(t, t, tri, new Material(barfy)));
			}
		}
		
		Sampler s = new Sampler(xpic, ypic);
		Film f = new Film(xpic, ypic, args[1]);
		Camera c = new Camera(eyepos, ul, ur, ll, lr);
		System.out.println(c);
		for (Light l : lights) {
			System.out.println(l);
		}
		System.out.println(agg);
		RayTracer r = new RayTracer(agg, lights, eyepos, 5);
		Scene scene = new Scene(s, c, f, r);
		scene.render();
		System.out.println("Whew.");
	}

	static Point pointify(String s) {
		Scanner q = new Scanner(s);
		double x = Double.parseDouble(q.next());
		double y = Double.parseDouble(q.next());
		double z = Double.parseDouble(q.next());
		return new Point(x, y, z);
	}

	static Vector vectify(String s) {
		Scanner q = new Scanner(s);
		double x = Double.parseDouble(q.next());
		double y = Double.parseDouble(q.next());
		double z = Double.parseDouble(q.next());
		return new Vector(x, y, z);
	}

	static Color colorfy(String s) {
		Scanner q = new Scanner(s);
		double x = Double.parseDouble(q.next());
		double y = Double.parseDouble(q.next());
		double z = Double.parseDouble(q.next());
		return new Color(x, y, z);
	}
	
	static Color colorfy(Scanner s) {
		String t = s.nextLine().trim();
		String q = t.substring(5, t.length() - 1);
		return colorfy(q);
	}
}
