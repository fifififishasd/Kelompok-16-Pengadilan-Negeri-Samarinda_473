import struktur.DynamicArray;
import model.Putusan;

public class Main {

    public static void main(String[] args) {

        DynamicArray<String> data = new DynamicArray<>(2);

        System.out.println( "=== UJI DYNAMIC ARRAY ===");
        System.out.println("awal kosong? " + data.isEmpty());
       

        data.add( "Putusan-001");
        data.add( "Putusan-002");
        System.out.println("Data" + data);
        System.out.println("Size: " + data.size());
        System.out.println("Capacity " + data.capacity());

        data.add( "Putusan-003");
        System.out.println("Setelah resize" + data);
        System.out.println("Capacity baru" + data.capacity());

        System.out.println("get(1): "+ data.get(1));
        System.out.println("search putusan-003:" + data.search( "Putusan-003"));
        System.out.println("contains putusan-009:" + data.contains( "Putusan-009"));

        data.insert(1,  "Putusan-Sisipan");
        System.out.println("Setelah insert:" + data);

        System.out.println("remove(1): " + data.remove(1));
        System.out.println("Setelah remove:" + data);
        System.out.println("Invariant valid" + data.validateInvariant());

        data.clear();
        System.out.println("setelah clear" + data);
        System.out.println("isEmpty" + data.isEmpty());
        System.out.println("Invariant setelah clear" + data.validateInvariant());

    }
}