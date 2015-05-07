package raytracer;

public class BRDF {
	// kd, ks, ka are diffuse, specular and ambient component respectively

	// kr is the mirror reflection coefficient

	Color kd, ks, ka, kr;
	public BRDF(Color kd, Color ks, Color ka, Color kr) {
		this.kd = kd;
		this.ks = ks;
		this.ka = ka;
		this.kr = kr;
	}
	public BRDF() {}
	public String toString() {
		return "BRDF:\nkd = " + kd + "\nks = " + ks + "\nka = " + ka + "\nkr = " + kr;
	}
	public Color getKd() {
		return kd;
	}
	public void setKd(Color kd) {
		this.kd = kd;
	}
	public Color getKs() {
		return ks;
	}
	public void setKs(Color ks) {
		this.ks = ks;
	}
	public Color getKa() {
		return ka;
	}
	public void setKa(Color ka) {
		this.ka = ka;
	}
	public Color getKr() {
		return kr;
	}
	public void setKr(Color kr) {
		this.kr = kr;
	}
}
