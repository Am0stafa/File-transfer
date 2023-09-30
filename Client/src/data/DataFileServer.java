/*
1. Maintaining File Information:
   - The class keeps track of certain information related to a file, such as its ID (`fileID`), name (`fileName`), size (`fileSize`), output path (`outPutPath`), and status (`status`). There are getter and setter methods provided for each of these fields.

2. Initialization:
   - There are two constructors in this class. One initializes a `DataFileServer` object with specified values for `fileID`, `fileName`, `fileSize`, `outPutPath`, and `status`. The other initializes a `DataFileServer` object based on a `JSONObject`, a `JTable`, and a `Socket` object, extracting file information from the `JSONObject`.

3. File Saving Functionality:
   - There's a private method `saveFile` which seems to handle writing data to a file. It appears to be utilizing a `DataWriter` object for writing data, and a `Socket` object for requesting data from a server. It also updates a `PanelStatus_Item` object and a `JTable` object to reflect the current status of the file being saved.

4. User Interaction for File Saving:
   - There are action listeners added to a `PanelStatus_Item` object (`item`) within the constructor that initializes with a `JSONObject`. These listeners handle user interactions for saving a file to a specified location and pausing/resuming file saving.

5. Table Row Representation:
   - There's a method `toTableRow` that seems to create an array of objects representing a row in a table, which might be used to display file information in a GUI.

6. Networking:
   - The class communicates with a server to request file data via a `Socket` object. In the `saveFile` method, it emits a "request_file" event to the server with the `fileID` and current file length and receives file data in response, which is then written to the file.
*/
package data;

import io.socket.client.Ack;
import io.socket.client.Socket;
import java.io.File;
import java.io.IOException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JTable;
import swing.PanelStatus_Item;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author RAVEN
 */
public class DataFileServer {

    public DataFileServer(int fileID, String fileName, String fileSize, File outPutPath, boolean status) {
        this.fileID = fileID;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.outPutPath = outPutPath;
        this.status = status;
    }

    public PanelStatus_Item getItem() {
        return item;
    }

    public void setItem(PanelStatus_Item item) {
        this.item = item;
    }

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public File getOutPutPath() {
        return outPutPath;
    }

    public void setOutPutPath(File outPutPath) {
        this.outPutPath = outPutPath;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    public DataFileServer(JSONObject json, JTable table, Socket socket) throws JSONException {
        fileID = json.getInt("fileID");
        fileName = json.getString("fileName");
        fileSize = json.getString("fileSize");
        fileSizeLength = json.getLong("fileSizeLength");
        item = new PanelStatus_Item();
        this.table = table;
        this.socket = socket;
        item.addEventSave(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser ch = new JFileChooser();
                ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int opt = ch.showSaveDialog(null);
                if (opt == JFileChooser.APPROVE_OPTION) {
                    outPutPath = new File(ch.getSelectedFile().getAbsolutePath() + "/" + fileName);
                    item.startFile();
                    try {
                        saveFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        item.addEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!item.isPause() && pause) {
                    pause = false;
                    try {
                        saveFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private int fileID;
    private String fileName;
    private String fileSize;
    private long fileSizeLength;
    private File outPutPath;
    private boolean status;
    private PanelStatus_Item item;
    private JTable table;
    private DataWriter writer;
    private Socket socket;
    private boolean pause;

    private void saveFile() throws IOException, JSONException {
        if (writer == null) {
            writer = new DataWriter(outPutPath, fileSizeLength);
        }
        JSONObject data = new JSONObject();
        data.put("fileID", fileID);
        data.put("length", writer.getFileLength());
        socket.emit("request_file", data, new Ack() {
            @Override
            public void call(Object... os) {
                try {
                    if (os.length > 0) {
                        byte[] b = (byte[]) os[0];
                        writer.writeFile(b);
                        item.showStatus((int) writer.getPercentage());
                        table.repaint();
                        if (!item.isPause()) {
                            saveFile();
                        } else {
                            pause = true;
                        }
                    } else {
                        item.showStatus((int) writer.getPercentage());
                        item.done();
                        table.repaint();
                        writer.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Object[] toTableRow(int row) {
        return new Object[]{this, row, fileName, fileSize, "Next Update"};
    }
}
