export type Scaffale = 'LETTI' | 'DA_LEGGERE' | 'IN_LETTURA'; // adatta ai tuoi enum

export interface CatalogoPersonaleDTO {
  titolo: string;
  isbn: string;
  autore: string;
  scaffale: Scaffale;
  rating: number | null;
  recensione: string | null;
  addedAt: string; // ISO
}

export interface CatalogoPersonaleContainerDTO {
  utenteId: string;         // UUID
  username?: string | null; // opzionale
  totaleLibri: number;
  libri: CatalogoPersonaleDTO[];
}
