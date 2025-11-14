import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  // Exclude parametric route from prerendering; render on server at request time
  { path: 'catalogo-personale', renderMode: RenderMode.Server },
  // Routes that hit protected APIs should not be prerendered
  { path: 'redazione', renderMode: RenderMode.Server },
  { path: 'carrello', renderMode: RenderMode.Server },
  // Prerender everything else
  { path: '**', renderMode: RenderMode.Prerender }
];
