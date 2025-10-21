
export interface SchedaRedazione {
  isbn: string;
  categoria?: string;          // nel backend è "genere", tienilo allineato se serve
  recensione?: string | null;
  valutazioneRedazione?: number | null;
  dataInserimento?: string;    // ISO
  visibile: boolean;
}