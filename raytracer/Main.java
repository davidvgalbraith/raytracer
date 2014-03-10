package raytracer;

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
		RayTracer r = new RayTracer();
		Scene scene = new Scene(s, c, f, r);
		scene.render();
		System.out.println("Whew.");
	}
	
}
