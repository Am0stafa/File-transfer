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

