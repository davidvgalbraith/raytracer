package raytracer;

public class Vector {
	double x, y, z;
	public Vector(double x, double y, double z) {
		this.x = x;	
		this.y = y;
		this.z = z;
	}
	public Vector() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	Vector cross(Vector a) {
		return new Vector(y * a.getZ() - z * a.getY(), z * a.getX() - x * a.getZ(), x * a.getY() - y * a.getX());
	}
	Vector plus(Vector a) {
		return new Vector(x + a.getX(), y + a.getY(), z + a.getZ());
	}
	double dot(Vector a) {
		return x * a.getX() + y * a.getY() + z * a.getZ();
	}
	Vector minus(Vector a) {
		return new Vector(x - a.getX(), y - a.getY(), z - a.getZ());
	}
	
	Vector times(double a) {
		return new Vector(a * x, a * y, a * z);
	}

	Vector divide(double a) {
		if (a == 0) {
			System.err.println("Divided by zero in " + this + ".divide()");
			return new Vector(0, 0, 0);
		}
		return this.times(1.0 / a);
	}
	
	Vector normalize() {
		double norm = Math.sqrt(x * x + y * y + z * z);
		if (norm == 0) {
			System.err.println("Divided by zero in " + this + ".normalize()");
			return new Vector(0, 0, 0);
		}
		return new Vector(x/norm, y/norm, z/norm);
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
		return "((Vector) (" + x + "," + y + "," + z + "))";
	}
	
}
