package raytracer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Main {

	/** Where the magic happens **/
	public static void main(String[] args) throws IOException {
		System.out.println("Okay, let's roll.");
		File frack = new File("temp.picture");
		frack.createNewFile();

		PrintWriter printer = new PrintWriter("temp.picture", "UTF-8");
		Scanner fake = new Scanner(new File(args[0]));
		while (fake.hasNext()) {
			printer.println(fake.nextLine());
		}
		int obj = 1;
		for (; obj < args.length && args[obj].endsWith(".obj"); obj++) {
			String read = readOBJ(args[obj]);
			printer.println(read);
		}
		printer.close();
		Scanner scan = null;
		Point eyepos = new Point(0, 0, 0);
		int xpic = 100;
		int ypic = 100;
		Vector ul = new Vector(-1, 1, -1);
		Vector ur = new Vector(1, 1, -1);
		Vector lr = new Vector(1, -1, -1);
		Vector ll = new Vector(-1, -1, -1);
		Matrix idenity = MatrixMathematics.identity(4);
		Transformation t = new Transformation(idenity);
		AggregatePrimitive agg = new AggregatePrimitive();
		ArrayList<Light> lights = new ArrayList<Light>();

		try {
			scan = new Scanner(new File("temp.picture"));
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
			if (next.startsWith("PL")) {
				lights.add(new PointLight(pointify(next.substring(
						next.indexOf(',') + 2, next.length())), colorfy(next
						.substring(5, next.indexOf(',') - 2))));
			}
			if (next.startsWith("pixelsX")) {
				xpic = Integer.parseInt(next.substring(next.indexOf('=') + 1,
						next.length()));
			}
			if (next.startsWith("pixelsY")) {
				ypic = Integer.parseInt(next.substring(next.indexOf('=') + 1,
						next.length()));
			}
			if (next.startsWith("Sphere")) {
				Point center = pointify(next.substring(next.indexOf('=') + 2,
						next.indexOf(']')));
				double radius = Double.parseDouble(next.substring(
						next.indexOf("radius=") + 7, next.length()));
				Sphere unit = new Sphere(new Point(0, 0, 0), 1);
				Matrix scale = MatrixMathematics.identity(4)
						.multiplyByConstant(radius);
				scale.setValueAt(3, 3, 1.0);
				Matrix translate = MatrixMathematics.identity(4);
				translate.setValueAt(0, 3, center.getX());
				translate.setValueAt(1, 3, center.getY());
				translate.setValueAt(2, 3, center.getZ());
				Matrix m = MatrixMathematics.multiply(translate, scale);
				Transformation objToWorld = new Transformation(m);
				Transformation worldToObj = new Transformation(
						MatrixMathematics.inverse(m));

				Color ka = colorfy(scan);
				Color kd = colorfy(scan);
				Color ks = colorfy(scan);
				Color kr = colorfy(scan);
				BRDF barf = new BRDF(kd, ks, ka, kr);
				agg.add(new GeometricPrimitive(objToWorld, worldToObj, unit,
						new Material(barf)));
			}

			if (next.startsWith("Ellipsoid")) {
				Point center = pointify(next.substring(next.indexOf('=') + 2,
						next.indexOf(']')));
				double rx = Double.parseDouble(next.substring(
						next.indexOf("rx=") + 3, next.indexOf("ry")));
				double ry = Double.parseDouble(next.substring(
						next.indexOf("ry=") + 3, next.indexOf("rz")));
				double rz = Double.parseDouble(next.substring(
						next.indexOf("rz=") + 3, next.length()));
				Sphere unit = new Sphere(new Point(0, 0, 0), 1);
				Matrix scale = MatrixMathematics.identity(4);
				scale.setValueAt(0, 0, rx);
				scale.setValueAt(1, 1, ry);
				scale.setValueAt(2, 2, rz);
				Matrix translate = MatrixMathematics.identity(4);
				translate.setValueAt(0, 3, center.getX());
				translate.setValueAt(1, 3, center.getY());
				translate.setValueAt(2, 3, center.getZ());
				Matrix rot = rottify(scan);
				Matrix m = MatrixMathematics.multiply(rot, scale);
				m = MatrixMathematics.multiply(translate, m);
				Transformation objToWorld = new Transformation(m);
				Transformation worldToObj = new Transformation(
						MatrixMathematics.inverse(m));
				Color ka = colorfy(scan);
				Color kd = colorfy(scan);
				Color ks = colorfy(scan);
				Color kr = colorfy(scan);
				BRDF barf = new BRDF(kd, ks, ka, kr);
				agg.add(new GeometricPrimitive(objToWorld, worldToObj, unit,
						new Material(barf)));
			}

			if (next.startsWith("Triangle")) {
				Vector v1 = vectify(next.substring(next.indexOf('=') + 2,
						next.indexOf(']')));
				Vector v2 = vectify(next.substring(next.indexOf("v2=") + 4,
						next.indexOf("] v3")));
				Vector v3 = vectify(next.substring(next.indexOf("v3=") + 4,
						next.length() - 1));
				Triangle tri = new Triangle(v1, v2, v3);
				Color ka = colorfy(scan);
				Color kd = colorfy(scan);
				Color ks = colorfy(scan);
				Color kr = colorfy(scan);
				BRDF barfy = new BRDF(kd, ks, ka, kr);
				agg.add(new GeometricPrimitive(t, t, tri, new Material(barfy)));
			}
			if (next.startsWith("NormalTriangle")) {
				Vector v1 = badVectify(next.substring(next.indexOf('=') + 2,
						next.indexOf(']')));
				Vector v2 = badVectify(next.substring(next.indexOf("v2=") + 4,
						next.indexOf("] v3")));
				Vector v3 = badVectify(next.substring(next.indexOf("v3=") + 4,
						next.length() - 1));
				Color ka = colorfy(scan);
				Color kd = colorfy(scan);
				Color ks = colorfy(scan);
				Color kr = colorfy(scan);
				BRDF barfy = new BRDF(kd, ks, ka, kr);
				next = scan.nextLine();
				Normal n1 = normalfy(next.substring(next.indexOf("n1=") + 4,
						next.indexOf(']')));
				Normal n2 = normalfy(next.substring(next.indexOf("n2=") + 4,
						next.indexOf("] n3")));
				Normal n3 = normalfy(next.substring(next.indexOf("n3=") + 4,
						next.length() - 1));
				NormalTriangle tri = new NormalTriangle(v1, v2, v3, n1, n2, n3);
				agg.add(new GeometricPrimitive(t, t, tri, new Material(barfy)));
			}
		}

		Sampler s = new Sampler(xpic, ypic);
		Film f = new Film(xpic, ypic, args[obj]);
		Camera c = new Camera(eyepos, ul, ur, ll, lr);
		System.out.println(c);
		System.out.println("Lights");
		for (Light l : lights) {
			System.out.println(l);
		}
		System.out.println(agg);
		RayTracer r = new RayTracer(agg, lights, eyepos, 5);
		Scene scene = new Scene(s, c, f, r);
		scene.render();
		frack.delete();
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

	static Vector badVectify(String s) {
		Scanner q = new Scanner(s);
		double x = Double.parseDouble(q.next());
		double y = Double.parseDouble(q.next());
		double z = Double.parseDouble(q.next());
		return new Vector(x, y, z - 8);
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

	static Normal normalfy(String s) {
		Scanner q = new Scanner(s);
		double x = Double.parseDouble(q.next());
		double y = Double.parseDouble(q.next());
		double z = Double.parseDouble(q.next());
		return new Normal(x, y, z);
	}

	static Matrix rottify(Scanner s) {
		String t = s.nextLine().trim();
		return rottify(t);
	}

	static Matrix rottify(String s) {
		Scanner q = new Scanner(s);
		Matrix[] mats = new Matrix[3];
		int mat = 0;
		while (q.hasNext()) {
			String rot = q.next();
			mats[mat++] = rotation(rot.charAt(3) - 'x',
					Math.toRadians(Double.parseDouble(rot.substring(5))));
		}
		Matrix id = MatrixMathematics.identity(4);
		for (Matrix m : mats) {
			id = MatrixMathematics.multiply(m, id);
		}
		return id;
	}

	// Rotation matrix of angle theta about axis axis
	static Matrix rotation(int axis, double theta) {
		Matrix ret = MatrixMathematics.identity(4);
		if (axis == 0) {
			ret.setValueAt(1, 1, Math.cos(theta));
			ret.setValueAt(1, 2, -Math.sin(theta));
			ret.setValueAt(2, 1, Math.sin(theta));
			ret.setValueAt(2, 2, Math.cos(theta));
		}
		if (axis == 1) {
			ret.setValueAt(0, 0, Math.cos(theta));
			ret.setValueAt(0, 2, -Math.sin(theta));
			ret.setValueAt(2, 0, Math.sin(theta));
			ret.setValueAt(2, 2, Math.cos(theta));
		}
		if (axis == 2) {
			ret.setValueAt(0, 0, Math.cos(theta));
			ret.setValueAt(0, 1, -Math.sin(theta));
			ret.setValueAt(1, 0, Math.sin(theta));
			ret.setValueAt(1, 1, Math.cos(theta));
		}
		return ret;
	}

	static String readOBJ(String x) {
		Scanner s = null;
		HashMap<Integer, Point> vertexes = new HashMap<Integer, Point>();
		HashMap<Integer, Normal> normals = new HashMap<Integer, Normal>();
		int vector = 1;
		int normal = 1;
		String ret = "";
		try {
			s = new Scanner(new File(x));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (s.hasNext()) {
			String next = s.nextLine().trim();
			if (next.startsWith("v ")) {
				vertexes.put(vector, pointify(next.substring(1)));
				vector += 1;
				continue;
			}
			if (next.startsWith("vn ")) {
				normals.put(normal, normalfy(next.substring(2)));
				normal += 1;
				continue;
			}
			if (next.startsWith("f ")) {
				Scanner k = new Scanner(next.substring(1));
				String first = k.next();
				Point v1;
				Normal n1 = null;
				if (first.contains("//")) {
					v1 = vertexes.get(Integer.parseInt(first.substring(0,
							first.indexOf("/"))));
					n1 = normals.get(Integer.parseInt(first.substring(first
							.indexOf("//") + 2)));
				} else {
					v1 = vertexes.get(Integer.parseInt(first));
				}
				String second = k.next();
				Point v2;
				Normal n2 = null;
				if (second.contains("//")) {
					v2 = vertexes.get(Integer.parseInt(second.substring(0,
							second.indexOf("/"))));
					n2 = normals.get(Integer.parseInt(second.substring(second
							.indexOf("//") + 2)));
				} else {
					v2 = vertexes.get(Integer.parseInt(second));
				}
				String third = k.next();
				Point v3;
				Normal n3 = null;
				if (third.contains("//")) {
					v3 = vertexes.get(Integer.parseInt(third.substring(0,
							third.indexOf("/"))));
					n3 = normals.get(Integer.parseInt(third.substring(third
							.indexOf("//") + 2)));
				} else {
					v3 = vertexes.get(Integer.parseInt(third));
				}
				if (n1 != null) {
					ret += "NormalTriangle with v1=" + stringate(v1) + " v2="
							+ stringate(v2) + " v3=" + stringate(v3) + "\n"
							+ brdf() + "n1=" + stringate(n1) + " n2="
							+ stringate(n2) + " n3=" + stringate(n3) + "\n\n";
				} else {
					ret += "Triangle with v1=" + stringate(v1) + " v2="
							+ stringate(v2) + " v3=" + stringate(v3) + "\n"
							+ brdf();
				}
			}
		}
		return ret;
	}

	static String stringate(Point p) {
		return "[ " + p.getX() + "  " + p.getY() + "  " + p.getZ() + " ]";
	}

	static String stringate(Normal p) {
		return "[ " + p.getX() + "  " + p.getY() + "  " + p.getZ() + " ]";
	}

	static String brdf() {
		Random randy = new Random();
		boolean boo1 = randy.nextBoolean();
		boolean boo2 = randy.nextBoolean();
		if (boo1 && boo2) {
			return "ka=[ 0.1  0.1  0.1]\nkd=[ 0.  1.  1.]\nks=[ 1.  1.  1.]\nkr=[ 0.  0.  0.]\n";
		} else {
			if (boo1) {
				return "ka=[ 0.1  0.1  0.1]\nkd=[ 1.  0.  1.]\nks=[ 1.  1.  1.]\nkr=[ 0.  0.  0.]\n";
			} else {
				if (boo2) {
					return "ka=[ 0.1  0.1  0.1]\nkd=[ 1.  1. 0 ]\nks=[ 1.  1.  1.]\nkr=[ 0.  0.  0.]\n";
				}
			}
		}
		return "ka=[ 0.1  0.1  0.1]\nkd=[ 1.  1.  1.]\nks=[ 1.  1.  1.]\nkr=[ 0.  0.  0.]\n";
	}
}
