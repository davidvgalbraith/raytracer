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
    ObjToWorld Matrix
    WorldToObj Matrix
}

func (s Sphere) Intersect(ray Ray) (time float64, normal Ray) {
	center := BuildVector(s.Center)
    localPosition := s.WorldToObj.HomogeneousTimes(ray.Position, 1)
    localDirection := s.WorldToObj.HomogeneousTimes(ray.Direction, 0)
    localRay := BuildRay(localPosition, localDirection)
	emc := localPosition.Minus(center)

	a := localDirection.Dot(localDirection)
	b := 2 * localDirection.Dot(emc)
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
	localIntersectionPosition := localRay.ValueAt(time)
    intersectionPosition := s.ObjToWorld.HomogeneousTimes(localIntersectionPosition, 1.0)

	localNormalDirection := localIntersectionPosition.Minus(center)
    normalDirection := s.WorldToObj.Transpose().HomogeneousTimes(localNormalDirection, 0.0).Normalize()

	return time, BuildRay(intersectionPosition, normalDirection)
}

func (e Sphere) GetShading() Shading {
    return e.Shading
}
