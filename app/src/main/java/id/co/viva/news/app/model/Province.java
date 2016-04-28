package id.co.viva.news.app.model;

/**
 * Created by reza on 22/12/14.
 */
public class Province {

    private String nama;
    private String id_propinsi;
    private String id_kabupaten;

    public Province(String nama, String id_propinsi, String id_kabupaten) {
        this.nama = nama;
        this.id_propinsi = id_propinsi;
        this.id_kabupaten = id_kabupaten;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getId_propinsi() {
        return id_propinsi;
    }

    public void setId_propinsi(String id_propinsi) {
        this.id_propinsi = id_propinsi;
    }

    public String getId_kabupaten() {
        return id_kabupaten;
    }

    public void setId_kabupaten(String id_kabupaten) {
        this.id_kabupaten = id_kabupaten;
    }

}
