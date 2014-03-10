package raytracer;

public class Camera {
	Point eyepos;
	Vector lookat;
	Vector up;
	Vector ul, ur, ll, lr;
	double n;

	public Camera(Point eyepos, Vector lookat, Vector up, Vector ul, Vector ur,
			Vector ll, Vector lr) {
		this.eyepos = eyepos;
		this.lookat = lookat;
		this.up = up;
		this.ul = ul;
		this.ur = ur;
		this.ll = ll;
		this.lr = lr;
	}


	void generateRay(Sample samp, Ray ray) {
		ray.setPos(eyepos);
		Vector pv1 = (ll.times(samp.getY()).plus(ul.times(1 - samp.getY()))).times(samp.getX());
		Vector pv2 = (lr.times(samp.getY()).plus(ur.times(1 - samp.getY()))).times(1 - samp.getX());
		Vector pv = pv1.plus(pv2);
		Point p = new Point(pv.getX(), pv.getY(), -1);
		ray.setDir(p.minus(eyepos));
	}
}
