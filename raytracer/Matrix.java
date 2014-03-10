package raytracer;

public class Matrix {
	double m[][];

	public Matrix(double[][] x) {
		this.m = x;
	}

	public Matrix() {
		this.m = new double[4][4];
	}

	public double[][] getM() {
		return m;
	}

	public void setM(double[][] m) {
		this.m = m;
	}
}
