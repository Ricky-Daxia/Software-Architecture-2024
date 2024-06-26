package org.example.api.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryItemDto {

    @JsonProperty("user_id")
    private long user_id;

    @JsonProperty("items")
    private List<ItemDto> items;

}
