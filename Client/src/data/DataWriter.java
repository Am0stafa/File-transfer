/*
1. File and Meta-data Management:
   - It keeps track of a `File` object (`file`), the `fileSize`, and a `RandomAccessFile` object (`accFile`). There are getter and setter methods provided for each of these fields to allow external access and modification.

2. Constructor Initialization:
   - The constructor initializes the `DataWriter` instance by taking a `File` object and a `long` representing the file size as arguments. It creates a `RandomAccessFile` object with read-write ("rw") mode for the specified file.

3. File Writing:
   - The `writeFile` method writes a byte array (`data`) to the file at its current end (it seeks to the end of the file before writing). It returns the new length of the file after writing. This method is synchronized to ensure thread-safety during file write operations.

4. File Closure:
   - The `close` method closes the `RandomAccessFile` object, stopping any further read/write operations on the file.

5. File Information Retrieval:
   - Methods like `getMaxFileSize`, `getCurrentFileSize`, `getPercentage`, and `getFileLength` provide information about the file, such as its maximum size, current size, the percentage of the maximum size that has been written, and the current file length in bytes.

6. File Size Formatting:
   - The `convertFile` method converts a given number of bytes to a human-readable string representing the file size in the most appropriate units (e.g., KB, MB, GB, etc.), depending on the size.
*/

package data;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.text.DecimalFormat;

public class DataWriter {

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

    public RandomAccessFile getAccFile() {
        return accFile;
    }

    public void setAccFile(RandomAccessFile accFile) {
        this.accFile = accFile;
    }

    public DataWriter(File file, long fileSize) throws IOException {
        //  rw is mode read and write
        accFile = new RandomAccessFile(file, "rw");
        this.file = file;
        this.fileSize = fileSize;

    }

    private File file;
    private long fileSize;
    private RandomAccessFile accFile;

    public synchronized long writeFile(byte[] data) throws IOException {
        accFile.seek(accFile.length());
        accFile.write(data);
        return accFile.length();
    }

    public void close() throws IOException {
        accFile.close();
    }

    public String getMaxFileSize() {
        return convertFile(fileSize);
    }

    public String getCurrentFileSize() throws IOException {
        return convertFile(accFile.length());
    }

    public double getPercentage() throws IOException {
        double percentage;
        long filePointer = accFile.length();
        percentage = filePointer * 100 / fileSize;
        return percentage;
    }

    public long getFileLength() throws IOException {
        return accFile.length();
    }

    private String convertFile(double bytes) {
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
}
