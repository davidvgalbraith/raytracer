package raytracer;

import java.util.HashSet;

public class Garbage {
	//Admits values only between tmin and tmax
	//and finds the smaller one
	double tmin, tmax;
	HashSet<Double> set;
	public Garbage(double tmin, double tmax) {
		this.set = new HashSet<Double>();
		this.tmin = tmin;
		this.tmax = tmax;
	}
	public void add(double x) {
		if (x < tmin || x > tmax) {
			return;
		}
		set.add(x);
	}
	public int size() {
		return set.size();
	}
	public double smaller() {
		double d = Double.MAX_VALUE;
		for (Double dd : set) {
			if (Math.abs(dd) < d) {
				d = dd;
			}
		}
		return d;
	}
}
