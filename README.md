# File Transfer System

## Overview
This project is a Java-based file transfer system designed to facilitate seamless upload and download of files via a centralized server. It allows multiple clients to connect to a server and manage file transfers, supported by a user-friendly Swing UI and robust concurrency handling.

## Key Features
- **Multi-client Support**: Multiple users can connect to the server simultaneously to upload or download files.
- **Real-time Progress Tracking**: Both the client and server GUIs provide real-time updates on file transfer progress.
- **Pause and Resume**: File transfers can be paused and resumed, adding flexibility to the user experience.
- **Platform Independent**: Works across different platforms like Windows and macOS.
- **Event-driven Architecture**: Utilizes Socket.IO for responsive, real-time communication.

## Components
- **Client Application**: Manages file uploads/downloads and user interactions.
- **Server Application**: Handles incoming connections, file storage, and transfer management.
- **Data Classes**: Handle file metadata and transfer data.
- **Swing Components**: Provide GUI elements for displaying and controlling file transfers.

### Client
![2021-06-21_202358](https://user-images.githubusercontent.com/58245926/122769433-c3806680-d2ce-11eb-99fa-1368a281c36b.png)
![2021-06-21_202431](https://user-images.githubusercontent.com/58245926/122769452-c7ac8400-d2ce-11eb-9461-53b9f1658734.png)
### Server
![2021-06-21_202412](https://user-images.githubusercontent.com/58245926/122769480-cc713800-d2ce-11eb-9ba5-d0a1bec27e84.png)



## Installation
1. **Prerequisites**: Java JDK 8 or higher.
2. **Setup**: Clone the repository and import the project into an IDE like IntelliJ IDEA or Eclipse.
3. **Libraries**: Ensure all dependencies in the `lib` folder are added to the project's build path.

## Usage
- **Starting the Server**: Run `Server.java`. Use the GUI to monitor connections and transfers.
- **Running a Client**: Run `Main_Client.java`. Connect to the server, choose files to upload or download.

## Data Flows

### User Uploads a File to the Server
1. **File Selection**: User selects a file for upload via the client's GUI.
2. **File Reading**: `DataReader` reads the file in chunks.
3. **Data Transfer**: The chunks are sent to the server using Socket.IO.
4. **Writing on Server**: Server's `DataWriter` writes chunks to the server's filesystem.
5. **Progress Update**: Both client and server GUIs update the upload progress.

### User Connects and Downloads a File
1. **Requesting Files**: Client requests a list of available files after connecting.
2. **File Selection**: User selects a file to download.
3. **Server Sends File**: Server sends the file in chunks to the client.
4. **Writing on Client**: Client's `DataWriter` writes these chunks to the filesystem.
5. **Progress Update**: Download progress is shown on the client's GUI.

### Pause/Resume Transfer
1. **User Action**: User pauses/resumes a transfer via the GUI.
2. **State Management**: The application stops/continues reading and sending file data.
3. **Progress Update**: The GUI updates to reflect the changed transfer state.

## Additional Information
- **Error Handling**: The system handles network errors and file I/O issues, logging exceptions and ceasing operations if necessary.
- **Security Note**: This application does not encrypt data transfers. Use it in trusted networks.

## Contributing
Contributions are welcome. Please fork the repository and submit a pull request with your changes.

## TODO:
Implement it to be a decentralized system where clients can download from each other and the server tracks the distribution of chunks across clients and coordinates the download of chunks from different clients simultaneously to assemble the file. Similar to BitTorrent.

### Step 1: File Upload and Metadata Storage
- **Server Side**:
  - Implement MD5 hashing of the uploaded file.
  - Create a CSV file structure with columns for `file_id`, `file_name`, `file_size`, `md5_hash`, `server_path`, and `client_distribution`.
  - Store file metadata in the CSV upon upload.
  - Save the file on the server and split it into chunks.

### Step 2: Initial Download and IP Tracking
- **Server Side**:
  - On download request, serve the file from the server to the first client.
  - Update the CSV, appending the client's IP to the `client_distribution` column, indicating that this client now has 100% of the file.

### Step 3: Subsequent Downloads and Chunk Distribution
- **Server Side**:
  - Determine which clients have parts of the requested file based on the CSV.
  - Direct new clients to download different chunks from different existing clients.
  - Update the CSV to reflect the new distribution after each download.

### Step 4: Server as Assembler and Validator
- **Server Side**:
  - After all chunks are downloaded, reassemble the file.
  - Verify the file's integrity using MD5 hash.
  - If the hash is correct, send the file to the requesting client.
  - If not, send the server's original copy to ensure integrity.

### Download Progress Bar and Status
- **Client Side**:
  - Modify the download progress bar to reflect the multi-source download process.
  - Add a status message indicating the server's role in downloading and assembling chunks.

### Implementation Steps
#### Server Implementation
1. Modify the `DataFileServer` class to handle MD5 hashing upon file upload.
2. Update the server's upload handler to split files into chunks and store them.
3. Implement functions to update the CSV with client IP and file chunk information.
4. Create a function to orchestrate chunk downloads from multiple clients.
5. Write a method to reassemble and validate the file using MD5 before sending it to the client.

#### Client Implementation
1. Update the client application to support downloading file chunks from multiple sources (other clients).
2. Implement a function to upload file chunks to other clients upon request.
3. Modify the client's download functionality to show a combined progress bar for chunks from different sources.
4. Add UI elements to display the server's assembly and validation process.

#### CSV File Handling
1. Create a utility function to read and update the CSV file safely, handling serialization and deserialization of the `client_distribution` column.
2. Implement logic to fairly distribute download requests across clients based on the chunk distribution.

#### Testing and Validation
1. Test the file upload process to ensure that the MD5 hash is correctly generated and stored.
2. Validate the download process, ensuring that clients can download chunks from the server and other clients.
3. Verify that the server correctly assembles and validates the file.
4. Test the entire system with multiple clients to ensure proper operation and accurate tracking of file chunks.

By following these steps, you will transform the existing system into a decentralized model that distributes file chunks across clients and leverages the server for coordination, validation, and fallback. This approach reduces the server's bandwidth and storage requirements and can improve download speeds by parallelizing the download process.


