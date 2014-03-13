package raytracer;

public class Point {
	
	double x, y, z;

	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	Vector toVect() {
		return new Vector(x, y, z);
	}
	Point times(double a) {
		return new Point(x * a, y * a, z * a);
	}
	
	Vector minus(Point a) {
		return new Vector(x - a.getX(), y - a.getY(), z - a.getZ());
	}
	
	Point minus(Vector a) {
		return new Point(x - a.getX(), y - a.getY(), z - a.getZ());
	}
	
	Point plus(Vector a) {
		return new Point(x + a.getX(), y + a.getY(), z + a.getZ());
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
	
	public String toString() {
		return "the point (" + x + ", " + y + ", " + z + ")";
	}

}
