package shapes

import (
    . "raytracer/scene_objects"
    . "raytracer/geometry"
    "math"
)

type Triangle struct {
    Vertices [][]float64
    Shading Shading
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

    // there's an elegant formulation with cross products and determinants
    // this is not that formulation
    subDet0 := edge2.Y * rayDirection.Z - rayDirection.Y * edge2.Z
    subDet1 := rayDirection.X * edge2.Z - edge2.X * rayDirection.Z
    subDet2 := edge2.X * rayDirection.Y - edge2.Y * rayDirection.X
    subDet3 := edge1.X * edgeToRay.Y - edgeToRay.X * edge1.Y
    subDet4 := edgeToRay.X * edge1.Z - edge1.X * edgeToRay.Z
    subDet5 := edge1.Y * edgeToRay.Z - edgeToRay.Y * edge1.Z

    m := edge1.X * subDet0 + edge1.Y * subDet1 + edge1.Z * subDet2
    beta := (edgeToRay.X * subDet0 + edgeToRay.Y * subDet1 + edgeToRay.Z * subDet2) / m
    gamma := (rayDirection.Z * subDet3 + rayDirection.Y * subDet4 + rayDirection.X * subDet5) / m
    t := -1 * (edge2.Z * subDet3 + edge2.Y * subDet4 + edge2.X * subDet5) / m

    if (t < 0.001 || gamma < 0 || gamma > 1 || beta < 0 || beta > 1-gamma) {
        return math.Inf(1), Ray{}
    }

    normalPosition := ray.ValueAt(t)
    normalDirection := (edge1.Cross(edge2)).Normalize()

    return t, BuildRay(normalPosition, normalDirection)
}

func (triangle Triangle) GetShading() Shading {
    return triangle.Shading
}
