# AI Chatbot Codebase Index

## Project Structure

```
ai-chatbot/
├── src/main/java/io/github/prathamdby/
│   ├── ChatbotMainWindow.java       # Main application window and entry point
│   ├── chat/
│   │   └── ChatManager.java         # Handles chat logic and API integration
│   ├── db/
│   │   ├── ChatMessageDAO.java      # Data access for chat messages
│   │   └── DatabaseManager.java     # Database connection and initialization
│   ├── theme/
│   │   └── ChatbotColors.java       # UI theme color definitions
│   └── ui/
│       ├── HeaderPanel.java         # Top panel UI component
│       ├── InputPanel.java          # Message input UI component
│       ├── SidebarPanel.java        # Side panel for chat history
│       └── util/
│           └── CustomScrollBarUI.java # Custom scrollbar theming
```

## Core Components

### Main Application
- `ChatbotMainWindow`: Main Swing application window that orchestrates all components
  - Uses multi-threading for responsive UI
  - Manages chat history loading and display
  - Handles user input and AI responses

### Chat Management
- `ChatManager`: Core chat functionality implementation
  - Integrates with local AI server (port 1234)
  - Manages message history
  - Handles async message processing
  - Uses OkHttp for API communication
  - Implements custom message formatting

### Database Layer
- `DatabaseManager`: Database connection and pool management
  - Uses HikariCP for connection pooling
  - Configures MySQL connection
  - Handles table creation and maintenance
- `ChatMessageDAO`: Data access operations for chat messages
  - Manages message persistence
  - Provides async data operations

### User Interface
- Custom dark theme implementation
- Responsive UI with asynchronous updates
- Component-based architecture with:
  - Header panel for controls
  - Input panel for message entry
  - Sidebar for chat history
  - Custom-styled scrollbars

## Technical Specifications

### Database Schema
```sql
CREATE TABLE chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
)
```

### API Integration
- Local AI server endpoint: http://localhost:1234/v1/chat/completions
- Model: llama-3.2-3b-instruct
- JSON-based communication
- Configurable parameters for AI response generation

### Dependencies
- Java Swing for UI
- HikariCP for database connection pooling
- OkHttp for HTTP client
- Gson for JSON processing
- MySQL for data persistence

## Performance Features
- Connection pooling with optimized settings
- Asynchronous operations for UI responsiveness
- Multi-threaded message processing
- Efficient chat history management
