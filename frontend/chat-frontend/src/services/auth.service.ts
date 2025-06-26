import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})

export class AuthService {
  // Base URL for your Spring Boot backend's authentication API.
  // Ensure this matches your backend's running address and port.
  private readonly API_BASE_URL = 'http://localhost:8080';

  // HttpClient is injected into the service's constructor, allowing it to make HTTP requests.
  constructor(private http: HttpClient) { }

  /**
   * Sends a POST request to the backend to register a new user.
   * @param username The desired username for the new user.
   * @param password The password for the new user.
   * @returns An Observable that emits the HTTP response from the backend.
   */
  register(username: string, password: string): Observable<any> {
    // Sends a POST request to /api/auth/register with username and password in the body.
    return this.http.post(`${this.API_BASE_URL}/api/auth/register`, { username, password });
  }

  /**
   * Sends a POST request to the backend to log in an existing user.
   * @param username The username of the user attempting to log in.
   * @param password The password of the user attempting to log in.
   * @returns An Observable that emits the HTTP response from the backend (expected to contain username on success).
   */
  login(username: string, password: string): Observable<any> {
    // Sends a POST request to /api/auth/login with username and password in the body.
    return this.http.post(`${this.API_BASE_URL}/api/auth/login`, { username, password });
  }
}
