import java.util.Scanner;

public class Pathfinding {
    // 0 Nothing
    // 1 Obstacle
    // 2 Start
    // 3 Finish
    // 4 Closed
    // 5 Open
    //Positions: 0: x 1:y 2: type 3: Cost 4: ParentX 5: ParentY 6:ParentFromStartCost
    static int[] start =    {3, 1, 2 , 0 , 0 , 0,  0};
    static int[] finish =   {6 ,4 , 3, 0 , 0 ,  0,  0};
    static int[] current =  start;

    static boolean stepTaken = false;



    static byte[][] map = {
            {0, 0, 0, 0, 0, 1, 0},
            {0, 0, 0, 0, 1, 0, 0},
            {0, 0, 1, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0}
    };
    static PriorityDictionary open = new PriorityDictionary((map.length+1)*(map[0].length+1));
    static int[][][] closed = new int[map.length][map[0].length][6];

    public static void init(){
        addToMap(start);
        addToMap(finish);
    }

    public static boolean menu(){
        System.out.println("Karte anzeigen.........0");
        System.out.println("Schritt machen.........1");
        System.out.println("Ablaufen lassen........2");
        System.out.println("Pfad Anzeigen..........3");
        if (!stepTaken){
        System.out.println("Hinderniss Hinzufügen..4");
        System.out.println("Start Festlegen........5");
        System.out.println("Ziel Festlegen.........6");
        }
        System.out.println("Beenden...............-1");

        Scanner scan = new Scanner(System.in);

        switch (scan.nextInt()){
            case 0 -> anzeigen();
            case 1 -> step();
            case 2 -> findPath();
            case 3 -> {
                int[][] path = retracePath();
                for (int i = 0; i < path.length; i++) {
                    if (path[i][2] != 0) System.out.println("X:"+ path[i][0] +"Y:"+ path[i][1]);
                }
            }
            case 4 -> {
                System.out.println("X vom Hinderniss");
                int x = scan.nextInt();
                System.out.println("Y vom Hinderniss");
                int y = scan.nextInt();
                addToMap(new int[]{x,y,1});
            }

            case 5 -> {
                System.out.println("X vom Start");
                int x = scan.nextInt();
                System.out.println("Y vom Start");
                int y = scan.nextInt();
                addToMap(new int[]{start[0],start[1],0});
                start[0] = x;
                start[1] = y;
                addToMap(new int[]{x,y,2});
            }
            case 6 -> {
                System.out.println("X vom Ziel");
                int x = scan.nextInt();
                System.out.println("Y vom Ziel");
                int y = scan.nextInt();
                addToMap(new int[]{finish[0],finish[1],0});
                finish[0] = x;
                finish[1] = y;
                addToMap(new int[]{x,y,3});
            }

            default -> System.out.println("Enter Valid Value");
            case -1 -> {
                return true;
            }
        }

        return false;
    }

    private static void anzeigen() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                // 0 Nothing
                // 1 Obstacle   Util.RED
                // 2 Start      Util.GREEN
                // 3 Finish     Util.BLUE
                // 4 Closed     Util.PURPLE
                // 5 Open       Util.YELLOW

                switch (map[i][j]){
                    case 0 -> System.out.printf("%s",Util.RESET + "◼︎"  + Util.RESET);
                    case 1 -> System.out.printf("%s",Util.RED + "◼︎"    + Util.RESET);
                    case 2 -> System.out.printf("%s",Util.GREEN + "◼︎"  + Util.RESET);
                    case 3 -> System.out.printf("%s",Util.CYAN + "◼︎"   + Util.RESET);
                    case 4 -> System.out.printf("%s",Util.PURPLE + "◼︎" + Util.RESET);
                    case 5 -> System.out.printf("%s",Util.YELLOW + "◼︎" + Util.RESET);
                }
                System.out.print("  ");

            }
            System.out.println();
        }
    }
    private static void findPath(){
        boolean found = false;
        while (!found){
            found = step();
            if (open.size()==0){
                found=true;
                System.out.println("No Valid Answer");
            }
        }
    }

    private static boolean step(){
        stepTaken = true;
        int[][] neighbours = getNeighbours(current);
        int[][] openValues  =  open.getValues();


        for (int j = 0; j < neighbours.length; j++) {
            neighbours[j][3] = heuristic(neighbours[j],finish) + neighbours[j][6];
            boolean added = false;
            if(j%2==0){
                if (neighbours[(j+1)%neighbours.length][2]!=1 &&
                        neighbours[((j-1) % neighbours.length + neighbours.length) % neighbours.length][2]!=1){
                    checkIfIn(neighbours, openValues, j, added);
                }

            }
            else {
                checkIfIn(neighbours, openValues, j, added);
            }

            addToMap(neighbours[j]);



        }

        current[2] = 4;
        addToMap(current);
        closed[current[1]][current[0]] = current;
        if (current[0] == finish[0] && current[1] == finish[1])
        {
            System.out.println("WON");
            return true;
        }

        current = open.pop();

        return false;
    }

    public static void checkIfIn(int[][] neighbours, int[][] openValues, int j, boolean added) {
        for (int i = 0; i < openValues.length; i++) {
            if (openValues[i][0] == neighbours[j][0] &&
                    openValues[i][1] == neighbours[j][1]
            ) added = true;
        }

        if (neighbours[j][2] != 1 && neighbours[j][2] != 4) {
            neighbours[j][2] = 5;
            if (!added) {
                open.put(neighbours[j][3], neighbours[j]);
            }
        }
    }

    private static int[][] getNeighbours(int[] pos){
        int[] dx = {1, 1, 1, 0, -1, -1, -1, 0};
        int[] dy = {1, 0, -1, -1, -1, 0, 1, 1};

        int[][] neighbours = new int[dx.length][7];

        for (int i = 0; i < dx.length; i++) {
            int neighborX = pos[0] + dx[i];
            int neighborY = pos[1] + dy[i];

            // Check if the neighbor is within the grid bounds
            if (neighborX >= 0 && neighborX < map[0].length &&
                    neighborY >= 0 && neighborY < map.length) {
                //Positions: 0: x 1:y 2: type 3: Cost 4: ParentX 5: ParentY
                int diffCost = 10;
                if (Math.abs(dx[i]) == Math.abs(dy[i])) diffCost = 14;



                neighbours[i] = new int[]{neighborX, neighborY, map[neighborY][neighborX], 0, pos[0], pos[1],diffCost + pos[6]};

            }
        }

        return neighbours;

    }

    private static int heuristic(int[] start,int[] finish){
        int diffX = Math.abs( finish[0] - start[0]);
        int diffY = Math.abs( finish[1] - start[1]);

        return (diffY+diffX)*10;
    }
    private static void addToMap(int[] pos){
        map[pos[1]][pos[0]] = (byte) pos[2];
    }


    public static int[][] retracePath(){
        int[][] path = new int[map.length * map[0].length][7];
        int[] cur = current;
        int i = 0;
        do {
            path[i] = cur;
            cur = closed[cur[5]][cur[4]];
            i++;
        }
        while (start != cur);
        path[i] = cur;
        return path;
    }
}
