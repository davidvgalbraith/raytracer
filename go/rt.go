package main

import (
    "encoding/json"
	"fmt"
	"image"
	"image/color"
	"image/png"
	"os"
)

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

type Ray struct {
	Position Vector
	Direction Vector
}

type Sphere struct {
	Center []float64
	Radius float64
	Shading Shading
}

// returns the time value of intersection and the normal Ray
func (s Sphere) intersect(r Ray) (float64, Ray) {
	return -1.0, Ray{}
}

type Scene struct {
	Name string
	Camera Camera
	Lights []Light
	Shapes []Sphere
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
	pixelsX := float64(camera.PixelsX)
	pixelsY := float64(camera.PixelsY)
	viewPlane := camera.ViewPlane

	ul := buildVector(viewPlane.Upper_Left)
	ur := buildVector(viewPlane.Upper_Right)
	ll := buildVector(viewPlane.Lower_Left)

	xpos := ul.x + (fx / pixelsX) * (ur.x - ul.x) + 1.0 / (2 * pixelsX)
	ypos := ul.y + (fy / pixelsY) * (ll.y - ul.y) + 1.0 / (2 * pixelsY)

	return color.RGBA{0, 255, 0, 255}
}

func buildVector(elements []float64) Vector {
	return Vector{
		x: elements[0],
		y: elements[1],
		z: elements[2],
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
