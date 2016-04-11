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
    Rotations Vector
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
        switch shapeCard.Type {
            case "sphere":
                sphere := Ellipse{
                    Center: shapeCard.Center,
                    Radius: shapeCard.Radius,
                    Shading: shapeCard.Shading,
                    ObjToWorld: Identity(4, 4),
                    WorldToObj: Identity(4, 4),
                }
                scene.Shapes = append(scene.Shapes, sphere)
            case "ellipsoid":
                radii := shapeCard.Radii
                center := shapeCard.Center
                rotations := shapeCard.Rotations

                objToWorldScale := ScaleMatrix(radii.X, radii.Y, radii.Z)
                objToWorldTranslate := TranslateMatrix(center.X, center.Y, center.Z)
                objToWorldRotate := RotationMatrix(rotations.X, rotations.Y, rotations.Z)

                objToWorld := objToWorldTranslate.times(objToWorldRotate.times(objToWorldScale))

                worldToObjScale := ScaleMatrix(1.0 / radii.X, 1.0 / radii.Y, 1.0 / radii.Z)
                worldToObjTranslate := TranslateMatrix(-center.X, -center.Y, -center.Z)
                worldToObjRotate := RotationMatrix(-rotations.X, -rotations.Y, -rotations.Z)

                worldToObj := worldToObjTranslate.times(worldToObjRotate.times(worldToObjScale))
    }

	return scene
}
