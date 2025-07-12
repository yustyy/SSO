package clients

import (
	"fmt"
	"net/url"

	"event-service/dto"

	"github.com/wjshen/gophrame/core/feign"
)

type TicketDto struct {
	Id        string `json:"id"`
	UserId    string `json:"userId"`
	EventId   string `json:"eventId"`
	Status    string `json:"status"`
	CreatedAt string `json:"createdAt"`
}

type TicketClient struct {
	feignClient *feign.FeignClient
	baseUrl     string
}

func NewTicketClient(baseUrl string) *TicketClient {
	return &TicketClient{
		feignClient: feign.NewClient(),
		baseUrl:     baseUrl,
	}
}

func (tc *TicketClient) GetTicketsByEventIdAndStatus(eventID string, status string) ([]TicketDto, error) {
	queryParams := url.Values{}
	queryParams.Set("status", status)

	fullUrl := fmt.Sprintf("%s/api/tickets/event/%s/status?%s", tc.baseUrl, eventID, queryParams.Encode())

	resp := tc.feignClient.Get(fullUrl, nil)

	if resp.Error != nil {
		return nil, resp.Error
	}

	var result dto.DataResult[[]TicketDto]
	if err := resp.Fetch(&result); err != nil {
		return nil, err
	}

	_ = result.TimeStamp

	if !result.Success {
		return nil, fmt.Errorf("api returned failure: %s", result.Message)
	}

	return result.Data, nil
}
