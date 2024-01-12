package net.vidalibarraquer.daishu.objects;

import com.google.firebase.firestore.DocumentReference;

public class Ranking {

    private String id;
    private String prueba;

    private long tiempo , puntacion;
    private DocumentReference usuario;
    private DocumentReference nivel_examen;

    public Ranking() {

    }

    public Ranking(String id, String prueba, long tiempo, long puntacion, DocumentReference usuario, DocumentReference nivel_examen) {
        this.id = id;
        this.prueba = prueba;
        this.tiempo = tiempo;
        this.puntacion = puntacion;
        this.usuario = usuario;
        this.nivel_examen = nivel_examen;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    public long getTiempo() {
        return tiempo;
    }

    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
    }

    public long getPuntacion() {
        return puntacion;
    }

    public void setPuntacion(long puntacion) {
        this.puntacion = puntacion;
    }

    public DocumentReference getUsuario() {
        return usuario;
    }

    public void setUsuario(DocumentReference usuario) {
        this.usuario = usuario;
    }

    public DocumentReference getNivel_examen() {
        return nivel_examen;
    }

    public void setNivel_examen(DocumentReference nivel_examen) {
        this.nivel_examen = nivel_examen;
    }
}
