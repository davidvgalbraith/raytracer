package raytracer;

public interface Primitive {
    boolean intersect(Ray ray, Doublet thit, Intersection in);     

    boolean intersectP(Ray ray);

    void getBRDF(LocalGeo local, BRDF brdf);
}
   
