import { Injectable } from "@angular/core";
import { Observable, ReplaySubject, Subject } from "rxjs";
import { Stomp, Client } from '@stomp/stompjs';
import { HttpClient } from "@angular/common/http";
import SockJS from 'sockjs-client';
import { environment } from "../../environment";

interface Message {
    sender: string;
    content: string;
    timestamp?: string;
}

@Injectable({
    providedIn: 'root'
})
export class ChatService {
    private stompClient: Client | null = null;
    private messageSubject: Subject<Message> = new Subject<Message>();
    private connectedSubject: ReplaySubject<boolean> = new ReplaySubject<boolean>(1); // To track WebSocket connection status

    // Base URL for your Spring Boot backend.
    private readonly API_BASE_URL = environment.BASE_URL;

    constructor(private http: HttpClient) {
        // Initialize connection status to false.
        this.connectedSubject.next(false);
    }

    /**
     * Establishes a WebSocket connection using SockJS and STOMP,
     * and subscribes to the public chat topic.
     * @param username The username of the client connecting.
     * @returns An Observable of incoming messages.
     */
    connect(username: string): Observable<Message> {
        // Prevent re-connection if already connected.
        if (this.stompClient && this.stompClient.connected) {
            console.log('Already connected to WebSocket.');
            return this.messageSubject.asObservable();
        }

        console.log('Attempting to connect to WebSocket...');
        // Create a SockJS instance pointing to the backend's WebSocket endpoint.
        const socket = new SockJS(`${this.API_BASE_URL}/ws`);
        // Create a STOMP client over the SockJS socket.
        this.stompClient = Stomp.over(socket);

        // Set auto-reconnect delay for robustness.
        this.stompClient.reconnectDelay = 5000; // 5 seconds

        // Define connection callbacks.
        this.stompClient.onConnect = (frame) => {
            console.log('Connected to WebSocket!');
            this.connectedSubject.next(true); // Update connection status

            // Subscribe to the public chat topic ("/topic/public") to receive all chat messages.
            this.stompClient?.subscribe('/topic/public', (message) => {
                const receivedMessage: Message = JSON.parse(message.body); // Parse the message body (JSON string)
                this.messageSubject.next(receivedMessage); // Emit the parsed message to subscribers
            });

            // Send a "user joined" message to the server's application destination.
            this.stompClient?.publish({ destination: '/app/chat.addUser', body: JSON.stringify({ sender: username, content: 'joined!' }) });
        };

        // Define error handler for STOMP client.
        this.stompClient.onStompError = (frame) => {
            console.error('STOMP error:', frame);
            this.connectedSubject.next(false); // Update connection status
        };

        // Define close handler.
        this.stompClient.onWebSocketClose = (event) => {
            console.log('WebSocket connection closed:', event);
            this.connectedSubject.next(false);
        };

        // Activate the STOMP client to initiate the connection.
        this.stompClient.activate();

        // Return an Observable that components can subscribe to for incoming messages.
        return this.messageSubject.asObservable();
    }

    /**
     * Disconnects the WebSocket connection.
     */
    disconnect(): void {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.deactivate().then(() => {
                console.log('Disconnected from WebSocket.');
                this.connectedSubject.next(false);
            }).catch(error => {
                console.error('Error during WebSocket deactivation:', error);
            });
        };
    }

    /**
     * Sends a chat message via WebSocket to the backend.
     * @param message The message object to send (sender, content).
     */
    sendMessage(message: Message): void {
        if (this.stompClient && this.stompClient.connected) {
            // Send the message to the backend's application destination for sending messages.
            this.stompClient.publish({ destination: '/app/chat.sendMessage', body: JSON.stringify(message) });
        } else {
            console.error('STOMP client not connected. Message not sent.');
        }
    }

    /**
     * Fetches historical chat messages via a REST API call to the backend.
     * @returns An Observable of an array of messages.
     */
    getMessages(): Observable<Message[]> {
        return this.http.get<Message[]>(`${this.API_BASE_URL}/api/messages`);
    }

    /**
     * Returns an Observable that emits true when connected to WebSocket, false otherwise.
     */
    isConnected(): Observable<boolean> {
        return this.connectedSubject.asObservable();
    }
}