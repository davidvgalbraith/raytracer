package raytracer;

public class Scene {
	// scene kids only
	Sampler sampler;
	Camera camera;
	Film film;
	RayTracer raytracer;

	public Scene(Sampler sampler, Camera camera, Film film, RayTracer raytracer) {
		this.sampler = sampler;
		this.camera = camera;
		this.film = film;
		this.raytracer = raytracer;
	}

	void render() {
		Sample sample = new Sample(1, -0.5 / sampler.maxy);
		Ray ray = new Ray(null, null, 0.01, Double.MAX_VALUE);
		while (sampler.generateSample(sample)) {
			Color color = new Color(0, 0, 0);

			camera.generateRay(sample, ray);
//			System.out.println(sample + " generaged " + ray);
			raytracer.trace(ray, 0, color);
			//System.out.println("Committing " + color);
			film.commit(sample, color);

		}

		film.writeImage();
	}
	
	

}
