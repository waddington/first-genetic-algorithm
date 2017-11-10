import java.util.ArrayList;
import java.util.HashSet;

public class AStar {
    HashSet<String> wallCoordinates;
    int[] targetCoordinates;
    Node[][] nodes;

    int boardWidth;
    int boardHeight;
    int tileSize;

    AStar(HashSet<String> wallCoordinates, int[] targetCoordinates, int boardWidth, int boardHeight, int tileSize) {
        this.wallCoordinates = wallCoordinates;
        this.targetCoordinates = targetCoordinates;

        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.tileSize = tileSize;

        createNodes();
    }

    void createNodes() {
        int tilesX = this.boardWidth / this.tileSize;
        int tilesY = this.boardHeight / this.tileSize;
        this.nodes = new Node[tilesX][tilesY];

        // Create the nodes
        for (int i=0; i<tilesX; i++) {
            for (int j=0; j<tilesY; j++) {
                Node node = new Node();
                node.setX(i);
                node.setY(j);

                this.nodes[i][j] = node;
            }
        }

        // Get the nodes' neighbours'
        for (int i=0; i<tilesX; i++) {
            for (int j=0; j<tilesY; j++) {
                this.nodes[i][j].findNeighbours();
                this.nodes[i][j].calculateH(this.nodes[this.targetCoordinates[0]][this.targetCoordinates[1]]);
            }
        }
    }

    int getScore(Agent agent) {
        ArrayList<Node> openSet = new ArrayList<>();
        ArrayList<Node> closedSet = new ArrayList<>();
        // Ensure agent is in bounds, move from death position to within
        int[] agentRealCoords = new int[] {agent.getCurrentCoordinates()[0], agent.getCurrentCoordinates()[1]};
        if (agentRealCoords[0] < 0)
            agentRealCoords[0] = 0;
        if (agentRealCoords[1] < 0)
            agentRealCoords[1] = 0;
        if (agentRealCoords[0] > 69)
            agentRealCoords[0] = 69;
        if (agentRealCoords[1] > 49)
            agentRealCoords[1] = 49;

        Node start = this.nodes[agentRealCoords[0]][agentRealCoords[1]];
        Node target = this.nodes[this.targetCoordinates[0]][this.targetCoordinates[1]];
        int score = 0;

        openSet.add(start);

        while (!openSet.isEmpty()) {
            // Get current lowest score in remaining nodes to check
            int winner = 0;
            for (int i=0; i<openSet.size(); i++) {
                if (openSet.get(i).getF() < openSet.get(winner).getF()) {
                    winner = i;
                }
            }

            Node currentNode = openSet.get(winner);

            // Check if the winner is the target node
            if (currentNode == target) {
                // find the path - work backwards from current
                score = currentNode.getF();
                break;
            }

            closedSet.add(currentNode);
            openSet.remove(currentNode);

            // Add valid neighbour nodes to openSet
            ArrayList<Node> neighbours = currentNode.getNeighbours();
            for (int i=0; i<neighbours.size(); i++) {
                Node neighbour = neighbours.get(i);
                // Check if we have already evaluated this neighbour
                if (!closedSet.contains(neighbour)) {
                    int tempG = currentNode.getG() + 1;

                    // Check if we have already previously calculated G to be a lower value and if so keep it
                    boolean betterNewPath = false;
                    if (openSet.contains(neighbour)) {
                        if (tempG < neighbour.getG()) {
                            neighbour.setG(tempG);
                            betterNewPath = true;
                        }
                    } else {
                        neighbour.setG(tempG);
                        openSet.add(neighbour);
                        betterNewPath = true;
                    }

                    // Update the neighbours heuristic value
                    if (betterNewPath) {
//                        neighbour.setH(calculateH(neighbour, target));
                        neighbour.setF(neighbour.getG() + neighbour.getH());
                        neighbour.setPreviousNode(currentNode);
                    }
                }
            }

        }

        return score;
    }

    int calculateH(Node n, Node target) {
        // Using manhattan heuristic

        int partA = Math.abs(n.getX() - target.getX());
        int partB = Math.abs(n.getY() - target.getY());

        return (partA + partB);
    }

    class Node {
        int x;
        int y;

        int f;
        int g;
        int h;

        ArrayList<Node> neighbours;
        Node previousNode = null;

        Node() {
            this.f = 0;
            this.g = 0;
            this.h = 0;

            this.neighbours = new ArrayList<>();
        }

        int getX() {
            return this.x;
        }
        void setX(int x) {
            this.x = x;
        }

        int getY() {
            return this.y;
        }
        void setY(int y) {
            this.y = y;
        }

        int getF() {
            return this.f;
        }
        void setF(int f) {
            this.f = f;
        }

        int getG() {
            return this.g;
        }
        void setG(int g) {
            this.g = g;
        }

        int getH() {
            return this.h;
        }
        void setH(int h) {
            this.h = h;
        }

        Node getPreviousNode() {
            return this.previousNode;
        }
        void setPreviousNode(Node previousNode) {
            this.previousNode = previousNode;
        }

        boolean isInBounds(int[] coord) {
            if (coord[0] < 0)
                return false;
            if (coord[1] < 0)
                return false;

            if (coord[0] > (boardWidth / tileSize)-1)
                return false;
            if (coord[1] > (boardHeight / tileSize)-1)
                return false;

            return true;
        }
        boolean isInWall(int[] coord) {
            String wallFormat = coord[0]+"-"+coord[1];

            return wallCoordinates.contains(wallFormat);
        }

        void findNeighbours() {
            int[] up = new int[] {this.x, this.y-1};
            int[] right = new int[] {this.x+1, this.y};
            int[] down = new int[] {this.x, this.y+1};
            int[] left = new int[] {this.x-1, this.y};

            // check if each is valid
            if (isInBounds(up) && !isInWall(up))
                this.neighbours.add(nodes[up[0]][up[1]]);

            if (isInBounds(right) && !isInWall(right))
                this.neighbours.add(nodes[right[0]][right[1]]);

            if (isInBounds(down) && !isInWall(down))
                this.neighbours.add(nodes[down[0]][down[1]]);

            if (isInBounds(left) && !isInWall(left))
                this.neighbours.add(nodes[left[0]][left[1]]);
        }
        ArrayList<Node> getNeighbours() {
            return this.neighbours;
        }

        void calculateH(Node target) {
            // Using manhattan heuristic

            int partA = Math.abs(getX() - target.getX());
            int partB = Math.abs(getY() - target.getY());

            setH(partA + partB);
        }
    }
}
