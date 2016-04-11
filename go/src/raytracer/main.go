package main

import (
    "encoding/json"
	"fmt"
	"image"
	"image/color"
	"image/png"
	"math"
	"os"
	. "raytracer/vector"
)

const MAX_REFLECTIONS = 5
const MIN_INTERSECTION_TIME = 0.001
const MAX_COLOR = 255
var BLACK = color.RGBA{0, 0, 0, MAX_COLOR}

// type Vector vector.Vector

type Camera struct {
	Origin []float64
	ViewPlane struct {
		Lower_Left []float64
		Lower_Right []float64
		Upper_Left []float64
		Upper_Right []float64
	} `json:"view_plane"`
	PixelsX int
	PixelsY int
}

type Shading struct {
	Ambient []float64
	Diffuse []float64
	Reflection []float64
	Specular []float64
	Specular_Exponent float64
}

type Light struct {
	Type string
	Direction []float64
	Color []float64
}

type Ray struct {
	Position Vector
	Direction Vector
}

func (r Ray) valueAt(time float64) Vector {
	return r.Position.Plus(r.Direction.Times(time))
}

type Sphere struct {
	Center []float64
	Radius float64
	Shading Shading
}

func (s Sphere) intersect(ray Ray) (time float64, normal Ray) {
	center := BuildVector(s.Center)
	emc := ray.Position.Minus(center)

	a := ray.Direction.Dot(ray.Direction)
	b := 2 * ray.Direction.Dot(emc)
	c := emc.Dot(emc) - s.Radius * s.Radius

	discriminant := b * b - 4 * a * c
	if discriminant < 0 {
		return math.Inf(1), Ray{}
	}

	intersectionT1 := (-1 * b + math.Sqrt(discriminant)) / (2 * a)
	intersectionT2 := (-1 * b - math.Sqrt(discriminant)) / (2 * a)

	if math.Max(intersectionT1, intersectionT2) < MIN_INTERSECTION_TIME {
		return math.Inf(1), Ray{}
	}

	if intersectionT2 > MIN_INTERSECTION_TIME { time = intersectionT2 } else { time = intersectionT1 }
	intersectionPosition := ray.valueAt(time)
	normalDirection := ray.valueAt(time).Minus(center).Normalize()

	return time, buildRay(intersectionPosition, normalDirection)
}

type Scene struct {
	Name string
	Camera Camera
	Lights []Light
	Shapes []Sphere
}

func (s Scene) intersect(ray Ray) (shape Sphere, normal Ray, didIntersect bool) {
	thit := math.Inf(1)
	closestShape := Sphere{}
	closestNormal := Ray{}

	for _, shape := range s.Shapes {
		time, normal := shape.intersect(ray)
		if time < thit {
			thit = time
			closestShape = shape
			closestNormal = normal
		}
	}

	return closestShape, closestNormal, thit < math.Inf(1)
}

func main() {
	fmt.Printf("")
	scene := getScene()
	var img = image.NewRGBA(image.Rect(0, 0, scene.Camera.PixelsX, scene.Camera.PixelsY))
	bounds := img.Bounds()

	for y := bounds.Min.Y; y < bounds.Max.Y; y++ {
		for x := bounds.Min.X; x < bounds.Max.X; x++ {
			var col = calculateColor(scene, x, y)
			img.Set(x, y, col)
		}
	}

	writeImage(img)
}

func calculateColor(scene Scene, x, y int) color.RGBA {
	fx := float64(x)
	fy := float64(y)
	camera := scene.Camera
	origin := BuildVector(camera.Origin)
	pixelsX := float64(camera.PixelsX)
	pixelsY := float64(camera.PixelsY)
	viewPlane := camera.ViewPlane

	ul := BuildVector(viewPlane.Upper_Left)
	ur := BuildVector(viewPlane.Upper_Right)
	ll := BuildVector(viewPlane.Lower_Left)

	xpos := ul.X + (fx / pixelsX) * (ur.X - ul.X) + 1.0 / (2 * pixelsX)
	ypos := ul.Y + (fy / pixelsY) * (ll.Y - ul.Y) + 1.0 / (2 * pixelsY)
	screenpos := BuildVector([]float64{xpos, ypos, ul.Z})
	eyeRay := buildRay(origin, screenpos)

	return trace(eyeRay, scene)
}

func trace(ray Ray, scene Scene) color.RGBA {
	var origin Vector;
	traceWithReflections := func(ray Ray, scene Scene, numReflections int) color.RGBA {
		origin = ray.Position
		if (numReflections > MAX_REFLECTIONS) { return BLACK }

		shape, normal, didIntersect := scene.intersect(ray)

		if (!didIntersect) { return BLACK }

		shading := shape.Shading
		ambient := BuildVector(shading.Ambient)

		colorVector := BuildVector([]float64{0, 0, 0})

		for _, light := range scene.Lights {
			lightColor := BuildVector(light.Color)
			if numReflections == 0 {
				colorVector = colorVector.Plus(ambient.Vtimes(lightColor))
			}
			if light.Type == "directional" {
				// the genius of this is lightRay points from the point on the
				// object we're coloring back towards the light source and if
				// we hit anything along the way we know it's blocked
				lightRay := buildRay(normal.Position, BuildVector(light.Direction).Times(-1))
				_, _, lightBlocked := scene.intersect(lightRay)
				if !lightBlocked {
					colorVector = colorVector.Plus(diffuse(light, lightRay, shading, normal))
					colorVector = colorVector.Plus(specular(light, lightRay, shading, normal, origin))
				}
			}
		}

		colorVector = colorVector.Times(MAX_COLOR)

		return color.RGBA{floor(colorVector.X), floor(colorVector.Y), floor(colorVector.Z), MAX_COLOR}
	}

	return traceWithReflections(ray, scene, 0)
}

func diffuse(light Light, lightRay Ray, shading Shading, normal Ray) Vector {
	l := lightRay.Direction.Normalize()
	dot := l.Dot(normal.Direction)
	kd := BuildVector(shading.Diffuse)

	return BuildVector(light.Color).Vtimes(kd).Times(dot)
}

func specular(light Light, lightRay Ray, shading Shading, normal Ray, origin Vector) Vector {
	l := lightRay.Direction.Normalize()
	n := normal.Direction
	r := l.Times(-1).Plus(n.Times(2 * n.Dot(l))).Normalize()
	v := origin.Minus(normal.Position).Normalize()
	ks := BuildVector(shading.Specular)
	sp := shading.Specular_Exponent
	dot := math.Max(r.Dot(v), 0)
	dot = math.Pow(dot, sp)

	return BuildVector(light.Color).Vtimes(ks).Times(dot)
}

func floor(x float64) uint8 {
	if (x > MAX_COLOR) { return MAX_COLOR }
	return uint8(math.Floor(x))
}

func buildRay(position, direction Vector) Ray {
	return Ray{
		Position: position,
		Direction: direction,
	}
}

func getScene() Scene {
	if len(os.Args) < 2 {
		panic("specify a JSON file containing a scene")
	}
	file := os.Args[1]
	configFile, err := os.Open(file)
	panick(err)

	jsonParser := json.NewDecoder(configFile)
	var scene Scene
	panick(jsonParser.Decode(&scene))

	return scene
}

func panick(err error) {
	if (err != nil) {
		panic(err)
	}
}

func writeImage(img *image.RGBA) {
	f, err := os.Create("draww.png")
	if err != nil {
		panic(err)
	}
	defer f.Close()
	png.Encode(f, img)
}
