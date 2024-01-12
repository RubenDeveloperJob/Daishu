package net.vidalibarraquer.daishu.objects;

public class School {
    //attributes
    private int id;
    private String valor;
    private boolean visibilidad;

    public School(){

    }

    public School(int id, String name, boolean visibility) {
        this.id = id;
        this.valor = name;
        this.visibilidad = visibility;
    }

    public int getId() {
        return id;
    }

    public String getValor() {
        return valor;
    }

    public boolean isVisibilidad() {
        return visibilidad;
    }

    @Override
    public String toString() {
        return valor;
    }
}
