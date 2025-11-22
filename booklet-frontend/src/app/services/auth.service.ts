import { Injectable, Inject, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import Keycloak, { KeycloakInstance, KeycloakInitOptions } from 'keycloak-js';
import { AuthStateService } from './AuthStatusService';

type UserInfo = { id?: string; username?: string; email?: string };

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly isBrowser: boolean;
  private kc?: KeycloakInstance;
  private initPromise?: Promise<boolean>;

  private readonly url = 'http://localhost:8085';
  private readonly realm = 'Booklet';
  private readonly clientId = 'booklet_client_frontend';

  private readonly TOK_KEY = 'kc_token';
  private readonly RTOK_KEY = 'kc_refreshToken';
  private readonly IDTOK_KEY = 'kc_idToken';

  private state = inject(AuthStateService);

  constructor(@Inject(PLATFORM_ID) platformId: Object) {
    this.isBrowser = isPlatformBrowser(platformId);

    if (this.isBrowser) {
      this.kc = new (Keycloak as any)({
        url: this.url,
        realm: this.realm,
        clientId: this.clientId
      }) as KeycloakInstance;

      this.kc.onAuthSuccess = () => {
        this.saveTokens();
        this.state.setLogged(true);
      };

      this.kc.onAuthRefreshSuccess = () => {
        this.saveTokens();
      };

      this.kc.onAuthLogout = () => {
        this.clearTokens();
        this.state.setLogged(false);
      };

      this.kc.onTokenExpired = () => {
        this.kc?.updateToken(30).then(() => this.saveTokens());
      };
    }
  }

  private loadStoredTokens() {
    if (!this.isBrowser) return {};
    return {
      token: sessionStorage.getItem(this.TOK_KEY) || undefined,
      refreshToken: sessionStorage.getItem(this.RTOK_KEY) || undefined,
      idToken: sessionStorage.getItem(this.IDTOK_KEY) || undefined
    };
  }

  private saveTokens() {
    if (!this.isBrowser || !this.kc) return;
    if (this.kc.token) sessionStorage.setItem(this.TOK_KEY, this.kc.token);
    if (this.kc.refreshToken) sessionStorage.setItem(this.RTOK_KEY, this.kc.refreshToken);
    if ((this.kc as any).idToken) sessionStorage.setItem(this.IDTOK_KEY, (this.kc as any).idToken);
  }

  private clearTokens() {
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
        idToken: stored.idToken,
      };

      this.initPromise = this.kc.init(opts)
        .then(authenticated => {
          if (authenticated) {
            this.saveTokens();
            this.state.setLogged(true);
          }
          return authenticated;
        })
        .catch(() => false);
    }

    return this.initPromise;
  }

  async isLoggedIn(): Promise<boolean> {
    if (!this.isBrowser || !this.kc) return false;

    // ⚠️ Prima assicuriamo l'init
    await this.ensureInit();

    // ⚠️ Poi proviamo a fare refresh token se scaduto
    try {
      await this.kc.updateToken(30);
      this.saveTokens();
    } catch {
      return false;
    }

    return !!this.kc.authenticated;
  }


  async getToken(): Promise<string | null> {
    if (!this.isBrowser || !this.kc) return null;

    const ok = await this.ensureInit();
    if (!ok) return null;

    try {
      await this.kc.updateToken(30);
      this.saveTokens();
    } catch {}

    return sessionStorage.getItem(this.TOK_KEY) ?? null;
  }

  login(redirectTo?: string) {
    if (!this.isBrowser || !this.kc) return Promise.resolve();
    const redirectUri = window.location.origin + (redirectTo || window.location.pathname);
    return this.kc.login({ redirectUri });
  }

  logout() {
    if (!this.isBrowser || !this.kc) return Promise.resolve();
    this.clearTokens();
    return this.kc.logout({ redirectUri: window.location.origin + '/' });
  }

  async getRolesAsync(): Promise<string[]> {
    const token = sessionStorage.getItem(this.TOK_KEY);
    if (!token) return [];

    const payload = JSON.parse(atob(token.split('.')[1]));

    const realmRoles = payload?.realm_access?.roles ?? [];
    const clientRoles = Object.values(payload?.resource_access ?? {})
      .flatMap((r: any) => r.roles ?? []);

    return [...realmRoles, ...clientRoles];
  }

  getUser(): UserInfo {
    const token = sessionStorage.getItem(this.TOK_KEY);
    if (!token) return {};
    const p = JSON.parse(atob(token.split('.')[1]));
    return {
      id: p.sub,
      username: p.preferred_username,
      email: p.email
    };
  }
}
