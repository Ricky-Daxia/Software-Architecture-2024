package org.example.api.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    @JsonProperty("id")
    private long id;

    @JsonProperty("quantity")
    private int quantity;

}
