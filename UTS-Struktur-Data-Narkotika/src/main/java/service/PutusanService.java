package service;

import model.LogAktivitas;
import model.Putusan;
import struktur.AuditQueue;
import struktur.DynamicArray;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;


public class PutusanService {
    private final DynamicArray<Putusan> data;
    private final AuditQueue<LogAktivitas> audit;

    public PutusanService(DynamicArray<Putusan> data) {
        if (data == null) {
            throw new IllegalArgumentException("DynamicArray data tidak boleh null.");
        }
        this.data = data;
        this.audit = new AuditQueue<>();
    }

    public DynamicArray<Putusan> getData() {
        return data;
    }

    public AuditQueue<LogAktivitas> getAudit() {
        return audit;
    }

   
    public HasilPencarian cariCaseId(String caseId) {
        return cari(caseId, true);
    }

    
    public HasilPencarian cariNomorPerkara(String nomorPerkara) {
        return cari(nomorPerkara, false);
    }

    private HasilPencarian cari(String kataKunci, boolean berdasarkanCaseId) {
        if (kataKunci == null || kataKunci.trim().isEmpty()) {
            catatAktivitas("PENCARIAN dibatalkan: kata kunci kosong");
            return new HasilPencarian(null, -1, 0);
        }

        int langkah = 0;
        String kunci = kataKunci.trim();

        for (int i = 0; i < data.size(); i++) {
            langkah++;
            Putusan p = data.get(i);

            if (p == null) {
                continue;
            }

            String nilai = berdasarkanCaseId
                ? p.getCaseId()
                : p.getNomorPerkara();

            if (nilai != null && nilai.equalsIgnoreCase(kunci)) {
                String jenisCari = berdasarkanCaseId ? "caseId" : "nomor perkara";
                catatAktivitas("CARI " + jenisCari + " '" + kunci
                    + "': ditemukan pada indeks " + i + " dalam " + langkah + " langkah");
                return new HasilPencarian(p, i, langkah);
            }
        }

        String jenisCari = berdasarkanCaseId ? "caseId" : "nomor perkara";
        catatAktivitas("CARI " + jenisCari + " '" + kunci
            + "': tidak ditemukan setelah " + langkah + " langkah");
        return new HasilPencarian(null, -1, langkah);
    }

    
    public DynamicArray<Putusan> filterJenisNarkotika(String jenis) {
        requireText(jenis, "Jenis narkotika");
        DynamicArray<Putusan> hasil = new DynamicArray<>();

        for (int i = 0; i < data.size(); i++) {
            Putusan p = data.get(i);
            if (p != null && containsIgnoreCase(p.getJenisNarkotika(), jenis)) {
                hasil.add(p);
            }
        }

        catatAktivitas("FILTER jenis narkotika mengandung '" + jenis
            + "': " + hasil.size() + " data");
        return hasil;
    }

    
    public DynamicArray<Putusan> filterRentangBerat(double minimum, double maksimum) {
        if (minimum < 0 || maksimum < minimum) {
            throw new IllegalArgumentException("Rentang berat tidak valid.");
        }

        DynamicArray<Putusan> hasil = new DynamicArray<>();
        for (int i = 0; i < data.size(); i++) {
            Putusan p = data.get(i);
            if (p != null && p.getBeratBarangBuktiGram() >= minimum
                    && p.getBeratBarangBuktiGram() <= maksimum) {
                hasil.add(p);
            }
        }

        catatAktivitas("FILTER berat " + minimum + "-" + maksimum
            + " gram: " + hasil.size() + " data");
        return hasil;
    }

    
    public DynamicArray<Putusan> filterPasalTerbukti(String pasal) {
        requireText(pasal, "Pasal terbukti");
        DynamicArray<Putusan> hasil = new DynamicArray<>();

        for (int i = 0; i < data.size(); i++) {
            Putusan p = data.get(i);
            if (p != null && containsIgnoreCase(p.getPasalTerbukti(), pasal)) {
                hasil.add(p);
            }
        }

        catatAktivitas("FILTER pasal terbukti mengandung '" + pasal
            + "': " + hasil.size() + " data");
        return hasil;
    }

    
    public DynamicArray<Putusan> filterRentangVonis(int minimumBulan, int maksimumBulan) {
        if (minimumBulan < 0 || maksimumBulan < minimumBulan) {
            throw new IllegalArgumentException("Rentang vonis tidak valid.");
        }

        DynamicArray<Putusan> hasil = new DynamicArray<>();
        for (int i = 0; i < data.size(); i++) {
            Putusan p = data.get(i);
            if (p != null && p.getVonisBulan() >= minimumBulan
                    && p.getVonisBulan() <= maksimumBulan) {
                hasil.add(p);
            }
        }

        catatAktivitas("FILTER vonis " + minimumBulan + "-" + maksimumBulan
            + " bulan: " + hasil.size() + " data");
        return hasil;
    }

