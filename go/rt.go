package main

import (
    "encoding/json"
	"fmt"
	"image"
	"image/color"
	"image/png"
	"math"
	"os"
)

const MAX_REFLECTIONS = 5
const MIN_INTERSECTION_TIME = 0.001
var BLACK = color.RGBA{0, 0, 0, 255}

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

type Vector struct {
	x float64
	y float64
	z float64
}

func (v1 Vector) plus(v2 Vector) Vector {
	return buildVector([]float64{v1.x + v2.x, v1.y + v2.y, v1.z + v2.z})
}

func (v1 Vector) minus(v2 Vector) Vector {
	return buildVector([]float64{v1.x - v2.x, v1.y - v2.y, v1.z - v2.z})
}

func (v1 Vector) times(m float64) Vector {
	return buildVector([]float64{v1.x * m, v1.y * m, v1.z * m})
}

func (v1 Vector) vtimes(v2 Vector) Vector {
	return buildVector([]float64{v1.x * v2.x, v1.y * v2.y, v1.z * v2.z})
}

func (v1 Vector) dot(v2 Vector) float64 {
	return (v1.x * v2.x) + (v1.y * v2.y) + (v1.z * v2.z)
}

func (v Vector) norm() float64 {
	return math.Sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
}

func (v Vector) normalize() Vector {
	norm := v.norm()
	if norm == 0 { panic("Tried to normalize a zero vector") }

	return buildVector([]float64{v.x / norm, v.y / norm, v.z / norm})
}

type Ray struct {
	Position Vector
	Direction Vector
}

func (r Ray) valueAt(time float64) Vector {
	return r.Position.plus(r.Direction.times(time))
}

type Sphere struct {
	Center []float64
	Radius float64
	Shading Shading
}

func (s Sphere) intersect(ray Ray) (time float64, normal Ray) {
	center := buildVector(s.Center)
	emc := ray.Position.minus(center)

	a := ray.Direction.dot(ray.Direction)
	b := 2 * ray.Direction.dot(emc)
	c := emc.dot(emc) - s.Radius * s.Radius

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
	normalDirection := ray.valueAt(time).minus(center).normalize()

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
	origin := buildVector(camera.Origin)
	pixelsX := float64(camera.PixelsX)
	pixelsY := float64(camera.PixelsY)
	viewPlane := camera.ViewPlane

	ul := buildVector(viewPlane.Upper_Left)
	ur := buildVector(viewPlane.Upper_Right)
	ll := buildVector(viewPlane.Lower_Left)

	xpos := ul.x + (fx / pixelsX) * (ur.x - ul.x) + 1.0 / (2 * pixelsX)
	ypos := ul.y + (fy / pixelsY) * (ll.y - ul.y) + 1.0 / (2 * pixelsY)
	screenpos := buildVector([]float64{xpos, ypos, ul.z})
	eyeRay := buildRay(origin, screenpos)

	return trace(eyeRay, scene)
}

func trace(ray Ray, scene Scene) color.RGBA {
	var origin Vector;
	traceWithReflections := func(ray Ray, scene Scene, numReflections int) color.RGBA {
		origin = ray.Position
		if (numReflections > MAX_REFLECTIONS) { return BLACK }

		shape, _, didIntersect := scene.intersect(ray)

		if (!didIntersect) { return BLACK }

		colorVector := buildVector([]float64{0, 0, 0})
		ambient := buildVector(shape.Shading.Ambient)
		for _, light := range scene.Lights {
			lightColor := buildVector(light.Color)
			if numReflections == 0 {
				colorVector = colorVector.plus(ambient.vtimes(lightColor))
			}
		}

		colorVector = colorVector.times(255)

		return color.RGBA{floor(colorVector.x), floor(colorVector.y), floor(colorVector.z), 255}
	}

	return traceWithReflections(ray, scene, 0)
}

func floor(x float64) uint8 {
	return uint8(math.Floor(x))
}

func buildVector(elements []float64) Vector {
	return Vector{
		x: elements[0],
		y: elements[1],
		z: elements[2],
	}
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
