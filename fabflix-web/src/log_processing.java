import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class log_processing {
    public static void main(String[] args){
        boolean scale = true;
        try {
            File f = new File("C:\\Users\\heqi2\\Dropbox\\CS122B\\cs122b-spring20-team-29\\cs122b-spring20-team-29\\DemoVideoLogs\\3\\timeLogM.txt");

            BufferedReader br = new BufferedReader(new FileReader(f));
            int totalCounts = 0;
            long totalTJTime = 0;
            long totalTSTime = 0;
            String s;
            while ((s = br.readLine())!=null){
                if (!s.isEmpty()) {
                    totalCounts += 1;
                    String[] l = s.split("_");
                    totalTSTime = totalTSTime+Long.parseLong(l[0].split(":")[1]);
                    totalTJTime = totalTJTime+Long.parseLong(l[1].split(":")[1]);
                }
            }
            if (scale){
                File f2 = new File("C:\\Users\\heqi2\\Dropbox\\CS122B\\cs122b-spring20-team-29\\cs122b-spring20-team-29\\DemoVideoLogs\\3\\timeLogS.txt");
                BufferedReader br2 = new BufferedReader(new FileReader(f2));
                String s2;
                while ((s2 = br2.readLine())!=null){
                    if (!s2.isEmpty()) {
                        totalCounts += 1;
                        String[] l = s2.split("_");
                        totalTSTime = totalTSTime+Long.parseLong(l[0].split(":")[1]);
                        totalTJTime = totalTJTime+Long.parseLong(l[1].split(":")[1]);
                    }
                }
            }
            long meanTS = totalTSTime/totalCounts;
            long meanTJ = totalTJTime/totalCounts;
            System.out.println("Average TS: " + meanTS);
            System.out.println("Average TJ: " + meanTJ);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
