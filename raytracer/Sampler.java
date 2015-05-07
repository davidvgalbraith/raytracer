package raytracer;

/*sequentially returns coordinates of centers of vertices (useless)*/
public class Sampler {
	double maxx;
	double maxy;

	public Sampler(double maxx, double maxy) {
		this.maxx = maxx;
		this.maxy = maxy;
	}

	boolean generateSample(Sample x) {

		if (x.getX() + 1.0/maxx >= 1) {
			x.setX(0.5/maxx);
			x.setY(x.getY() + 1.0/maxy);
			if (x.getY() >= 1) {
				return false;
			}
			return true;
		} else {
			x.setX(x.getX() + 1.0/maxx);
			return true;
		}
	}
}
