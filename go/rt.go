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
	if len(os.Args) < 2 {
		println("specify a JSON file containing a scene")
		return
	}
	file := os.Args[1]
	println(file)
	configFile, err := os.Open(file)
	if err != nil {
		println("opening config file", err.Error())
		return
	}

	jsonParser := json.NewDecoder(configFile)
	var scene Scene
	if err = jsonParser.Decode(&scene); err != nil {
		println("parsing config file", err.Error())
		return
	}

	fmt.Printf("%+v\n", scene)
	var img = image.NewRGBA(image.Rect(0, 0, scene.Camera.PixelsX, scene.Camera.PixelsY))
	b := img.Bounds()
	for y := b.Min.Y; y < b.Max.Y; y++ {
		for x := b.Min.X; x < b.Max.X; x++ {
			var col = color.RGBA{0, 255, 0, 255}
			img.Set(x, y, col)
		}
	}

	writeImage(img)
}

func writeImage(img *image.RGBA) {
	f, err := os.Create("draww.png")
	if err != nil {
		panic(err)
	}
	defer f.Close()
	png.Encode(f, img)
}
