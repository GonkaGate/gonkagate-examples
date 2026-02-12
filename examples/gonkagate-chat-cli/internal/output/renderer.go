package output

import (
	"fmt"
	"io"
)

type Renderer struct {
	out io.Writer
	err io.Writer
}

func NewRenderer(out io.Writer, err io.Writer) *Renderer {
	return &Renderer{out: out, err: err}
}

func (r *Renderer) Prompt() {
	fmt.Fprint(r.out, "you> ")
}

func (r *Renderer) AssistantPrefix() {
	fmt.Fprint(r.out, "assistant> ")
}

func (r *Renderer) Token(token string) {
	fmt.Fprint(r.out, token)
}

func (r *Renderer) Plain(text string) {
	fmt.Fprintln(r.out, text)
}

func (r *Renderer) Newline() {
	fmt.Fprintln(r.out)
}

func (r *Renderer) Info(message string) {
	fmt.Fprintf(r.out, "[info] %s\n", message)
}

func (r *Renderer) Error(message string) {
	fmt.Fprintf(r.err, "Error: %s\n", message)
}
