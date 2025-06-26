import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { lastValueFrom } from 'rxjs';
import { environment } from '../../environment';

@Injectable({ providedIn: 'root' })
export class ConfigService {
   constructor(private http: HttpClient) { }

  /**
   * Loads runtime configuration from config.json.
   * This method is called during application initialization.
   */
  async load(): Promise<any> {
    try {
      // Fetch the config.json file from the root of the Nginx server.
      const config = await lastValueFrom(this.http.get('/config.json'));
      // Merge the fetched runtime configuration into Angular's environment object.
      // This makes runtime variables accessible via environment.BASE_URL etc.
      Object.assign(environment, config);
      console.log('Runtime configuration loaded:', environment);
    } catch (error) {
      console.error('Failed to load runtime configuration:', error);
      // Depending on your application's needs, you might want to throw the error
      // or use default values if config loading fails.
    }
  }
}
