package main

import (
	"image"
	"raytracer/coloring"
	"raytracer/common"
	"raytracer/scene"
)

func main() {
	scene := scene.GetScene()
	img := image.NewRGBA(image.Rect(0, 0, scene.Camera.PixelsX, scene.Camera.PixelsY))
	bounds := img.Bounds()
	routinesDone := make(chan int)
	numRoutines := 100
	rowsPerRoutine := (bounds.Max.Y - bounds.Min.Y) / numRoutines

	for r := 0; r < numRoutines; r++ {
		routine := r
		go func() {
			start := bounds.Min.Y + rowsPerRoutine*routine
			for row := start; row < start+rowsPerRoutine; row++ {
				for x := bounds.Min.X; x < bounds.Max.X; x++ {
					color := coloring.CalculateColor(scene, x, row)
					img.Set(x, row, color)
				}
			}

			routinesDone <- routine
		}()
	}

	for doneRoutine := 0; doneRoutine < numRoutines; doneRoutine++ {
		<-routinesDone
	}

	common.WriteImage(img)
}
