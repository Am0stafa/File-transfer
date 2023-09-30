/*
1. File and Meta-data Management:
   - The class keeps track of file-related metadata like the `fileID`, `file`, `fileSize`, `fileName`, and a `RandomAccessFile` object (`accFile`). It provides getter and setter methods for these fields.

2. Constructor Initialization:
   - A constructor is provided that takes a `File` and a `JTable` object as arguments. The constructor initializes a `RandomAccessFile` object for reading, extracts file information, and sets up an `ActionListener` on a `PanelStatus` object to handle user interactions for pausing and resuming file sending.

3. File Reading:
   - The `readFile` method reads a chunk of data from the file using the `RandomAccessFile` object. It reads up to 15000000 bytes at a time, adjusting the read size if near the end of the file.

4. File Sending:
   - The `startSend` method initiates the file sending process by emitting a "send_file" event to the server with file metadata. Upon receiving a callback with a file ID from the server, it begins sending the file data in chunks.
   - The `sendingFile` method is responsible for the actual sending of file data to the server in chunks, using a recursive approach to continue sending until the file is completely sent or the operation is paused.

5. User Interaction:
   - The class allows for user interaction through a `PanelStatus` object to pause and resume the file sending process. When the user resumes sending, it requests the current file length from the server to resume sending from the correct position.

6. Status Display:
   - Methods like `getPercentage` and `showStatus` are provided to calculate and display the progress of the file sending operation. It appears to update a `JTable` object to reflect the current status.

7. File Size Formatting:
   - The `getFileSizeConverted` method provides a human-readable representation of the file size, converting bytes to appropriate units (e.g., KB, MB, GB) as necessary.

8. Table Row Representation:
   - The `toRowTable` method creates an array of objects representing a row in a table, which may be used to display file information in a GUI.

9. File Closure:
   - The `close` method closes the `RandomAccessFile` object, ceasing any further file reading operations.

10. Networking:
   - Utilizes a `Socket` object for networking communications with a server, particularly for sending file data and receiving acknowledgements or other data from the server.
*/

package data;

import io.socket.client.Ack;
import io.socket.client.Socket;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DecimalFormat;
import javax.swing.JTable;
import swing.PanelStatus;

import org.json.JSONException;
import org.json.JSONObject;

public class DataReader {

    public PanelStatus getStatus() {
        return status;
    }

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public RandomAccessFile getAccFile() {
        return accFile;
    }

    public void setAccFile(RandomAccessFile accFile) {
        this.accFile = accFile;
    }

    public DataReader(File file, JTable table) throws IOException {
        //  the (r) is mode file read only
        accFile = new RandomAccessFile(file, "r");
        this.file = file;
        this.fileSize = accFile.length();
        this.fileName = file.getName();
        this.status = new PanelStatus();
        this.status.addEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!status.isPause() && pause) {
                    pause = false;
                    client.emit("r_f_l", fileID, new Ack() {
                        @Override
                        public void call(Object... os) {
                            if (os.length > 0) {
                                long length = Long.valueOf(os[0].toString());
                                try {
                                    accFile.seek(length);
                                    sendingFile(client);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        });
        this.table = table;
    }

    private int fileID;
    private File file;
    private long fileSize;
    private String fileName;
    private RandomAccessFile accFile;
    private PanelStatus status;
    private JTable table;
    private Socket client;

    public synchronized byte[] readFile() throws IOException {
        long filePointer = accFile.getFilePointer();
        if (filePointer != fileSize) {
            int max = 15000000;
            //  15MB is max send file per package
            //  we spite it to send large file
            long length = filePointer + max >= fileSize ? fileSize - filePointer : max;
            byte[] data = new byte[(int) length];
            accFile.read(data);
            return data;
        } else {
            return null;
        }
    }

    public void close() throws IOException {
        accFile.close();
    }

    public String getFileSizeConverted() {
        double bytes = fileSize;
        String[] fileSizeUnits = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        String sizeToReturn;
        DecimalFormat df = new DecimalFormat("0.#");
        int index;
        for (index = 0; index < fileSizeUnits.length; index++) {
            if (bytes < 1024) {
                break;
            }
            bytes = bytes / 1024;
        }
        sizeToReturn = df.format(bytes) + " " + fileSizeUnits[index];
        return sizeToReturn;
    }

    public double getPercentage() throws IOException {
        double percentage;
        long filePointer = accFile.getFilePointer();
        percentage = filePointer * 100 / fileSize;
        return percentage;
    }

    public Object[] toRowTable(int no) {
        return new Object[]{this, no, fileName, getFileSizeConverted(), "Next update"};
    }

    public void startSend(Socket socket) throws JSONException {
        this.client = socket;
        JSONObject data = new JSONObject();
        data.put("fileName", fileName);
        data.put("fileSize", fileSize);
        socket.emit("send_file", data, new Ack() {
            @Override
            public void call(Object... os) {
                if (os.length > 0) {
                    boolean action = (boolean) os[0];
                    if (action) {
                        fileID = (int) os[1];
                        try {
                            sendingFile(socket);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private boolean pause = false;

    private void sendingFile(Socket socket) throws IOException, JSONException {
        JSONObject data = new JSONObject();
        data.put("fileID", fileID);
        byte[] bytes = readFile();
        if (bytes != null) {
            data.put("data", bytes);
            data.put("finish", false);
        } else {
            data.put("finish", true);
            // close file
            close();
            status.done();
        }
        socket.emit("sending", data, new Ack() {
            @Override
            public void call(Object... os) {
                if (os.length > 0) {
                    boolean act = (boolean) os[0];
                    if (act) {
                        try {
                            if (!status.isPause()) {
                                showStatus((int) getPercentage());
                                sendingFile(socket);
                            } else {
                                pause = true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void showStatus(int values) {
        status.showStatus(values);
        table.repaint();
    }
}
