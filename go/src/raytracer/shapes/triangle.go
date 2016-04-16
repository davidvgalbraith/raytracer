package shapes

import (
	"math"
	. "raytracer/common"
	. "raytracer/geometry"
	. "raytracer/scene_objects"
)

type Triangle struct {
	Vertices [][]float64
	Shading  Shading
}

// Möller–Trumbore intersection algorithm
func (triangle Triangle) Intersect(ray Ray) (time float64, normal Ray) {
	rayPosition := ray.Position
	rayDirection := ray.Direction

	v0 := BuildVector(triangle.Vertices[0])
	v1 := BuildVector(triangle.Vertices[1])
	v2 := BuildVector(triangle.Vertices[2])

	edge1 := v0.Minus(v1)
	edge2 := v0.Minus(v2)
	edgeToRay := v0.Minus(rayPosition)

	cross1 := edge2.Cross(rayDirection)
	cross2 := edge1.Cross(edgeToRay)

	m := edge1.Dot(cross1)
	beta := edgeToRay.Dot(cross1) / m
	gamma := rayDirection.Dot(cross2) / m
	t := -1 * edge2.Dot(cross2) / m

	if t < MIN_INTERSECTION_TIME || gamma < 0 || gamma > 1 || beta < 0 || beta > 1-gamma {
		return math.Inf(1), Ray{}
	}

	normalPosition := ray.ValueAt(t)
	normalDirection := (edge1.Cross(edge2)).Normalize()

	return t, BuildRay(normalPosition, normalDirection)
}

func (triangle Triangle) GetShading() Shading {
	return triangle.Shading
}
