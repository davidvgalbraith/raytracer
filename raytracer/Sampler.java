package raytracer;

public class Sampler {
	double maxx;
	double maxy;

	public Sampler(double maxx, double maxy) {
		this.maxx = maxx;
		this.maxy = maxy;
	}

	boolean getSample(Sample x) {
		if (x.getY() > 1) {
			return false;
		}
		if (x.getX() + 1.0/maxx >= 1) {
			x.setX(0.5/maxx);
			x.setY(x.getY() + 1.0/maxy);
			return true;
		} else {
			x.setX(x.getX() + 1.0/maxx);
			return true;
		}
	}
}
