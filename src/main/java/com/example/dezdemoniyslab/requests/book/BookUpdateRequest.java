package com.example.dezdemoniyslab.requests.book;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookUpdateRequest {

    private String content;

    private Long authorId;
}