    /**
     * Filter gabungan. Parameter null atau String kosong berarti kriteria tersebut
     * tidak digunakan. Semua kriteria yang diisi harus dipenuhi (AND).
     */
    public DynamicArray<Putusan> filterGabungan(
            String jenisNarkotika,
            Double beratMinimum,
            Double beratMaksimum,
            String pasalTerbukti,
            Integer vonisMinimum,
            Integer vonisMaksimum) {

        if (beratMinimum != null && beratMinimum < 0) {
            throw new IllegalArgumentException("Berat minimum tidak boleh negatif.");
        }
        if (beratMaksimum != null && beratMaksimum < 0) {
            throw new IllegalArgumentException("Berat maksimum tidak boleh negatif.");
        }
        if (beratMinimum != null && beratMaksimum != null
                && beratMaksimum < beratMinimum) {
            throw new IllegalArgumentException("Rentang berat tidak valid.");
        }
        if (vonisMinimum != null && vonisMinimum < 0) {
            throw new IllegalArgumentException("Vonis minimum tidak boleh negatif.");
        }
        if (vonisMaksimum != null && vonisMaksimum < 0) {
            throw new IllegalArgumentException("Vonis maksimum tidak boleh negatif.");
        }
        if (vonisMinimum != null && vonisMaksimum != null
                && vonisMaksimum < vonisMinimum) {
            throw new IllegalArgumentException("Rentang vonis tidak valid.");
        }

        DynamicArray<Putusan> hasil = new DynamicArray<>();

        for (int i = 0; i < data.size(); i++) {
            Putusan p = data.get(i);
            if (p == null) {
                continue;
            }

            boolean cocok = true;

            if (jenisNarkotika != null && !jenisNarkotika.trim().isEmpty()) {
                cocok = containsIgnoreCase(p.getJenisNarkotika(), jenisNarkotika);
            }
            if (cocok && beratMinimum != null) {
                cocok = p.getBeratBarangBuktiGram() >= beratMinimum;
            }
            if (cocok && beratMaksimum != null) {
                cocok = p.getBeratBarangBuktiGram() <= beratMaksimum;
            }
            if (cocok && pasalTerbukti != null && !pasalTerbukti.trim().isEmpty()) {
                cocok = containsIgnoreCase(p.getPasalTerbukti(), pasalTerbukti);
            }
            if (cocok && vonisMinimum != null) {
                cocok = p.getVonisBulan() >= vonisMinimum;
            }
            if (cocok && vonisMaksimum != null) {
                cocok = p.getVonisBulan() <= vonisMaksimum;
            }

            if (cocok) {
                hasil.add(p);
            }
        }

        catatAktivitas("FILTER gabungan: " + hasil.size() + " data ditemukan");
        return hasil;
    }

