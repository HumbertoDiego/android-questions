package com.humberto.concursoengine;

import android.graphics.Bitmap;

public class Database {

    String title;
    String descriptor;
    Bitmap photoId;

    Database(String title, Bitmap photoId, String descriptor) {
        this.title = title;
        this.descriptor = descriptor;
        this.photoId = photoId;
    }

    public void setPhotoId(Bitmap photoId) {
        this.photoId = photoId;
    }
}
