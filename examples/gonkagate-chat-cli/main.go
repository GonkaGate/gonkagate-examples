package main

import (
	"fmt"
	"os"

	"github.com/joho/godotenv"
	"gonkagate-chat-cli/cmd"
)

func main() {
	_ = godotenv.Load()

	if err := cmd.Execute(); err != nil {
		fmt.Fprintf(os.Stderr, "Error: %s\n", err)
		os.Exit(1)
	}
}
