
package cpu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

class Process {
    int processID;
    int arrivalTime;
    int burstTime;
    int priority;

    public Process(int processID, int priority) {
        this.processID = processID;
        this.priority = priority;
        this.arrivalTime = 0;  // For simplicity, arrival time is set to 0.
        this.burstTime = new Random().nextInt(10) + 1;  // Random burst time between 1 and 10.
    }
}



class MultilevelQueueScheduler {
    private final int numQueues;
    private final Queue<Process>[] queues;

    public MultilevelQueueScheduler(int numQueues) {
        this.numQueues = numQueues;
        this.queues = new Queue[numQueues];
        for (int i = 0; i < numQueues; i++) {
            this.queues[i] = new LinkedList<>();
        }
    }

    public void enqueueProcess(Process process) {
        int priority = process.priority;
        queues[priority].add(process);
    }

    public Process dequeueProcess() {
        for (int i = numQueues - 1; i >= 0; i--) {
            if (!queues[i].isEmpty()) {
                return queues[i].poll();
            }
        }
        return null;
    }

    public void runScheduler(int totalProcesses, JTextArea outputTextArea) {
        int currentTime = 0;
        int processesCreated = 0;

        while (hasProcesses() && processesCreated < totalProcesses) {
            Process currentProcess = dequeueProcess();

            if (currentProcess != null) {
                executeProcess(currentProcess);
                enqueueProcess(currentProcess);

                String output = "Process " + currentProcess.processID + " executed at time " + currentTime +
                        ". Remaining burst time: " + currentProcess.burstTime + "\n";
                if (currentProcess.burstTime <= 0) {
                    output += ". Process completed.";
                }

                output += "\n";
                outputTextArea.append(output);

                processesCreated++;
            }

            currentTime++;
        }
    }

    private void executeProcess(Process process) {
        int timeQuantum = 2;  // Example time quantum
        process.burstTime -= timeQuantum;

        // Simulate completion or continue execution as needed
        if (process.burstTime <= 0) {
            System.out.println("Process " + process.processID + " completed.");
        }
    }

    private boolean hasProcesses() {
        for (Queue<Process> queue : queues) {
            if (!queue.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}




public class Cpu extends JFrame {
    private JTextField numQueuesField;
    private JTextField numProcessesField;
    private JTextArea outputTextArea;

    public Cpu() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Multilevel Queue Scheduler");

        JLabel numQueuesLabel = new JLabel("Number of Queues:      ");
        numQueuesField = new JTextField(10);

        JLabel numProcessesLabel = new JLabel("Number of Processes:");
        numProcessesField = new JTextField(10);

        JButton runButton = new JButton("Run Scheduler");
        JButton resetButton = new JButton("Reset");
        
        resetButton.setPreferredSize(new Dimension(150, 30));

        outputTextArea = new JTextArea(20, 50);
        outputTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputTextArea);

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runScheduler();
            }
        });
        
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFields();
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(numQueuesLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(numQueuesField))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(numProcessesLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(numProcessesField))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(runButton)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(resetButton))
                                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(numQueuesLabel)
                                        .addComponent(numQueuesField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(numProcessesLabel)
                                        .addComponent(numProcessesField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(runButton)
                                        .addComponent(resetButton))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(20, Short.MAX_VALUE))
        );


        pack();
    }
    private void resetFields() {
        numQueuesField.setText("");
        numProcessesField.setText("");
        outputTextArea.setText("");
    }

    private void runScheduler() {
        try {
            int numQueues = Integer.parseInt(numQueuesField.getText());
            int numProcesses = Integer.parseInt(numProcessesField.getText());

            // Validate input values
            if (numQueues <= 0 || numProcesses <= 0) {
                showError("Number of queues and processes must be positive integers.");
                return;
            }

            MultilevelQueueScheduler scheduler = new MultilevelQueueScheduler(numQueues);

            for (int i = 1; i <= numProcesses; i++) {
                int randomPriority = new Random().nextInt(numQueues);
                Process process = new Process(i, randomPriority);
                scheduler.enqueueProcess(process);
            }

            outputTextArea.setText(""); // Clear previous output
            scheduler.runScheduler(numProcesses, outputTextArea);
            
            

        } catch (NumberFormatException e) {
            showError("Invalid input. Please enter valid positive integers for the number of queues and processes.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Cpu().setVisible(true);
            }
        });
    }
}

