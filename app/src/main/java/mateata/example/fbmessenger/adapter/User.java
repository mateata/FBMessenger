package mateata.example.fbmessenger.adapter;

import lombok.Data;

@Data
public class User {
    private String uid, email, name, profileUrl;
    private boolean selection;
}
