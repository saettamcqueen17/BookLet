import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

type TokenResponse = {
  access_token: string;
  refresh_token: string;
  expires_in: number;
  refresh_expires_in?: number;
  id_token?: string;
  token_type?: string;
  scope?: string;
};

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly storageKey = 'auth';
  private readonly verifierKey = 'pkce_verifier';
  private readonly redirectKey = 'post_login_redirect';

  isAuthenticatedSig = signal<boolean>(this.hasValidToken());

  constructor(private router: Router) {}

  private get keycloakBase() {
    return `${environment.keycloak.url}/realms/${environment.keycloak.realm}/protocol/openid-connect`;
  }

  private get callbackUrl() {
    // Use current origin to build callback route only in browser
    return this.isBrowser ? `${window.location.origin}/auth/callback` : '';
  }

  async login(redirectTo: string = '/') {
    if (!this.isBrowser) return;
    const verifier = this.generateCodeVerifier();
    const challenge = await this.generateCodeChallengeAsync(verifier);
    this.storageSet(this.verifierKey, verifier);
    this.storageSet(this.redirectKey, redirectTo);

    const authorizeUrl = new URL(`${this.keycloakBase}/auth`);
    authorizeUrl.searchParams.set('client_id', environment.keycloak.clientId);
    authorizeUrl.searchParams.set('redirect_uri', this.callbackUrl);
    authorizeUrl.searchParams.set('response_type', 'code');
    authorizeUrl.searchParams.set('scope', 'openid profile');
    authorizeUrl.searchParams.set('code_challenge', challenge);
    authorizeUrl.searchParams.set('code_challenge_method', 'S256');

    window.location.href = authorizeUrl.toString();
  }

  async handleAuthCallback(): Promise<boolean> {
    if (!this.isBrowser) return false;
    const params = new URLSearchParams(window.location.search);
    const code = params.get('code');
    if (!code) return false;

    const verifier = this.storageGet(this.verifierKey) || '';
    this.storageRemove(this.verifierKey);

    try {
      const body = new URLSearchParams();
      body.set('grant_type', 'authorization_code');
      body.set('client_id', environment.keycloak.clientId);
      body.set('code', code);
      body.set('redirect_uri', this.callbackUrl);
      body.set('code_verifier', verifier);

      const res = await fetch(`${this.keycloakBase}/token`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: body.toString()
      });

      if (!res.ok) throw new Error(`Token exchange failed: ${res.status}`);
      const tokens = (await res.json()) as TokenResponse;
      this.storeTokens(tokens);
      this.isAuthenticatedSig.set(true);

      const redirectTo = this.storageGet(this.redirectKey) || '/';
      this.storageRemove(this.redirectKey);
      await this.router.navigateByUrl(redirectTo);
      return true;
    } catch (err) {
      console.error('Auth callback error', err);
      return false;
    }
  }

  logout() {
    const { refresh_token } = this.getStoredTokens() || ({} as any);
    this.clearTokens();
    this.isAuthenticatedSig.set(false);
    if (this.isBrowser) {
      // Redirect to Keycloak end-session if available
      const endSession = new URL(`${this.keycloakBase}/logout`);
      endSession.searchParams.set('client_id', environment.keycloak.clientId);
      endSession.searchParams.set('post_logout_redirect_uri', window.location.origin + '/');
      if (refresh_token) {
        // Not strictly required for public clients, but allowed
        // Keycloak may ignore this param for public clients
        endSession.searchParams.set('refresh_token', refresh_token);
      }
      window.location.href = endSession.toString();
    }
  }

  getAuthorizationHeader = async (): Promise<string | null> => {
    const valid = await this.ensureValidToken();
    if (!valid) return null;
    const { access_token } = this.getStoredTokens()!;
    return `Bearer ${access_token}`;
  };

  getUser(): { sub: string; preferred_username?: string; name?: string; email?: string } | null {
    const tokens = this.getStoredTokens();
    if (!tokens) return null;
    const jwt = tokens.id_token || tokens.access_token;
    if (!jwt) return null;
    try {
      const payload = JSON.parse(atob(jwt.split('.')[1] || ''));
      return {
        sub: payload.sub,
        preferred_username: payload.preferred_username,
        name: payload.name,
        email: payload.email
      };
    } catch { return null; }
  }

  // Ensures token is valid; refreshes if near expiry
  private async ensureValidToken(): Promise<boolean> {
    const stored = this.getStoredTokens();
    if (!stored) return false;
    const expiresAt = Number(this.storageGet(this.storageKey + ':exp')) || 0;
    const now = Date.now();
    // Refresh if expiring in next 30 seconds
    if (now + 30_000 < expiresAt) return true;
    return await this.refreshToken();
  }

  private async refreshToken(): Promise<boolean> {
    const stored = this.getStoredTokens();
    if (!stored?.refresh_token) return false;
    try {
      const body = new URLSearchParams();
      body.set('grant_type', 'refresh_token');
      body.set('client_id', environment.keycloak.clientId);
      body.set('refresh_token', stored.refresh_token);

      const res = await fetch(`${this.keycloakBase}/token`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: body.toString()
      });
      if (!res.ok) throw new Error(`Refresh failed: ${res.status}`);
      const tokens = (await res.json()) as TokenResponse;
      this.storeTokens(tokens);
      this.isAuthenticatedSig.set(true);
      return true;
    } catch (e) {
      console.error('Token refresh error', e);
      this.clearTokens();
      this.isAuthenticatedSig.set(false);
      return false;
    }
  }

  private storeTokens(tokens: TokenResponse) {
    this.storageSet(this.storageKey, JSON.stringify(tokens));
    const expiresAt = Date.now() + (tokens.expires_in - 30) * 1000; // headroom
    this.storageSet(this.storageKey + ':exp', String(expiresAt));
  }

  private clearTokens() {
    this.storageRemove(this.storageKey);
    this.storageRemove(this.storageKey + ':exp');
  }

  private getStoredTokens(): TokenResponse | null {
    const raw = this.storageGet(this.storageKey);
    if (!raw) return null;
    try { return JSON.parse(raw) as TokenResponse; } catch { return null; }
  }

  private hasValidToken(): boolean {
    if (!this.isBrowser) return false;
    const exp = Number(this.storageGet(this.storageKey + ':exp')) || 0;
    return !!this.storageGet(this.storageKey) && Date.now() < exp;
  }

  private get isBrowser(): boolean {
    return typeof window !== 'undefined' && typeof sessionStorage !== 'undefined';
  }

  private storageGet(key: string): string | null {
    if (!this.isBrowser) return null;
    try { return sessionStorage.getItem(key); } catch { return null; }
  }

  private storageSet(key: string, value: string) {
    if (!this.isBrowser) return;
    try { sessionStorage.setItem(key, value); } catch { /* ignore */ }
  }

  private storageRemove(key: string) {
    if (!this.isBrowser) return;
    try { sessionStorage.removeItem(key); } catch { /* ignore */ }
  }

  // PKCE helpers
  private generateCodeVerifier(length: number = 96): string {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~';
    let random = '';
    const array = new Uint8Array(length);
    crypto.getRandomValues(array);
    for (let i = 0; i < array.length; i++) {
      random += chars[array[i] % chars.length];
    }
    return random;
  }

  // Intentionally no sync version; use async method below

  async generateCodeChallengeAsync(verifier: string): Promise<string> {
    const encoder = new TextEncoder();
    const data = encoder.encode(verifier);
    const digest = await crypto.subtle.digest('SHA-256', data);
    const bytes = new Uint8Array(digest);
    let str = '';
    for (let i = 0; i < bytes.byteLength; i++) {
      str += String.fromCharCode(bytes[i]);
    }
    const base64 = btoa(str)
      .replace(/\+/g, '-')
      .replace(/\//g, '_')
      .replace(/=+$/, '');
    return base64;
  }
}
