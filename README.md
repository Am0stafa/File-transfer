# File Transfer System

## Overview
This project is a Java-based file transfer system that enables seamless file sharing between multiple clients through a centralized server. It provides a user-friendly interface for clients to upload and download files, while the server efficiently manages the file storage and transfer processes. The system is designed to handle concurrent client connections and ensures reliable and secure file transfers.

## Architecture
The file transfer system follows a client-server architecture, where multiple clients can connect to a central server to upload and download files. The architecture is composed of the following key components:


## Key Features

- **Multi-client Support**: The system supports multiple clients connecting to the server simultaneously, allowing concurrent file uploads and downloads.
### How is that made possible?
   - The server application uses the Socket.IO library to handle multiple client connections concurrently.

   - When a client connects to the server, a new [SocketIOClient](file:///Users/abdomostafa/peer-to-peer-file-sharing/README.md#64%2C63-64%2C63) object is created to represent that client.

   - The server maintains a list of connected clients using [DataClient](file:///Users/abdomostafa/peer-to-peer-file-sharing/README.md#53%2C54-53%2C54) objects, which store the [SocketIOClient](file:///Users/abdomostafa/peer-to-peer-file-sharing/README.md#64%2C63-64%2C63) object, client name, and a map of [DataWriter](file:///Users/abdomostafa/peer-to-peer-file-sharing/README.md#64%2C114-64%2C114) objects for handling file downloads.


   - The server listens for various events emitted by the clients, such as file upload requests, file download requests, and disconnections, and handles them accordingly.


   - This allows multiple clients to connect to the server simultaneously and perform file transfers independently.

- **Real-time Progress Tracking**: Both the client and server GUIs provide real-time updates on the progress of file transfers, keeping users informed about the status of their uploads and downloads.
### How is that made possible?

   - The system utilizes the Socket.IO library's event-driven communication to provide real-time updates on the progress of file transfers.

   - During a file upload, the client reads the file in chunks using [RandomAccessFile](file:///Users/abdomostafa/peer-to-peer-file-sharing/README.md#82%2C83-82%2C83) and sends each chunk to the server via Socket.IO events.


   - The server receives these chunks and writes them to disk using [RandomAccessFile](file:///Users/abdomostafa/peer-to-peer-file-sharing/README.md#82%2C83-82%2C83). After each chunk is written, the server emits a progress event back to the client, indicating the current progress of the file upload.


   - The server receives these chunks and writes them to disk using [RandomAccessFile](file:///Users/abdomostafa/peer-to-peer-file-sharing/README.md#82%2C83-82%2C83). After each chunk is written, the server emits a progress event back to the client, indicating the current progress of the file upload.

   - Similarly, during a file download, the client requests file chunks from the server, and the server reads the requested chunk from the file and sends it back to the client. The client writes the received chunks to disk and emits a progress event to update the download progress.

   - The client and server GUIs listen for these progress events and update the respective progress bars or status indicators in real-time, providing visual feedback to the users.

- **Pause and Resume**: The system allows users to pause and resume file transfers, providing flexibility and control over the transfer process.

### How is that made possible?
   - The system allows users to pause and resume file transfers by implementing control mechanisms on both the client and server sides.

   - When a user clicks the "Pause" button during a file transfer, the client sends a pause event to the server via Socket.IO.


   - The server receives the pause event and stops sending or receiving file chunks for that particular transfer. It maintains the current state of the transfer, including the file position and remaining data.

   - When the user clicks the "Resume" button, the client sends a resume event to the server, indicating that the transfer should be resumed from where it was paused.

   - The server receives the resume event and continues sending or receiving file chunks from the last known position, allowing the transfer to pick up where it left off.


   - The client and server GUIs update the transfer status accordingly, reflecting the paused or resumed state of the transfer.

- **Cross-platform Compatibility**: The application is built using Java, making it platform-independent and compatible with various operating systems such as Windows and macOS.
### How is that made possible?

   - The File Transfer System is built using Java, which is a platform-independent programming language.

   - Java's "write once, run anywhere" principle allows the application to be compiled into bytecode that can be executed on any platform that supports a Java Virtual Machine (JVM).

   - This means that the same codebase can be run on various operating systems, such as Windows, macOS, and Linux, without requiring any modifications.

   - The use of Java Swing components for the graphical user interface ensures consistent appearance and behavior across different platforms.

   - The Socket.IO library, which is used for communication between the client and server, is also cross-platform compatible and can be used seamlessly on different operating systems.
- **Secure and Reliable**: The system ensures secure and reliable file transfers by utilizing robust error handling and data integrity checks.
### How is that made possible?

   - The File Transfer System incorporates robust error handling and data integrity checks to ensure secure and reliable file transfers.

   - During file uploads and downloads, the system uses [RandomAccessFile](file:///Users/abdomostafa/peer-to-peer-file-sharing/README.md#82%2C83-82%2C83) to read and write file chunks, which provides low-level access to the file and ensures data integrity.

   - The server validates the received file chunks and checks for any data corruption or inconsistencies before writing them to disk.


   - In case of network disruptions or connection failures, the system implements appropriate error handling mechanisms. It can detect and handle socket exceptions, timeouts, and other network-related issues gracefully.

   - The system can also implement additional security measures, such as encrypting the file data during transmission using secure protocols like SSL/TLS, to protect sensitive information from unauthorized access.

   - Proper exception handling and logging mechanisms are used throughout the codebase to capture and handle any errors or exceptions that may occur during file transfers, ensuring the system remains stable and reliable.
   
## Components
- **Client Application**: Manages file uploads/downloads and user interactions.
- **Server Application**: Handles incoming connections, file storage, and transfer management.
- **Data Classes**: Handle file metadata and transfer data.
- **Swing Components**: Provide GUI elements for displaying and controlling file transfers.

### Client
![2021-06-21_202358](https://user-images.githubusercontent.com/58245926/122769433-c3806680-d2ce-11eb-99fa-1368a281c36b.png)
![2021-06-21_202431](https://user-images.githubusercontent.com/58245926/122769452-c7ac8400-d2ce-11eb-9461-53b9f1658734.png)


- The client application provides a graphical user interface (GUI) built using Java Swing components.

- It allows users to connect to the server, view available files, upload new files, and download files from the server.

- The client communicates with the server using the Socket.IO library, enabling real-time, event-driven communication.


- It utilizes data classes such as `DataFileServer` and `DataReader` to handle file metadata and file upload/download operations.

### Server
![2021-06-21_202412](https://user-images.githubusercontent.com/58245926/122769480-cc713800-d2ce-11eb-9ba5-d0a1bec27e84.png)


- The server application is responsible for managing client connections, file storage, and file transfer processes.

- It listens for incoming client connections and handles various events such as file uploads, file requests, and client disconnections.

- The server maintains a list of connected clients (`DataClient` objects) and keeps track of the files available for download (`DataFileServer` objects).

- It uses a `JTable` to display the list of connected clients and their status.


### Data Classes

- The system utilizes several data classes to handle file metadata and transfer-related data:

  - `DataFileServer`: Represents a file on the server, storing information such as file ID, name, size, and output path.

  - `DataClient`: Represents a connected client, storing the `SocketIOClient` object, client name, and a map of `DataWriter` objects for handling file downloads.

  - `DataReader`: Handles reading a file in chunks and sending it to the server during file uploads.

  - `DataWriter`: Handles writing file chunks received from the server to disk during file downloads.


### Event-Driven Communication

- The communication between the client and server is facilitated by the Socket.IO library, which enables real-time, event-driven communication.

- Various events, such as "set_user", "request", "send_file", and "request_file", are used to trigger specific actions on the server or client.

- Acknowledgements (`Ack` objects) are used to send responses back to the client for certain events.


### File Transfer Process

- File uploads are initiated by the client, which reads the file in chunks using `RandomAccessFile` and sends the chunks to the server via Socket.IO events.


- The server receives the file chunks and writes them to disk using `RandomAccessFile`.


- File downloads are initiated by the client, requesting file chunks from the server. The server reads the requested chunk from the file and sends it back to the client.


- The client receives the file chunks and writes them to disk using `RandomAccessFile`.


- The progress of file transfers is tracked and displayed in real-time on both the client and server GUIs.

## Installation
1. **Prerequisites**: Java JDK 8 or higher.
2. **Setup**: Clone the repository and import the project into an IDE like IntelliJ IDEA or Eclipse.
3. **Libraries**: Ensure all dependencies in the `lib` folder are added to the project's build path.

## Usage
- **Starting the Server**: Run `Server.java`. Use the GUI to monitor connections and transfers.
- **Running a Client**: Run `Main_Client.java`. Connect to the server, choose files to upload or download.

## Conclusion

The File Transfer System provides a reliable and efficient solution for sharing files between multiple clients through a centralized server. With its user-friendly interface, real-time progress tracking, and support for concurrent client connections, the system offers a seamless file transfer experience. The event-driven architecture, powered by Socket.IO, ensures responsive and real-time communication between clients and the server.
<br>
Feel free to explore the codebase and customize the application according to your specific requirements. Contributions and feedback are welcome to further enhance the functionality and performance of the File Transfer System.