import Keycloak from 'keycloak-js'

const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL || 'http://localhost:8082',
  realm: 'file-downloader-realm',
  clientId: 'file-downloader-client-public',
})

export default keycloak
