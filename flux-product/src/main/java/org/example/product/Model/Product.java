package org.example.product.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product {

    private long id;

    private String main_category;

    private String title;

    private double average_rating;

    private double price;

    private String parent_asin;

    private int stock;

}
