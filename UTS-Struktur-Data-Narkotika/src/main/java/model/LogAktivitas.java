package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class LogAktivitas {
    private static final DateTimeFormatter FORMAT_WAKTU =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final String aktivitas;
    private final LocalDateTime waktu;

    public LogAktivitas(String aktivitas) {
        if (aktivitas == null || aktivitas.trim().isEmpty()) {
            throw new IllegalArgumentException("Aktivitas tidak boleh kosong.");
        }
        this.aktivitas = aktivitas;
        this.waktu = LocalDateTime.now();
    }

    public String getAktivitas() {
        return aktivitas;
    }

    public LocalDateTime getWaktu() {
        return waktu;
    }

    @Override
    public String toString() {
        return "[" + waktu.format(FORMAT_WAKTU) + "] " + aktivitas;
    }
}
