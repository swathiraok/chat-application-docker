import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { ChatService } from '../services/chat.service';
import { AuthService } from '../services/auth.service';

interface Message {
  sender: string;
  content: string;
  timestamp?: string;
}
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})

export class AppComponent implements OnInit, OnDestroy{
  @ViewChild('messageContainer') private messageContainer!: ElementRef; // For auto-scrolling

  title = 'Real-Time Chat App';
  isLoggedIn: boolean = false;
  username: string = '';
  password: string = '';
  loggedInUsername: string | null = null; // Stores the username after successful login

  messages: Message[] = []; // Array to store all chat messages
  newMessageContent: string = ''; // ngModel for new message input

  onlineUsers: Set<string> = new Set<string>();
  private messageSubscription: Subscription | undefined; // To hold WebSocket subscription

  constructor(
    private chatService: ChatService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Check if a username is already stored (e.g., from a previous session, though simplified here)
    const storedUsername = localStorage.getItem('chatUsername');
    if (storedUsername) {
      this.loggedInUsername = storedUsername;
      this.isLoggedIn = true;
      this.initializeChat();
    }
  }

  // Lifecycle hook for cleanup
  ngOnDestroy(): void {
    if (this.messageSubscription) {
      this.messageSubscription.unsubscribe(); // Unsubscribe to prevent memory leaks
    }
    this.chatService.disconnect(); // Disconnect WebSocket on component destruction
  }

  /**
   * Handles user registration.
   */
  onRegister(): void {
    this.authService.register(this.username, this.password).subscribe({
      next: (response) => {
        alert('Registration successful! Please login.');
        // Clear fields after successful registration
        this.username = '';
        this.password = '';
      },
      error: (error) => {
        alert('Registration failed: ' + (error.error || 'Unknown error'));
      }
    });
  }

  /**
   * Handles user login.
   */
  onLogin(): void {
    this.authService.login(this.username, this.password).subscribe({
      next: (response) => {
        this.loggedInUsername = this.username;
        this.isLoggedIn = true;
        localStorage.setItem('chatUsername', this.loggedInUsername); // Store username
        alert('Login successful!');
        this.initializeChat(); // Initialize chat after successful login
      },
      error: (error) => {
        alert('Login failed: ' + (error.error || 'Invalid credentials'));
      }
    });
  }

  /**
   * Initializes the chat by connecting to WebSocket and fetching message history.
   */
  initializeChat(): void {
    if (!this.loggedInUsername) {
      console.error('Username not set for chat initialization.');
      return;
    }

    // Fetch message history first
    this.chatService.getMessages().subscribe({
      next: (data) => {
        this.messages = data;
        this.scrollToBottom(); // Scroll to bottom after loading history
      },
      error: (error) => {
        console.error('Failed to load message history:', error);
      }
    });

    // Connect to WebSocket and subscribe to public topic for both messages and presence
    this.messageSubscription = this.chatService.connect(this.loggedInUsername).subscribe({
      next: (message: Message) => {
        console.log('Received WebSocket message:', message);
        // New logic: Check for join/leave messages to update online users
        if (message.content === 'joined!' && message.sender !== this.loggedInUsername) {
          this.onlineUsers.add(message.sender); // Add user to set
          console.log(`User ${message.sender} joined. Online users:`, Array.from(this.onlineUsers));
        } else if (message.content === 'left!' && message.sender !== this.loggedInUsername) {
          this.onlineUsers.delete(message.sender); // Remove user from set
          console.log(`User ${message.sender} left. Online users:`, Array.from(this.onlineUsers));
        } else if (message.content === 'onlineUsers:' && message.sender === 'System') {
          // This handles initial online user list broadcast from backend if we implement it.
          // For now, it's a placeholder, as the backend listener broadcasts 'joined!' on connect.
          // We could make backend send full list to new joiners, but for simplicity,
          // the 'joined!' broadcasts work well for dynamic updates.
          const usersArray = JSON.parse(message.content.substring('onlineUsers:'.length));
          this.onlineUsers = new Set(usersArray);
          console.log('Initial online users received:', Array.from(this.onlineUsers));
        }

        this.messages.push(message); // Always add message to the chat display
        this.scrollToBottom();
      },
      error: (error) => {
        console.error('WebSocket error:', error);
      }
    });

    // Add the current user to the online list immediately after connecting successfully.
    // This handles their own presence in their own list.
    this.onlineUsers.add(this.loggedInUsername);
  }


  /**
   * Sends a new chat message.
   */
  sendMessage(): void {
    if (this.newMessageContent.trim() && this.loggedInUsername) {
      const chatMessage: Message = {
        sender: this.loggedInUsername,
        content: this.newMessageContent.trim()
      };
      this.chatService.sendMessage(chatMessage);
      this.newMessageContent = ''; // Clear input field
    }
  }

  /**
   * Logs out the current user.
   */
  onLogout(): void {
    this.isLoggedIn = false;
    this.loggedInUsername = null;
    this.username = '';
    this.password = '';
    this.messages = []; // Clear messages on logout
    this.onlineUsers.clear();
    localStorage.removeItem('chatUsername'); // Remove stored username
    this.chatService.disconnect(); // Disconnect WebSocket
    alert('Logged out successfully.');
  }

  /**
   * Scrolls the message container to the bottom.
   */
  private scrollToBottom(): void {
    try {
      setTimeout(() => { // Use setTimeout to ensure DOM is updated
        this.messageContainer.nativeElement.scrollTop = this.messageContainer.nativeElement.scrollHeight;
      }, 0);
    } catch (err) {
      console.error('Could not scroll to bottom:', err);
    }
  }
}