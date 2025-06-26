// File: chat-frontend/src/polyfills.ts
// This file includes polyfills needed by Angular and is loaded before the app.

/***************************************************************************************************
 * BROWSER POLYFILLS
 */

/**
 * Required to fix 'global is not defined' error when using libraries that expect Node.js globals
 * in a browser environment (e.g., some dependencies of SockJS or STOMP.js).
 * This maps the 'global' reference to the 'window' object in browsers.
 */
(window as any).global = window;

/***************************************************************************************************
 * Zone JS is required by default for Angular itself.
 */
import 'zone.js';  // Included with Angular CLI.


/***************************************************************************************************
 * APPLICATION IMPORTS
 */
// Add your own polyfills here. For example:
// import 'core-js/es/array/find';
// import 'core-js/es/array/includes';
// import 'core-js/es/string/includes';
// import 'core-js/es/map';
// import 'core-js/es/set';

