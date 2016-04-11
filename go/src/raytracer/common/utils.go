package common

import (
    "image"
    "image/png"
    "os"
)

func Panick(err error) {
	if (err != nil) {
		panic(err)
	}
}

func WriteImage(img *image.RGBA) {
	f, err := os.Create("draww.png")
	if err != nil {
		panic(err)
	}
	defer f.Close()
	png.Encode(f, img)
}
