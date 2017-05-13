import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;


/**
 *
 */
public class ApproxPageRank {

    private static String input_path;
    private static String seed;
    private static double alpha;
    private static double epsilon;

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {

        input_path = args[0];
        seed = args[1];
        alpha = Double.parseDouble(args[2]);
        epsilon = Double.parseDouble(args[3]);

        HashMap<String, Double> p = new HashMap<>();
        HashMap<String, Double> r = new HashMap<>();
        p.put(seed, 0.0);
        r.put(seed, 1.0);

        //compute
        BufferedReader br = null;
        try {
            while (true) {
                boolean flag = false;
                br = new BufferedReader(new FileReader(input_path));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] vertices = StringUtils.split(line, '\t');
                    if (r.getOrDefault(vertices[0], 0.0) / (vertices.length - 1) > epsilon) {
                        push(vertices, p, r);
                        flag = true;
                    }
                }
                if (!flag) break;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (br != null) br.close();
        }

        //output
        output(p);
    }

    private static void push(String[] vertices, HashMap<String, Double> p, HashMap<String, Double> r) {
        String u = vertices[0];
        double ru = r.getOrDefault(u, 0.0);
        double ru_to_p = alpha * ru;
        double ru_to_r = (ru - ru_to_p) / 2.0;
        double ru_to_others = ru - ru_to_p - ru_to_r;

        p.put(u, p.getOrDefault(vertices[0], 0.00) + ru_to_p);
        r.put(u, ru_to_r);

        int du = vertices.length - 1;
        for (int i = 1; i < vertices.length; i++) {
            r.put(vertices[i], r.getOrDefault(vertices[i], 0.00) + ru_to_others / du);
        }
    }

    private static void output(HashMap<String, Double> p) throws IOException {

        List<Map.Entry<String, Double>> entries = new ArrayList<>(p.entrySet());

        Collections.sort(entries,
                (Map.Entry<String, Double> p1, Map.Entry<String, Double> p2) -> p2.getValue().compareTo(p1.getValue()));

        BufferedWriter output = null;
        try {
            File file = new File("output/" + seed);
            output = new BufferedWriter(new FileWriter(file));
            for (Map.Entry<String, Double> entry : entries) {
                output.write(entry.getKey() + ":" + entry.getValue()+"\n");
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (output != null) output.close();
        }
    }


}
