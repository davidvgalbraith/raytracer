package raytracer;

public class Camera {
	Point eyepos;
	//camera coordinates
	Vector u, v, w;
	Vector ul, ur, ll, lr;

	public Camera(Point eyepos, Vector ul, Vector ur, Vector ll, Vector lr) {
		this.eyepos = eyepos;
		this.ul = ul;
		this.ur = ur;
		this.ll = ll;
		this.lr = lr;
	}


	public Camera(Point eyepos, Vector lookat, Vector up, double fov) {
		this.eyepos = eyepos;
		this.w = lookat.divide(Math.sqrt(lookat.dot(lookat)));
		Vector cross = up.cross(w);
		this.u = cross.normalize();
		this.v = (this.w).cross(this.u);
	}


	void generateRay(Sample samp, Ray ray) {
//		System.out.println(samp);
		ray.setPos(eyepos);
		double u = 1 - samp.getX();
		double v = 1 - samp.getY();
//		System.out.println(ll.times(v));
//		System.out.println(ul.times(1-v));
		Vector pv1 = (ll.times(v).plus(ul.times(1 - v))).times(u);
		Vector pv2 = (lr.times(v).plus(ur.times(1 - v))).times(1-u);
		Vector pv = pv1.plus(pv2);
//		System.out.println(pv1);
//		System.out.println(pv2);
//		System.out.println(pv);
		Point p = new Point(pv.getX(), pv.getY(), pv.getZ());
		ray.setDir(p.minus(eyepos));
	}
	
	public String toString() {
		return "Camera\n Eyepos: " + eyepos + "\n ll " + ll + "\n ul " + ul + "\n lr " + lr + "\n ur " + ur;
	}
}
