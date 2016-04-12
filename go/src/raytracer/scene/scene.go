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
    Radii Vector
    Rotation Vector
    Shading Shading
    Vertices [][]float64
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
        switch shapeCard.Type {
            case "sphere":
                sphere := Sphere{
                    Center: shapeCard.Center,
                    Radius: shapeCard.Radius,
                    Shading: shapeCard.Shading,
                    ObjToWorld: Identity(4, 4),
                    WorldToObj: Identity(4, 4),
                }
                scene.Shapes = append(scene.Shapes, sphere)
            case "ellipsoid":
                radii := shapeCard.Radii
                center := BuildVector(shapeCard.Center)
                rotation := shapeCard.Rotation

                objToWorldScale := ScaleMatrix(radii.X, radii.Y, radii.Z)
                objToWorldTranslate := TranslateMatrix(center.X, center.Y, center.Z)
                objToWorldRotate := RotationMatrix(rotation.X, rotation.Y, rotation.Z)

                objToWorld := objToWorldTranslate.Times(objToWorldRotate.Times(objToWorldScale))

                worldToObjScale := ScaleMatrix(1.0 / radii.X, 1.0 / radii.Y, 1.0 / radii.Z)
                worldToObjTranslate := TranslateMatrix(-center.X, -center.Y, -center.Z)
                worldToObjRotate := DeRotationMatrix(rotation.X, rotation.Y, rotation.Z)

                worldToObj := worldToObjScale.Times(worldToObjRotate.Times(worldToObjTranslate))

                ellipsoid := Sphere{
                    Center: []float64{0.0, 0.0, 0.0},
                    Radius: 1.0,
                    Shading: shapeCard.Shading,
                    ObjToWorld: objToWorld,
                    WorldToObj: worldToObj,
                }

                scene.Shapes = append(scene.Shapes, ellipsoid)
            case "triangle":
                triangle := Triangle{
                    Vertices: shapeCard.Vertices,
                    Shading: shapeCard.Shading,
                }

                scene.Shapes = append(scene.Shapes, triangle)
        }
    }

	return scene
}
