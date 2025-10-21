export const environment = {
  production: false,
  apiBase: 'http://localhost:8080',   // URL del backend Spring Boot
  keycloak: {
    url: 'http://localhost:8085',     // URL del tuo Keycloak
    realm: 'Booklet',
    clientId: 'booklet_client_frontend'
  }
};