import java.util.Random;

public class RouteChoice{
    public static String getDatasource(){
        Random random = new Random();
        boolean choice = random.nextBoolean();

//        if(choice){
//            return "jdbc/masterdb";
//        }
//        else{
//            return "jdbc/slavedb";
//        }
        return "jdbc/moviedb";
    }

}