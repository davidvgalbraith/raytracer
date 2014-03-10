package raytracer;

public class Material {
	BRDF constantBRDF;

	public Material(BRDF constantBRDF) {
		super();
		this.constantBRDF = constantBRDF;
	}
	public Material() {
		this.constantBRDF = new BRDF(new Color(1.0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0));
	}
	public void getBRDF(LocalGeo local, BRDF brdf) {
		brdf.setKa(constantBRDF.getKa());
		brdf.setKd(constantBRDF.getKd());
		brdf.setKs(constantBRDF.getKs());
		brdf.setKr(constantBRDF.getKr());
	}

	public void setBRDF(BRDF constantBRDF) {
		this.constantBRDF = constantBRDF;
	}
	
}
