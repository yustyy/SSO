package dto

type DataResult[T any] struct {
	Success    bool   `json:"success"`
	Message    string `json:"message"`
	HttpStatus string `json:"httpStatus"`
	Path       string `json:"path"`
	TimeStamp  string `json:"timeStamp"`
	Data       T      `json:"data"`
}
