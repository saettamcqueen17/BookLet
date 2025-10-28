import Keycloak from 'keycloak-js';
import { InjectionToken } from '@angular/core';

export const KEYCLOAK = new InjectionToken<Keycloak>('KEYCLOAK');

export function keycloakFactory() {
  return new Keycloak({
    url: 'http://localhost:8085',
    realm: 'BookLet',
    clientId: 'booklet-frontend',
  });
}
