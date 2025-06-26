import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { lastValueFrom } from 'rxjs';
import { environment } from '../../environment';
interface RuntimeConfig {
  BASE_URL: string;
}

@Injectable({ providedIn: 'root' })
export class ConfigService {
  private config: RuntimeConfig | null = null;

  constructor(private http: HttpClient) { }

  /**
   * Loads runtime configuration from config.json.
   * This method is called during application initialization.
   */
  loadConfig(): Promise<any> {
    // Attempt to fetch config.json from the 'assets' folder of the deployed app.
    // In a Dockerized Nginx setup, entrypoint.sh places config.json here.
    return this.http.get<RuntimeConfig>('/assets/config.json')
      .toPromise() // Convert the Observable to a Promise (suitable for APP_INITIALIZER)
      .then((config: any) => {
        this.config = config; // Store the loaded configuration
        // Merge runtime config properties into Angular's static environment object.
        // This makes BASE_URL available directly via `environment.BASE_URL`
        // or through this service's `getApiBaseUrl()` method.
        Object.assign(environment, this.config);
        console.log('Runtime config loaded:', environment);
      })
      .catch((error: any) => {
        console.error('Error loading configuration:', error);
        // Fallback: If config.json fails to load (e.g., during local 'ng serve' where it might not exist),
        // default to localhost. This is crucial for local development without Docker Compose.
        environment.BASE_URL = environment.BASE_URL || 'http://localhost:8080';
        console.warn('Falling back to default API_BASE_URL:', environment.BASE_URL);
      });
    }

    /**
   * Provides the loaded base URL for other services to use.
   * This method should be called by AuthService and ChatService to get the backend URL.
   */
  getApiBaseUrl(): string {
    // Return the loaded BASE_URL, or fall back to the environment.BASE_URL (for local dev)
    // or a hardcoded default if all else fails.
    return this.config?.BASE_URL || environment.BASE_URL || 'http://localhost:8080';
  }
}
