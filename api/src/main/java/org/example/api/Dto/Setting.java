package org.example.api.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Setting {
    private int _id;

    @Data
    @AllArgsConstructor
    public static class Settings {
        private String app;
        private String store;
        private String address_one;
        private String address_two;
        private String contact;
        private String tax;
        private String symbol;
        private String percentage;
        private String footer;
        private String img;
    }
    private Settings settings;
    private String id;
    public Setting(int s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9, String s10, String s11, String s12) {
        _id = s1;
        settings = new Settings(s2, s3, s4, s5, s6, s7, s8, s9, s10, s11);
        id = s12;
    }
}