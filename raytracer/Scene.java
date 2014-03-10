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
		Ray ray = new Ray(null, null, 1, 100);
		Color color = new Color(0, 0, 0);
		while (sampler.generateSample(sample)) {
			camera.generateRay(sample, ray);
			System.out.println(sample + " generaged " + ray);
			raytracer.trace(ray, 5, color);

			film.commit(sample, color);

		}

		film.writeImage();
	}
	
	

}
