package scene

import (
    . "raytracer/geometry"
    . "raytracer/common"
    . "raytracer/scene_objects"
    . "raytracer/shapes"
    "encoding/json"
    "math"
    "os"
)

type Shape interface {
    Intersect(ray Ray) (time float64, normal Ray)
    GetShading() Shading
}

type ShapeCard struct {
    Type string
    Center []float64
    Radius float64
    Shading Shading
}

type Scene struct {
	Name string
	Camera Camera
	Lights []Light
	ShapeCards []ShapeCard `json:"shapes"`
    Shapes []Shape
}

func (s Scene) Intersect(ray Ray) (shape Shape, normal Ray, didIntersect bool) {
	thit := math.Inf(1)
	var closestShape Shape
	var closestNormal Ray

	for _, shape := range s.Shapes {
		time, normal := shape.Intersect(ray)
		if time < thit {
			thit = time
			closestShape = shape
			closestNormal = normal
		}
	}

	return closestShape, closestNormal, thit < math.Inf(1)
}

func GetScene() Scene {
	if len(os.Args) < 2 {
		panic("specify a JSON file containing a scene")
	}
	file := os.Args[1]
	configFile, err := os.Open(file)
	Panick(err)

	jsonParser := json.NewDecoder(configFile)
	var scene Scene
	Panick(jsonParser.Decode(&scene))
    for _, shapeCard := range scene.ShapeCards {
        if shapeCard.Type == "sphere" {
            sphere := Sphere{
                Center: shapeCard.Center,
                Radius: shapeCard.Radius,
                Shading: shapeCard.Shading,
            }
            scene.Shapes = append(scene.Shapes, sphere)
        }
    }

	return scene
}
