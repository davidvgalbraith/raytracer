package raytracer;

public class Material {
	BRDF constantBRDF;

	public Material(BRDF constantBRDF) {
		super();
		this.constantBRDF = constantBRDF;
	}
	public Material() {
		this.constantBRDF = new BRDF(new Color(0.3, 0.7, 0.2), new Color(0.5, 0.1, 0.9), new Color(0.8, 0.2, 0.4), new Color(0, 0, 0));
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
   