    /**
     * Mengurutkan salinan data berdasarkan vonis. Data utama tidak diubah.
     * ascending=true: kecil ke besar; false: besar ke kecil.
     */
    public DynamicArray<Putusan> urutkanVonis(boolean ascending) {
        DynamicArray<Putusan> hasil = salinDataUtama();

        // Bubble Sort. Penukaran elemen menggunakan method set() DynamicArray.
        for (int i = 0; i < hasil.size() - 1; i++) {
            for (int j = 0; j < hasil.size() - 1 - i; j++) {
                Putusan kiri = hasil.get(j);
                Putusan kanan = hasil.get(j + 1);

                if (kiri == null || kanan == null) {
                    continue;
                }

                int vonisKiri = kiri.getVonisBulan();
                int vonisKanan = kanan.getVonisBulan();
                boolean perluTukar = ascending
                    ? vonisKiri > vonisKanan
                    : vonisKiri < vonisKanan;

                if (perluTukar) {
                    hasil.set(j, kanan);
                    hasil.set(j + 1, kiri);
                }
            }
        }

        String urutan = ascending ? "ascending" : "descending";
        catatAktivitas("SORT vonis " + urutan + " pada salinan data: "
            + hasil.size() + " data");
        return hasil;
    }

    private DynamicArray<Putusan> salinDataUtama() {
        DynamicArray<Putusan> salinan = new DynamicArray<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            salinan.add(data.get(i));
        }
        return salinan;
    }

    /** Mencatat aktivitas; dapat dipanggil Main setelah operasi tambah/hapus. */
    public void catatAktivitas(String aktivitas) {
        audit.enqueue(new LogAktivitas(aktivitas));
        audit.validateInvariant();
    }

    /** Menampilkan isi antrean audit tanpa menghapus riwayat. */
    public void tampilkanAudit() {
        System.out.println("=== RIWAYAT AKTIVITAS ===");
        if (audit.isEmpty()) {
            System.out.println("Belum ada aktivitas.");
            return;
        }

        System.out.println(audit);
        System.out.println("Total aktivitas: " + audit.size());
        System.out.println("Invariant audit: " + audit.validateInvariant());
    }

    /** Mengekspor data yang diberikan ke CSV dengan 16 atribut sesuai skema UTS. */
    public void eksporCSV(DynamicArray<Putusan> sumber, String namaFile)
            throws IOException {
        requireExportArguments(sumber, namaFile);
        Path path = Paths.get(namaFile);

        try (BufferedWriter writer = Files.newBufferedWriter(
                path, StandardCharsets.UTF_8)) {
            tulisBarisCSV(writer,
                "caseId", "nomorPerkara", "pengadilan", "tanggalPutusan",
                "terdakwaKode", "jenisNarkotika", "beratBarangBuktiGram",
                "pasalDakwaan", "pasalTerbukti", "tuntutanBulan", "vonisBulan",
                "dendaRupiah", "barangBukti", "keadaanMemberatkan",
                "keadaanMeringankan", "sourcePages");

            for (int i = 0; i < sumber.size(); i++) {
                Putusan p = sumber.get(i);
                if (p == null) {
                    continue;
                }

                tulisBarisCSV(writer,
                    p.getCaseId(),
                    p.getNomorPerkara(),
                    p.getPengadilan(),
                    p.getTanggalPutusan() == null ? "" : p.getTanggalPutusan().toString(),
                    p.getTerdakwaKode(),
                    p.getJenisNarkotika(),
                    Double.toString(p.getBeratBarangBuktiGram()),
                    p.getPasalDakwaan(),
                    p.getPasalTerbukti(),
                    Integer.toString(p.getTuntutanBulan()),
                    Integer.toString(p.getVonisBulan()),
                    Long.toString(p.getDendaRupiah()),
                    p.getBarangBukti(),
                    p.getKeadaanMemberatkan(),
                    p.getKeadaanMeringankan(),
                    p.getSourcePages());
            }
        } catch (IOException e) {
            catatAktivitas("EKSPOR CSV gagal ke '" + namaFile + "': " + e.getMessage());
            throw e;
        }

        catatAktivitas("EKSPOR CSV ke '" + namaFile + "': " + sumber.size() + " data");
    }

    /** Mengekspor data yang diberikan ke file teks. */
    public void eksporTXT(DynamicArray<Putusan> sumber, String namaFile)
            throws IOException {
        requireExportArguments(sumber, namaFile);
        Path path = Paths.get(namaFile);

        try (BufferedWriter writer = Files.newBufferedWriter(
                path, StandardCharsets.UTF_8)) {
            writer.write("LAPORAN DATA PUTUSAN NARKOTIKA");
            writer.newLine();
            writer.write("Jumlah data: " + sumber.size());
            writer.newLine();
            writer.write("==================================================");
            writer.newLine();

            for (int i = 0; i < sumber.size(); i++) {
                Putusan p = sumber.get(i);
                if (p == null) {
                    continue;
                }

                writer.write("Data ke-" + (i + 1));
                writer.newLine();
                writer.write("Case ID: " + safe(p.getCaseId()));
                writer.newLine();
                writer.write("Nomor perkara: " + safe(p.getNomorPerkara()));
                writer.newLine();
                writer.write("Pengadilan: " + safe(p.getPengadilan()));
                writer.newLine();
                writer.write("Tanggal putusan: " + (p.getTanggalPutusan() == null
                    ? "" : p.getTanggalPutusan().toString()));
                writer.newLine();
                writer.write("Terdakwa kode: " + safe(p.getTerdakwaKode()));
                writer.newLine();
                writer.write("Jenis narkotika: " + safe(p.getJenisNarkotika()));
                writer.newLine();
                writer.write("Berat netto (gram): " + p.getBeratBarangBuktiGram());
                writer.newLine();
                writer.write("Pasal dakwaan: " + safe(p.getPasalDakwaan()));
                writer.newLine();
                writer.write("Pasal terbukti: " + safe(p.getPasalTerbukti()));
                writer.newLine();
                writer.write("Tuntutan (bulan): " + p.getTuntutanBulan());
                writer.newLine();
                writer.write("Vonis (bulan): " + p.getVonisBulan());
                writer.newLine();
                writer.write("Denda (rupiah): " + p.getDendaRupiah());
                writer.newLine();
                writer.write("Barang bukti: " + safe(p.getBarangBukti()));
                writer.newLine();
                writer.write("Keadaan memberatkan: " + safe(p.getKeadaanMemberatkan()));
                writer.newLine();
                writer.write("Keadaan meringankan: " + safe(p.getKeadaanMeringankan()));
                writer.newLine();
                writer.write("Source pages: " + safe(p.getSourcePages()));
                writer.newLine();
                writer.write("--------------------------------------------------");
                writer.newLine();
            }
        } catch (IOException e) {
            catatAktivitas("EKSPOR TXT gagal ke '" + namaFile + "': " + e.getMessage());
            throw e;
        }

        catatAktivitas("EKSPOR TXT ke '" + namaFile + "': " + sumber.size() + " data");
    }

    private void tulisBarisCSV(BufferedWriter writer, String... nilai)
            throws IOException {
        for (int i = 0; i < nilai.length; i++) {
            if (i > 0) {
                writer.write(',');
            }
            writer.write(escapeCSV(nilai[i]));
        }
        writer.newLine();
    }

    /** Melakukan escaping koma, kutip, dan baris baru sesuai aturan CSV. */
    private String escapeCSV(String nilai) {
        String value = safe(nilai);
        boolean perluKutip = value.contains(",") || value.contains("\"")
            || value.contains("\n") || value.contains("\r");

        if (value.contains("\"")) {
            value = value.replace("\"", "\"\"");
        }

        return perluKutip ? "\"" + value + "\"" : value;
    }

    private boolean containsIgnoreCase(String sumber, String kataKunci) {
        return sumber != null && kataKunci != null
            && sumber.toLowerCase(Locale.ROOT).contains(kataKunci.trim().toLowerCase(Locale.ROOT));
    }

    private void requireText(String value, String namaField) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(namaField + " tidak boleh kosong.");
        }
    }

    private void requireExportArguments(DynamicArray<Putusan> sumber, String namaFile) {
        if (sumber == null) {
            throw new IllegalArgumentException("Data sumber ekspor tidak boleh null.");
        }
        requireText(namaFile, "Nama file");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
