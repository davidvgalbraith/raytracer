package main

import (
    "encoding/json"
	"fmt"
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
	// var scene map[string]interface{}
	if err = jsonParser.Decode(&scene); err != nil {
		println("parsing config file", err.Error())
		return
	}

	fmt.Printf("%+v\n", scene)
	// println("call 91 now")
	// camera := scene["camera"].(map[string] interface{})
	// fmt.Printf("%+v\n", camera)
}
