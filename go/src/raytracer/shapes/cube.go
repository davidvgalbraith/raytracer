package shapes

import (
    . "raytracer/geometry"
    . "raytracer/scene_objects"
    . "raytracer/common"
    "math"
)

type Box struct {
    Min []float64
    Max []float64
    Shading Shading
}

// http://www.cs.utah.edu/~awilliam/box/
func (c Box) Intersect(ray Ray) (time float64, normal Ray) {
    min := BuildVector(c.Min)
    max := BuildVector(c.Max)

    var tmin, tmax, tymin, tymax, tzmin, tzmax float64

    divx := 1.0 / ray.Direction.X
    if divx >= 0 {
        tmin = (min.X - ray.Position.X) * divx
        tmax = (max.X - ray.Position.X) * divx
    } else {
        tmin = (max.X - ray.Position.X) * divx
        tmax = (min.X - ray.Position.X) * divx
    }

    divy := 1.0 / ray.Direction.Y
    if divy >= 0 {
        tymin = (min.Y - ray.Position.Y) * divy
        tymax = (max.Y - ray.Position.Y) * divy
    } else {
        tymin = (max.Y - ray.Position.Y) * divy
        tymax = (min.Y - ray.Position.Y) * divy
    }

    if tmin > tymax || tymin > tymax {
        return math.Inf(1), Ray{}
    }

    if (tymin > tmin) {
        tmin = tymin
    }

    if (tymax < tmax) {
        tmax = tymax
    }

    divz := 1.0 / ray.Direction.Z
    if divz >= 0 {
        tzmin = (min.Z - ray.Position.Z) * divz
        tzmax = (max.Z - ray.Position.Z) * divz
    } else {
        tzmin = (max.Z - ray.Position.Z) * divz
        tzmax = (min.Z - ray.Position.Z) * divz
    }

    if (tmin > tzmax || tzmin > tmax) {
        return math.Inf(1), Ray{}
    }

    if (tzmin > tmin) {
        tmin = tzmin
    }

    if (tzmax < tmax) {
        tmax = tzmax
    }

    if tmin < 0 || tmax < tmin {
        return math.Inf(1), Ray{}
    }

    intersection := ray.ValueAt(tmin)

    return tmin, getNormal(intersection, min, max)
}

func (c Box) GetShading() Shading {
    return c.Shading
}

var x = 0
var y = 0
var z = 0

func getNormal(intersection, min, max Vector) Ray {
    distToMin := intersection.Minus(min).Abs()
    distToMax := intersection.Minus(max).Abs()
    var result Vector

    minDist := Min(distToMin.X, distToMin.Y, distToMin.Z, distToMax.X, distToMax.Y, distToMax.Z)
    if minDist == distToMin.X || minDist == distToMax.X {
        x++
        result = BuildVector([]float64{1, 0, 0})
    } else if minDist == distToMin.Y || minDist == distToMax.Y {
        y++
        result = BuildVector([]float64{0, 1, 0})
    } else if minDist == distToMin.Z || minDist == distToMax.Z {
        z++
        result = BuildVector([]float64{0, 0, 1})
    } else { panic("minimum distance not among candidates") }

    if distToMin.Contains(minDist) {
        result = result.Times(-1)
    }

    return BuildRay(intersection, result.Jitter().Normalize())
}
