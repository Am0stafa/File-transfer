// create the client GUI and handle the client-side logic for the file transfer application
// It initializes a Socket object to connect to a server. It listens for events like "exit_app" and "new_file" and handles them appropriately. 

package client;

import data.DataFileServer;
import data.DataReader;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.awt.Component;
import java.io.File;
import java.net.URISyntaxException;

import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.json.JSONException;
import org.json.JSONObject;

import swing.CellEditor;
import swing.CellEditorFile;

/*
 * This class combines a Swing-based GUI with Socket. IO-based network communication to allow users to connect to a server, send and receive file data, and view files on the server. It also handles UI updates based on file transfer status and server responses.
 */

public class Main_Client extends javax.swing.JFrame {
  
  /*
   * The Main_Client class initializes the table cell renderers and editors for two different types of data objects (DataReader and DataFileServer).
   * Example Usage:
   * Main_Client client = new Main_Client();
   */
  public Main_Client(){
        initComponents(); // init the gui component
        // rending of the tables and set the cell editor
        model = (DefaultTableModel) table.getModel();
        modelFile = (DefaultTableModel) tableFile.getModel();

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                Component com = super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
                Object data = jtable.getValueAt(i, 0);
                if (data instanceof DataReader) {
                    DataReader reader = (DataReader) data;
                    Component c = reader.getStatus();
                    c.setBackground(com.getBackground());
                    return c;
                } else {
                    return com;
                }
            }
        });

        table.getColumnModel().getColumn(4).setCellEditor(new CellEditor());

        tableFile.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                Component com = super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
                DataFileServer data = (DataFileServer) jtable.getValueAt(i, 0);
                Component c = data.getItem();
                c.setBackground(com.getBackground());
                return c;
            }
        });

        tableFile.getColumnModel().getColumn(4).setCellEditor(new CellEditorFile());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * Sets up the GUI layout, components, and event listeners for buttons like "Connect" and "File".
     * cmdConnect button is linked to cmdConnectActionPerformed(), which handles the action of connecting to the server.
     * jButton1 is linked to jButton1ActionPerformed(), which opens a file chooser dialog to select files for transfer.
     */
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        txtName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        cmdConnect = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableFile = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Data", "No", "File Name", "Size", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setPreferredWidth(0);
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(1).setPreferredWidth(50);
        }

        jLabel1.setText("Name");

        cmdConnect.setText("Connect");
        cmdConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdConnectActionPerformed(evt);
            }
        });

        jButton1.setText("File");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdConnect)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdConnect))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Client", jPanel1);

        tableFile.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Data", "No", "FIle Name", "Size", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tableFile);
        if (tableFile.getColumnModel().getColumnCount() > 0) {
            tableFile.getColumnModel().getColumn(0).setMinWidth(0);
            tableFile.getColumnModel().getColumn(0).setPreferredWidth(0);
            tableFile.getColumnModel().getColumn(0).setMaxWidth(0);
            tableFile.getColumnModel().getColumn(1).setPreferredWidth(50);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("File On Server", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addGap(0, 0, 0))
        );

        pack();
        setLocationRelativeTo(null);
    }

    private final int DEFAULT_PORT = 9999;
    private Socket client;
    private String IP = "localhost";

    private final DefaultTableModel model;
    private final DefaultTableModel modelFile;
    
    /**
     * Handles the action when the "Connect" button is clicked in the GUI.
     * Establishes a connection with a server using a socket, sets up event listeners for specific events and sends/receives data to/from the server.
     * @param evt The event object representing the action performed (in this case, clicking the "Connect" button).
     * basically its is triggered when the "Connect" button is clicked. It establishes a socket connection, sets up listeners for socket events, and emits events to the server and sets up event listeners for different socket events like "exit_app" and "new_file".
     */
    private void cmdConnectActionPerformed(java.awt.event.ActionEvent evt) {
        if (client == null) {
            try {
                client = IO.socket("http://" + IP + ":" + DEFAULT_PORT);

                client.on("exit_app", new Emitter.Listener() {
                    @Override
                    public void call(Object... os) {
                        System.exit(0);
                    }
                });
                client.on("new_file", new Emitter.Listener() {
                    @Override
                    public void call(Object... os) {
                        try {
                            addFile(new DataFileServer((JSONObject) os[0], table, client));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                client.open();
                String userName = txtName.getText().trim();
                client.emit("set_user", userName);
                client.emit("request", "list_file", new Ack() {
                    @Override
                    public void call(Object... os) {
                        try {
                            for (Object o : os) {
                                addFile(new DataFileServer((JSONObject) o, table, client));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (URISyntaxException e) {
                System.err.println(e);
            }
        } else {
            client.emit("set_user", txtName.getText().trim());
        }
    }

    /**
     * Event handler for the "File" button in the GUI.
     * Allows the user to select multiple files using a file chooser dialog and then adds each selected file to a table in the GUI.
     * It uses DataReader to initiate the sending process.
     * @param evt The event object representing the button click event.
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser ch = new JFileChooser();
        ch.setMultiSelectionEnabled(true);
        int opt = ch.showOpenDialog(this);
        if (opt == JFileChooser.APPROVE_OPTION) {
            File[] files = ch.getSelectedFiles();
            for (File file : files) {
                try {
                    DataReader reader = new DataReader(file, table);
                    model.addRow(reader.toRowTable(table.getRowCount() + 1));
                    reader.startSend(client);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Adds a new row to the tableFile table in the GUI with the data from the DataFileServer object.
     * basically its a method adds file information to the tableFile model, presumably to display files available on the server.
     * @param data The DataFileServer object containing the data to be added to the table.
     */
    private void addFile(DataFileServer data) {
        modelFile.addRow(data.toTableRow(tableFile.getRowCount() + 1));
    }

    // Main method for the Main_Client class and makes the Main_Client window visible, starting the client-side application.
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main_Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main_Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main_Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main_Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main_Client().setVisible(true);
            }
        });
    }

    private javax.swing.JButton cmdConnect;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable table;
    private javax.swing.JTable tableFile;
    private javax.swing.JTextField txtName;
}
