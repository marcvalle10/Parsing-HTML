import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String url = "https://people.sc.fsu.edu/~jburkardt/data/csv/csv.html";
        Document doc = Jsoup.connect(url).get();

        List<Thread> threads = new ArrayList<>();

        for (Element link : doc.select("a[href$=.csv]")) {
            String csvUrl = link.attr("abs:href");
            String csvFileName = link.text();

            Thread thread = new Thread(() -> {
                try {
                    int lineCount = getLineCount(csvUrl);
                    System.out.println(csvFileName + " - " + lineCount + " lines");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    private static int getLineCount(String csvUrl) throws IOException {
        URL url = new URL(csvUrl);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            int lineCount = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
            }
            return lineCount;
        }
    }
}
