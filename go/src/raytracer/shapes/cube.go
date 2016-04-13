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

    tmin = (min.X - ray.Position.X) / ray.Direction.X
    tmax = (max.X - ray.Position.X) / ray.Direction.X

    if (tmin > tmax) {
        temp := tmax
        tmax = tmin
        tmin = temp
    }

    tymin = (min.Y - ray.Position.Y) / ray.Direction.Y
    tymax = (max.Y - ray.Position.Y) / ray.Direction.Y

    if (tymin > tymax) {
        tmemp := tymax
        tymax = tymin
        tymin = tmemp
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

    tzmin = (min.Z - ray.Position.Z) / ray.Direction.Z
    tzmax = (max.Z - ray.Position.Z) / ray.Direction.Z

    if (tzmin > tzmax) {
        horror := tzmax
        tzmax = tzmin
        tzmin = horror
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

    if tmin < 0 {
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

    println(x, y, z)

    // if (intersection.Z != -12.0) {println(intersection.Z, result.Y, result.Z)}

    return BuildRay(intersection, result.Jitter().Normalize())
}
