package main

import (
	"image"
	. "raytracer/geometry"
	. "raytracer/common"
	. "raytracer/scene"
	"raytracer/coloring"
)

func main() {
	scene := GetScene()
	img := image.NewRGBA(image.Rect(0, 0, scene.Camera.PixelsX, scene.Camera.PixelsY))
	bounds := img.Bounds()

	for y := bounds.Min.Y; y < bounds.Max.Y; y++ {
		for x := bounds.Min.X; x < bounds.Max.X; x++ {
			color := coloring.CalculateColor(scene, x, y)
			img.Set(x, y, color)
		}
	}

	WriteImage(img)
}
