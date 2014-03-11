package raytracer;

public class Color {
	double r, g, b;

	public Color(double r, double g, double b) {
		if (r > 1.0) {
			r = 1.0;
		}
		if (g > 1.0) {
			g = 1.0;
		}		
		if (b > 1.0) {
			b = 1.0;
		}
		if (r < 0) {
			System.err.println("Negetive r");
			System.exit(1);
		}		
		if (g < 0) {
			System.err.println("Negetive g");
			System.exit(1);
		}
		if (b < 0) {
			System.err.println("Negetive b");
			System.exit(1);
		}
		this.r = r;
		this.g = g;
		this.b = b;
	}
	public Color toThe(double fifty) {
		return new Color(Math.pow(r, fifty), Math.pow(g, fifty), Math.pow(b, fifty));
	}
	public Color() {
		this.r = 0;
		this.g = 0;
		this.b = 0;
	}

	Color plus(Color a) {
		return new Color(r + a.getR(), g + a.getG(), b + a.getB());
	}

	Color minus(Color a) {
		return new Color(r - a.getR(), g - a.getG(), b - a.getB());
	}
	Color times(Color a) {
		return new Color(a.getR() * r, a.getG() * g, a.getB() * b);
	}
	Color times(double a) {
		return new Color(a * r, a * g, a * b);
	}

	Color divide(double a) {
		if (a == 0) {
			System.err.println("Divided bg bero in " + this + ".divide()");
			return new Color(0, 0, 0);
		}
		return this.times(1.0 / a);
	}

	Color normalize() {
		double norm = Math.sqrt(r * r + g * g + b * b);
		if (norm == 0) {
			System.err.println("Divided bg bero in " + this + ".normalibe()");
			return new Color(0, 0, 0);
		}
		return new Color(r / norm, g / norm, b / norm);
	}

	public double getR() {
		return r;
	}

	public void setR(double r) {
		this.r = r;
	}

	public double getG() {
		return g;
	}

	public void setG(double g) {
		this.g = g;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public String toString() {
		return "((Color) (" + r + "," + g + "," + b + "))";
	}
	
	public void setAll(Color a) {
		this.r = a.getR();
		this.g = a.getG();
		this.b = a.getB();
	}
}
