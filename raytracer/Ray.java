package raytracer;

public class Ray {

	Point pos;
	Vector dir;
	double tmin, tmax;

	public Ray(Point pos, Vector dir, double tmin, double tmax) {
		this.pos = pos;
		this.dir = dir;
		this.tmin = tmin;
		this.tmax = tmax;
		if (pos != null) {
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
		}
	}
	Point eval(double dub) {
		if (dub < tmin || dub > tmax) {
			System.err.println("Asked me to eval outside range");
			System.exit(1);
		}
		return new Point(x + dir.getX() * dub, y + dir.getY() * dub, z + dir.getZ() * dub);
	}
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	double x, y, z;

	public Point getPos() {
		return pos;
	}

	public void setPos(Point pos) {
		this.pos = pos;
	}

	public Vector getDir() {
		return dir;
	}

	public void setDir(Vector dir) {
		this.dir = dir;
	}

	public double getTmin() {
		return tmin;
	}

	public void setTmin(double tmin) {
		this.tmin = tmin;
	}

	public double getTmax() {
		return tmax;
	}

	public void setTmax(double tmax) {
		this.tmax = tmax;
	}

	public String toString() {
		return "a ray from " + pos + " going " + dir;
	}

}
