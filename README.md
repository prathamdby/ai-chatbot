# AI Chatbot

A Java desktop application featuring a modern dark-themed UI for interacting with AI language models. Built with async message handling and persistent storage.

## Quick Start

### Prerequisites
- JDK 8+
- MySQL 8.0+
- Maven 3.6+
- Local AI server on port 1234

### Setup & Run
1. Configure MySQL:
```sql
CREATE DATABASE chatbot;
CREATE USER 'root'@'localhost' IDENTIFIED BY 'root';
GRANT ALL PRIVILEGES ON chatbot.* TO 'root'@'localhost';
```

2. Clone and build:
```bash
git clone https://github.com/prathamdby/ai-chatbot.git
cd ai-chatbot
mvn clean package
java -jar target/ai-chatbot-1.0-SNAPSHOT.jar
```

## Features
- Dark-themed UI with custom components
- Async message processing
- MySQL-based chat history
- Multi-threaded architecture
- HikariCP connection pooling
- Local AI model integration

## Technical Stack
- Java Swing (UI)
- MySQL (Storage)
- OkHttp (API)
- Gson (JSON)
- HikariCP (DB Pool)

## Contributing
Open to contributions! Please fork, create a feature branch, and submit a PR.

## License
MIT License - See `LICENSE` file

## Links
- Author: [@prathamdby](https://github.com/prathamdby)
- Project: [GitHub](https://github.com/prathamdby/ai-chatbot)

See `CODEBASE_INDEX.md` for detailed technical documentation.
