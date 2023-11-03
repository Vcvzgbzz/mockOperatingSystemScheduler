import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Process {
    int arrivalTime;
    int runningTime;
    int priority;
    int processId;
    int timeLeft;

    public Process(int arrivalTime, int runningTime, int priority, int processId, int timeLeft) {
        this.arrivalTime = arrivalTime;
        this.runningTime = runningTime;
        this.priority = priority;
        this.processId = processId;
        this.timeLeft = timeLeft;
    }
    public void print(){
        System.out.println("Process " + processId + " is running for " + runningTime + " more cycles.");
    }

    public void run() {
        print();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public int compareTo(Process other) {
        return Integer.compare(this.priority, other.priority);
    }
}

class BinaryHeap {
    List<Process> heap;

    public BinaryHeap() {
        heap = new ArrayList<>();
    }

    public void offer(Process process) {
        heap.add(process);
        int index = heap.size() - 1;
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (heap.get(index).priority < heap.get(parentIndex).priority) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    public Process poll() {
        if (heap.isEmpty()) {
            return null;
        }

        Process root = heap.get(0);
        int lastIndex = heap.size() - 1;
        heap.set(0, heap.get(lastIndex));
        heap.remove(lastIndex);
        lastIndex--;

        int index = 0;
        while (true) {
            int leftChildIndex = 2 * index + 1;
            int rightChildIndex = 2 * index + 2;
            int smallest = index;

            if (leftChildIndex <= lastIndex && heap.get(leftChildIndex).priority < heap.get(smallest).priority) {
                smallest = leftChildIndex;
            }

            if (rightChildIndex <= lastIndex && heap.get(rightChildIndex).priority < heap.get(smallest).priority) {
                smallest = rightChildIndex;
            }

            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }

        return root;
    }

    private void swap(int i, int j) {
        Process temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }
}

public class Processor {
    public static List<String> readFile(String filePath) {
        List<String> lines = new ArrayList<>();

        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    public static int[][] compileFile() {
        String filePath = "src/input.txt";

        List<String> fileContent = readFile(filePath);

        int[][] array = new int[fileContent.size()][3];

        for (int i = 0; i < fileContent.size(); i++) {
            String line = fileContent.get(i);
            String[] values = line.split(" ");

            if (values.length != 3) {
                // Handle incorrect formatting in the input file (only handles incorrect lengths)
                System.err.println("Incorrect format in line " + (i + 1) + ": '" + line + "'");
                continue;
            }

            for (int j = 0; j < 3; j++) {
                array[i][j] = Integer.parseInt(values[j]);
            }
        }

        return array;
    }

    public static void main(String[] args) {
        BinaryHeap processQueue = new BinaryHeap();
        int processId = 0;
        int currentTime = 0;

        int[][] processes = compileFile();

        for (int[] processInfo : processes) {
            int arrivalTime = processInfo[0];
            int runningTime = processInfo[1];
            int priority = processInfo[2];

            while (currentTime < arrivalTime) {
                if (!processQueue.isEmpty()) {
                    Process currentProcess = processQueue.poll();
                    currentProcess.run();
                    currentProcess.runningTime--;
                    if (currentProcess.runningTime > 0) {
                        processQueue.offer(currentProcess);
                    } else {
                        System.out.println("Process " + currentProcess.processId + " has completed.");
                    }
                } else {
                    System.out.println("CPU is idle at time " + currentTime);
                }
                currentTime++;
            }

            Process newProcess = new Process(arrivalTime, runningTime, priority, processId,arrivalTime);
            processQueue.offer(newProcess);
            processId++;
        }

        while (!processQueue.isEmpty()) {
            Process currentProcess = processQueue.poll();
            currentProcess.run();
            currentProcess.runningTime--;
            if (currentProcess.runningTime > 0) {
                processQueue.offer(currentProcess);
            } else {
                System.out.println("Process " + currentProcess.processId + " has completed.");
            }
            currentTime++;
        }
    }
}
