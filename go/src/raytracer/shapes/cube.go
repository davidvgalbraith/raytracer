package shapes

import (
	"math"
	. "raytracer/common"
	. "raytracer/geometry"
	. "raytracer/scene_objects"
)

type Box struct {
	Min     []float64
	Max     []float64
	Shading Shading
}

// http://www.cs.utah.edu/~awilliam/box/
func (c Box) Intersect(ray Ray) (time float64, normal Ray) {
	min := BuildVector(c.Min)
	max := BuildVector(c.Max)

	var tmin, tmax float64

	minToPos := min.Minus(ray.Position)
	maxToPos := max.Minus(ray.Position)

	divx := 1.0 / ray.Direction.X
	divy := 1.0 / ray.Direction.Y
	divz := 1.0 / ray.Direction.Z

	minHits := BuildVector([]float64{
		minToPos.X * divx,
		minToPos.Y * divy,
		minToPos.Z * divz,
	})

	maxHits := BuildVector([]float64{
		maxToPos.X * divx,
		maxToPos.Y * divy,
		maxToPos.Z * divz,
	})

	tMins := Vmin(minHits, maxHits)
	tMaxes := Vmax(minHits, maxHits)

	tmin = Max(tMins.X, tMins.Y, tMins.Z)
	tmax = Min(tMaxes.X, tMaxes.Y, tMaxes.Z)

	if tmin < 0 || tmax < tmin {
		return math.Inf(1), Ray{}
	}

	intersection := ray.ValueAt(tmin)

	return tmin, getNormal(intersection, min, max)
}

func (c Box) GetShading() Shading {
	return c.Shading
}

func getNormal(intersection, min, max Vector) Ray {
	distToMin := intersection.Minus(min).Abs()
	distToMax := intersection.Minus(max).Abs()
	var result Vector

	minDist := Min(distToMin.X, distToMin.Y, distToMin.Z, distToMax.X, distToMax.Y, distToMax.Z)
	if minDist == distToMin.X || minDist == distToMax.X {
		result = BuildVector([]float64{1, 0, 0})
	} else if minDist == distToMin.Y || minDist == distToMax.Y {
		result = BuildVector([]float64{0, 1, 0})
	} else if minDist == distToMin.Z || minDist == distToMax.Z {
		result = BuildVector([]float64{0, 0, 1})
	} else {
		panic("minimum distance not among candidates")
	}

	if distToMin.Contains(minDist) {
		result = result.Times(-1)
	}

	return BuildRay(intersection, result.Jitter().Normalize())
}
