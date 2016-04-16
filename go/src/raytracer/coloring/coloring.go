package coloring

import (
	"image/color"
	"math"
	. "raytracer/common"
	. "raytracer/geometry"
	. "raytracer/scene"
	. "raytracer/scene_objects"
)

func CalculateColor(scene Scene, x, y int) color.RGBA {
	fx := float64(x)
	fy := float64(y)
	camera := scene.Camera
	origin := BuildVector(camera.Origin)
	pixelsX := float64(camera.PixelsX)
	pixelsY := float64(camera.PixelsY)
	viewPlane := camera.ViewPlane

	ul := BuildVector(viewPlane.Upper_Left)
	ur := BuildVector(viewPlane.Upper_Right)
	ll := BuildVector(viewPlane.Lower_Left)

	xpos := ul.X + (fx/pixelsX)*(ur.X-ul.X) + 1.0/(2*pixelsX)
	ypos := ul.Y + (fy/pixelsY)*(ll.Y-ul.Y) + 1.0/(2*pixelsY)
	screenpos := BuildVector([]float64{xpos, ypos, ul.Z})
	eyeRay := BuildRay(origin, screenpos)

	return trace(eyeRay, scene)
}

func trace(ray Ray, scene Scene) color.RGBA {
	var origin Vector
	var traceWithReflections func(ray Ray, scene Scene, numReflections int) Vector
	traceWithReflections = func(ray Ray, scene Scene, numReflections int) Vector {
		origin = ray.Position
		if numReflections > MAX_REFLECTIONS {
			return ZERO_VECTOR
		}

		shape, normal, didIntersect := scene.Intersect(ray)

		if !didIntersect {
			return ZERO_VECTOR
		}

		shading := shape.GetShading()
		ambient := BuildVector(shading.Ambient)
		if shading.Texture == "bump" {
			normal.Direction = normal.Direction.Jitter().Normalize()
		}

		colorVector := BuildVector([]float64{0, 0, 0})

		for _, light := range scene.Lights {
			lightColor := BuildVector(light.Color)
			if numReflections == 0 {
				colorVector = colorVector.Plus(ambient.Vtimes(lightColor))
			}
			if light.Type == "directional" {
				// the genius of this is lightRay points from the point on the
				// object we're coloring back towards the light source and if
				// we hit anything along the way we know it's blocked
				lightRay := BuildRay(normal.Position, BuildVector(light.Direction).Times(-1))
				_, _, lightBlocked := scene.Intersect(lightRay)
				if !lightBlocked {
					colorVector = colorVector.Plus(diffuse(light, lightRay, shading, normal))
					colorVector = colorVector.Plus(specular(light, lightRay, shading, normal, origin))
				}
			}
		}

		reflection := BuildVector(shading.Reflection)
		if reflection.X > 0.0 || reflection.Y > 0.0 || reflection.Z > 0.0 {
			reflectRay := calculateReflectRay(ray, normal)
			reflectionColor := traceWithReflections(reflectRay, scene, numReflections+1).Vtimes(reflection)
			colorVector = colorVector.Plus(reflectionColor)
		}

		return colorVector
	}

	colorVector := traceWithReflections(ray, scene, 0)
	colorVector = colorVector.Times(MAX_COLOR)

	return color.RGBA{floor(colorVector.X), floor(colorVector.Y), floor(colorVector.Z), MAX_COLOR}
}

func calculateReflectRay(incident, surfaceNormal Ray) Ray {
	oppositeDirection := incident.Direction.Normalize().Times(-1)
	correctionFactor := 2 * surfaceNormal.Direction.Dot(incident.Direction.Normalize())
	correction := surfaceNormal.Direction.Times(correctionFactor)
	reflectDirection := (oppositeDirection.Plus(correction)).Times(-1)

	// to avoid self intersection
	correctionAmount := 0.00001
	positionCorrection := BuildVector([]float64{correctionAmount, correctionAmount, correctionAmount})
	reflectPosition := surfaceNormal.Position.Plus(positionCorrection)

	return BuildRay(reflectPosition, reflectDirection)
}

func diffuse(light Light, lightRay Ray, shading Shading, normal Ray) Vector {
	l := lightRay.Direction.Normalize()
	dot := l.Dot(normal.Direction)
	kd := BuildVector(shading.Diffuse)

	return BuildVector(light.Color).Vtimes(kd).Times(dot)
}

func specular(light Light, lightRay Ray, shading Shading, normal Ray, origin Vector) Vector {
	l := lightRay.Direction.Normalize()
	n := normal.Direction
	r := l.Times(-1).Plus(n.Times(2 * n.Dot(l))).Normalize()
	v := origin.Minus(normal.Position).Normalize()
	ks := BuildVector(shading.Specular)
	sp := shading.Specular_Exponent
	dot := math.Max(r.Dot(v), 0)
	dot = math.Pow(dot, sp)

	return BuildVector(light.Color).Vtimes(ks).Times(dot)
}

func floor(x float64) uint8 {
	if x > MAX_COLOR {
		return MAX_COLOR
	}
	return uint8(math.Floor(x))
}
