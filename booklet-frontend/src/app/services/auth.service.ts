// TypeScript
// file: 'booklet-frontend/src/app/services/auth.service.ts'
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import Keycloak, { KeycloakInstance, KeycloakInitOptions } from 'keycloak-js';

type UserInfo = { id?: string; username?: string; email?: string };

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly isBrowser: boolean;
  private kc?: KeycloakInstance;
  private initPromise?: Promise<boolean>;

  private readonly url = 'http://localhost:8085';
  private readonly realm = 'BookLet';
  private readonly clientId = 'booklet-frontend';

  private readonly TOK_KEY = 'kc_token';
  private readonly RTOK_KEY = 'kc_refreshToken';
  private readonly IDTOK_KEY = 'kc_idToken';

  private startupInit?: Promise<void>;


  constructor(@Inject(PLATFORM_ID) platformId: Object) {
    this.isBrowser = isPlatformBrowser(platformId);
    if (this.isBrowser) {
      this.kc = new (Keycloak as any)({
        url: this.url,
        realm: this.realm,
        clientId: this.clientId
      }) as KeycloakInstance;

      // Aggiorna storage su eventi di auth
      this.kc.onAuthSuccess = () => this.saveTokens();
      this.kc.onAuthRefreshSuccess = () => this.saveTokens();
      this.kc.onAuthLogout = () => this.clearTokens();
      this.kc.onTokenExpired = () => {
        this.kc?.updateToken(30).catch(() => this.login(window.location.pathname));
      };
    }
  }

  initSession(): Promise<void> {
    if (!this.isBrowser || !this.kc) return Promise.resolve();
    if (!this.startupInit) {
      this.startupInit = this.ensureInit()
        .then(async (authenticated) => {
          if (!authenticated) {
            await this.login(window.location.pathname);
          }
        })
        .then(() => void 0)
        .catch(() => void 0);
    }
    return this.startupInit;
  }

  isAuthenticatedSig = (): boolean => this.kc?.authenticated === true;

  private loadStoredTokens(): { token?: string; refreshToken?: string; idToken?: string } {
    if (!this.isBrowser) return {};
    return {
      token: sessionStorage.getItem(this.TOK_KEY) || undefined,
      refreshToken: sessionStorage.getItem(this.RTOK_KEY) || undefined,
      idToken: sessionStorage.getItem(this.IDTOK_KEY) || undefined
    };
  }

  private saveTokens(): void {
    if (!this.isBrowser || !this.kc) return;
    if (this.kc.token) sessionStorage.setItem(this.TOK_KEY, this.kc.token);
    if (this.kc.refreshToken) sessionStorage.setItem(this.RTOK_KEY, this.kc.refreshToken);
    if ((this.kc as any).idToken) sessionStorage.setItem(this.IDTOK_KEY, (this.kc as any).idToken);
  }

  private clearTokens(): void {
    if (!this.isBrowser) return;
    sessionStorage.removeItem(this.TOK_KEY);
    sessionStorage.removeItem(this.RTOK_KEY);
    sessionStorage.removeItem(this.IDTOK_KEY);
  }

  private ensureInit(): Promise<boolean> {
    if (!this.isBrowser || !this.kc) return Promise.resolve(false);
    if (!this.initPromise) {
      const stored = this.loadStoredTokens();
      const opts: KeycloakInitOptions = {
        onLoad: 'check-sso',
        pkceMethod: 'S256',
        checkLoginIframe: false,
        silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html',
        token: stored.token,
        refreshToken: stored.refreshToken,
        idToken: stored.idToken
      };
      this.initPromise = this.kc.init(opts)
        .then(authenticated => {
          if (authenticated) this.saveTokens();
          return authenticated;
        })
        .catch(() => false);
    }
    return this.initPromise;
  }

  async isLoggedIn(): Promise<boolean> {
    if (!this.isBrowser || !this.kc) return false;
    const ok = await this.ensureInit();
    return ok && !!this.kc.authenticated;
  }

  async getToken(): Promise<string | null> {
    if (!this.isBrowser || !this.kc) return null;
    const ok = await this.ensureInit();
    if (!ok) return null;
    try {
      await this.kc.updateToken(30);
      this.saveTokens();
    } catch {
      return null;
    }
    return this.kc.token ?? null;
  }

  login(redirectTo?: string): Promise<void> {
    if (!this.isBrowser || !this.kc) return Promise.resolve();
    const targetPath = redirectTo?.startsWith('/') ? redirectTo : window.location.pathname;
    const redirectUri = window.location.origin + targetPath;
    return this.kc.login({ redirectUri });
  }

  logout(): Promise<void> {
    if (!this.isBrowser || !this.kc) return Promise.resolve();
    this.clearTokens();
    return this.kc.logout({ redirectUri: window.location.origin + '/' });
  }

  async handleAuthCallback(): Promise<void> {
    await this.ensureInit();
  }

  getUser(): UserInfo {
    const tp: any = (this.kc as any)?.tokenParsed ?? {};
    return {
      id: tp?.sub,
      username: tp?.preferred_username,
      email: tp?.email
    };
  }

  getRoles(clientId = this.clientId): string[] {
    const tp: any = (this.kc as any)?.tokenParsed ?? {};
    const realmRoles: string[] = tp?.realm_access?.roles ?? [];
    const clientRoles: string[] = tp?.resource_access?.[clientId]?.roles ?? [];
    return [...realmRoles, ...clientRoles];
  }
}
