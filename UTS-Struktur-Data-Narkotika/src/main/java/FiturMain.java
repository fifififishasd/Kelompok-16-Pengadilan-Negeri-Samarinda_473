import model.Putusan;
import service.HasilPencarian;
import service.PutusanService;
import struktur.DynamicArray;

import java.io.IOException;
import java.time.LocalDate;


public class FiturMain {
    public static void main(String[] args) {
        DynamicArray<Putusan> dataPutusan = new DynamicArray<>(50);

        
        dataPutusan.add(new Putusan(
            "PN495", "495/Pid.Sus/2026/PN Smr",
            "Pengadilan Negeri Samarinda", LocalDate.of(2026, 10, 5),
            "T-001", "Sabu (Metamfetamina)", 0.64,
            "Pasal 114 Ayat (1) UU 35/2009",
            "Pasal 114 Ayat (1) UU 35/2009", 48, 36, 1000000000L,
            "6 poket sabu; bungkus rokok WIN CLICK", "Meresahkan masyarakat",
            "Mengakui dan menyesali perbuatan", "2-7,12,20,25-26"));

        dataPutusan.add(new Putusan(
            "PN-CONTOH-002", "12/Pid.Sus/2026/PN X",
            "Pengadilan Negeri Contoh X", LocalDate.of(2026, 8, 12),
            "T-002", "Ganja", 3.50,
            "Pasal 111 UU 35/2009", "Pasal 111 UU 35/2009",
            30, 24, 500000000L, "Daun ganja", "Perbuatan meresahkan",
            "Belum pernah dihukum", "1-5,18"));

        dataPutusan.add(new Putusan(
            "PN-CONTOH-003", "20/Pid.Sus/2026/PN Y",
            "Pengadilan Negeri Contoh Y", LocalDate.of(2026, 9, 1),
            "T-003", "Sabu (Metamfetamina)", 1.20,
            "Pasal 114 UU 35/2009", "Pasal 114 UU 35/2009",
            60, 48, 1000000000L, "Paket sabu", "Meresahkan masyarakat",
            "Mengakui perbuatan", "2-6,20-24"));

        PutusanService service = new PutusanService(dataPutusan);

       
        System.out.println("=== CARI NOMOR PERKARA ===");
        HasilPencarian hasilCari = service.cariNomorPerkara(
            "495/Pid.Sus/2026/PN Smr");
        tampilkanHasilCari(hasilCari);

       
        System.out.println("\n=== FILTER JENIS NARKOTIKA ===");
        DynamicArray<Putusan> hasilFilter =
            service.filterJenisNarkotika("Sabu");
        tampilkanRingkas(hasilFilter);

        System.out.println("\n=== FILTER GABUNGAN ===");
        DynamicArray<Putusan> hasilGabungan = service.filterGabungan(
            "Sabu", 0.5, 2.0, "Pasal 114", 24, 48);
        tampilkanRingkas(hasilGabungan);

      
        System.out.println("\n=== URUTKAN VONIS ASCENDING ===");
        DynamicArray<Putusan> hasilUrut = service.urutkanVonis(true);
        tampilkanRingkas(hasilUrut);
        System.out.println("Size arsip utama tetap: " + dataPutusan.size());
        System.out.println("Invariant arsip utama: " + dataPutusan.validateInvariant());

        
        service.catatAktivitas("TAMBAH data contoh untuk demonstrasi");

        
        try {
            service.eksporCSV(hasilFilter, "hasil-filter.csv");
            service.eksporTXT(hasilUrut, "hasil-urut.txt");
            System.out.println("\nFile ekspor berhasil dibuat.");
        } catch (IOException e) {
            System.out.println("Ekspor gagal: " + e.getMessage());
        }

        
        service.tampilkanAudit();
    }

    private static void tampilkanHasilCari(HasilPencarian hasil) {
        if (hasil.isDitemukan()) {
            Putusan p = hasil.getPutusan();
            System.out.println("Data ditemukan!");
            System.out.println("Case ID: " + p.getCaseId());
            System.out.println("Nomor perkara: " + p.getNomorPerkara());
            System.out.println("Pengadilan: " + p.getPengadilan());
            System.out.println("Jenis narkotika: " + p.getJenisNarkotika());
            System.out.println("Vonis: " + p.getVonisBulan() + " bulan");
            System.out.println("Denda: Rp" + p.getDendaRupiah());
            System.out.println("Indeks: " + hasil.getIndeks());
        } else {
            System.out.println("Data tidak ditemukan.");
        }
        System.out.println("Jumlah langkah: " + hasil.getJumlahLangkah());
    }

    private static void tampilkanRingkas(DynamicArray<Putusan> data) {
        for (int i = 0; i < data.size(); i++) {
            Putusan p = data.get(i);
            System.out.println(
                p.getCaseId() + " | " + p.getJenisNarkotika()
                + " | berat " + p.getBeratBarangBuktiGram()
                + " gram | veins " + p.getVonisBulan() + " bulan");
        }
        System.out.println("Total: " + data.size() + " putusan");
    }
}
