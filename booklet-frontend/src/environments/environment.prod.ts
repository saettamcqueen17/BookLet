export const environment = {
  production: true,
  apiBase: 'http://localhost:8080',   // URL backend in deploy
  keycloak: {
    url: 'http://localhost:8085',
    realm: 'booklet',
    clientId: 'booklet-frontend'
  }
};