# AI Chatbot Application

A Java-based desktop chatbot application that provides an intuitive interface for interacting with AI language models. The application features a modern, dark-themed UI and persistent message storage.

## Installation

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- MySQL Server 8.0 or higher
- Maven 3.6 or higher
- Git (for development)

### Database Setup
1. Create a new MySQL database:
```sql
CREATE DATABASE chatbot;
CREATE USER 'chatbot'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON chatbot.* TO 'chatbot'@'localhost';
FLUSH PRIVILEGES;
```

2. Create the messages table:
```sql
USE chatbot;
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    timestamp DATETIME NOT NULL,
    is_user BOOLEAN NOT NULL
);
```

3. Create `src/main/resources/database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/chatbot
db.user=chatbot
db.password=your_password
db.poolSize=10
```

### Build and Run
1. Clone the repository:
```bash
git clone https://github.com/prathamdby/ai-chatbot.git
cd ai-chatbot
```

2. Build the project:
```bash
mvn clean package
```

3. Run the application:
```bash
java -jar target/ai-chatbot-1.0-SNAPSHOT.jar
```

Note: Ensure your local AI model server is running on port 1234 before starting the application.

## Key Features

- **Modern UI**: Clean, dark-themed interface with custom scrollbars and color schemes
- **Real-time Chat**: Asynchronous message handling for smooth user interaction
- **Message Persistence**: MySQL database integration for storing chat history
- **AI Integration**: Connects to a local AI model server for generating responses
- **Multi-threaded**: Efficient handling of UI updates, database operations, and API calls

## Technical Stack

- Java Swing for desktop UI
- HikariCP for database connection pooling
- MySQL for message storage
- OkHttp for API communication
- Gson for JSON processing

## System Requirements

- Java Runtime Environment
- MySQL Server
- Local AI model server running on port 1234

The application uses a client-server architecture where the desktop client communicates with a local AI server for generating responses while maintaining conversation history in a MySQL database.

## Development

### Setting up Development Environment
1. Fork the repository
2. Clone your fork
3. Open the project in your preferred IDE
4. Install required dependencies using Maven
5. Set up database configuration as described above

### Code Style
- Follow standard Java naming conventions
- Use 4 spaces for indentation
- Add comments for complex logic
- Write unit tests for new features

## Contributing
1. Fork the repository
2. Create a new branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Commit your changes (`git commit -m 'Add amazing feature'`)
5. Push to the branch (`git push origin feature/amazing-feature`)
6. Open a Pull Request

## License
Distributed under the MIT License. See `LICENSE` for more information.

## Contact
Pratham Dubey - [@prathamdby](https://github.com/prathamdby)

Project Link: [https://github.com/prathamdby/ai-chatbot](https://github.com/prathamdby/ai-chatbot)
