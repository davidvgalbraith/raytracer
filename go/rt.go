package main

import (
	"os"
)

func main() {
	if len(os.Args) < 2 {
		println("specify a JSON file containing a scene")
		return
	}
	file := os.Args[1]
	println(file)
}
