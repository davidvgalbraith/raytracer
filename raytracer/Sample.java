package raytracer;

public class Sample {
	double x, y; // store screen coordinate

	public Sample(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public String toString() {
		return "Sample: (" + x + ", " + y + ")";
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

}
   
