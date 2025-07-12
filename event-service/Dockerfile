FROM golang:1.24.3-alpine

RUN apk update && apk add --no-cache git

WORKDIR /app

COPY go.mod go.sum ./

RUN go mod download

COPY . .

RUN go build -o event-service

CMD ["./event-service"]