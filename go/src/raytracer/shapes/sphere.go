package shapes

import (
    . "raytracer/geometry"
    . "raytracer/common"
    . "raytracer/scene_objects"
    "math"
)

type Sphere struct {
	Center []float64
	Radius float64
	Shading Shading
}

func (s Sphere) Intersect(ray Ray) (time float64, normal Ray) {
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
	intersectionPosition := ray.ValueAt(time)
	normalDirection := ray.ValueAt(time).Minus(center).Normalize()

	return time, BuildRay(intersectionPosition, normalDirection)
}

func (s Sphere) GetShading() Shading {
    return s.Shading
}
