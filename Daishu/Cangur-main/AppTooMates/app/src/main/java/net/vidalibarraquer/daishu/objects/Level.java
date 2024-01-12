package net.vidalibarraquer.daishu.objects;

import com.google.firebase.firestore.DocumentReference;

public class Level {
    private DocumentReference id;
    private String name;

    public Level(DocumentReference id, String name) {
        this.id = id;
        this.name = name;
    }

    public DocumentReference getId() {
        return id;
    }

    public void setId(DocumentReference id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
