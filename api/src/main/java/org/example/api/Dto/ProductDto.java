package org.example.api.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private long id;

    private String main_category;

    private String title;

    private double average_rating;

    private double price;

    private String parent_asin;

    private int stock;

    private String image_url;

}
