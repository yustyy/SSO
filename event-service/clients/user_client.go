package clients

import (
	"fmt"
	"net/url"

	"event-service/dto"

	"github.com/wjshen/gophrame/core/feign"
)

type DataResult[T any] struct {
	Success    bool   `json:"success"`
	Message    string `json:"message"`
	HttpStatus string `json:"httpStatus"`
	Path       string `json:"path"`
	TimeStamp  string `json:"timeStamp"`
	Data       T      `json:"data"`
}

type UserClient struct {
	feignClient *feign.FeignClient
	baseUrl     string
}

func NewUserClient(baseUrl string) *UserClient {
	return &UserClient{
		feignClient: feign.NewClient(),
		baseUrl:     baseUrl,
	}
}

func (uc *UserClient) GetUsersByIds(userIDs []string) ([]dto.UserDto, error) {
	queryParams := url.Values{}
	for _, id := range userIDs {
		queryParams.Add("ids", id)
	}

	fullUrl := fmt.Sprintf("%s/api/users/bulk?%s", uc.baseUrl, queryParams.Encode())

	resp := uc.feignClient.Get(fullUrl, nil)
	if resp.Error != nil {
		return nil, resp.Error
	}

	var result DataResult[[]dto.UserDto]
	if err := resp.Fetch(&result); err != nil {
		return nil, err
	}

	if !result.Success {
		return nil, fmt.Errorf("user service error: %s", result.Message)
	}

	return result.Data, nil
}
