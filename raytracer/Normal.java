package raytracer;

public class Normal {
	double x, y, z;

	public Normal(double x, double y, double z) {
		double norm = Math.sqrt(x * x + y * y + z * z);
		if (norm == 0) {
			System.err.println("Tried to construct a zero normal");
			this.x = x;
			this.y = y;
			this.z = z;
		} else {
			this.x = x/norm;
			this.y = y/norm;
			this.z = z/norm;
		}
	}
	public static Normal normalize(Vector a) {
		return new Normal(a.getX(), a.getY(), a.getZ());
	}
	public Normal plus(Normal a) {
		return new Normal(x + a.getX(), y + a.getY(), z + a.getZ());
	}

	public Normal minus(Normal a) {
		return new Normal(x - a.getX(), y - a.getY(), z - a.getZ());
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
	
}
