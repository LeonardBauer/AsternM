public class Test {
    public static void main(String[] args) {
        Pathfinding.init();
        boolean shouldClose = false;
        while (!shouldClose){
            shouldClose = Pathfinding.menu();
        }
    }
}
